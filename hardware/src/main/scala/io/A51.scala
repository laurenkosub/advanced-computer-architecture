/*
 * A5/1 Protocol using LFSRs
 * author: Lauren Kosub s186193
 */

package io

import Chisel._
import patmos.Constants._
import ocp._

object A51 extends DeviceObject {

    def init(params: Map[String, String]) = {
    }

    def create(params: Map[String, String]) : A51 = { 
        Module(new A51())
    }

    trait Pins {}
}

class A51() extends CoreDevice() {

	override val io = new CoreDeviceIO() with A51.Pins  

    // establish OCP default response
    val masterReg = Reg(next = io.ocp.M)
   	val respReg = Reg(init = OcpResp.NULL)
   	respReg := OcpResp.NULL
   
    // variables and function declarations 
    val DEFAULT = "h1fabcd1f".U             // arbitrary default
    val FRAME = "b1110101011001111001011"   // agreed upon, public frame
    val SIZE1 = 19
    val SIZE2 = 22
    val SIZE3 = 23
    val CLKBIT1 = 8
    val CLKBIT2 = 10
    val DATA_WIDTH = 32

    val secKey1 = Reg(init = Bits(width = DATA_WIDTH)) // first DATA_WIDTH bits of 114 bit key
    val secKey2 = Reg(init = Bits(width = DATA_WIDTH)) // second DATA_WIDTH bits 114 bit key
    val secKey3 = Reg(init = Bits(width = DATA_WIDTH)) // third DATA_WIDTH bits of 114 bit key
    val secKey4 = Reg(init = Bits(width = 18)) // last 18 bits 114 bit key
    
    val frame = Reg(init = UInt(FRAME, 22))

    //val idle :: restart :: genKey :: encrypt :: Nil = Enum(UInt(), 4)
    //val stateReg = Reg(init = restart)

    def maj (x : Bits, y : Bits, z : Bits) = {
        (x & y) ^ (x & z) ^ (y & z)
    }

    // key is split up between two DATA_WIDTH bit registers to make it 64 bits
    // because patmos is DATA_WIDTH bit
    val secretBit = Reg(init = Bits(width = 1))
    val key1 = Reg(init = UInt(0, 32)) // first DATA_WIDTH bits of 64 bit key
    key1 := UInt("h1fabcd1f".U, 32)
    val key2 = Reg(init = UInt(0, 32)) // last DATA_WIDTH bits 64 bit key
    key2 := UInt("h1fabcd1f".U, 32)	

	// necessary lfsr's for protocol
    val lfsr19 = Module(new NLFSR(SIZE1))
    lfsr19.io.seed := key1(SIZE1-1, 0)
    lfsr19.io.inc := false.B
    lfsr19.io.frame := frame

   	val lfsr22 = Module(new NLFSR(22))
    lfsr22.io.seed := Cat(key1(31, SIZE1), key2(8, 0))
    lfsr22.io.inc := false.B
    lfsr22.io.frame := frame

   	val lfsr23 = Module(new NLFSR(23)) 
    lfsr23.io.seed := key2(31, 9)
    lfsr23.io.inc := false.B
    lfsr23.io.frame := frame
    
    val maj_bit = maj(  lfsr19.io.out(CLKBIT1), 
                        lfsr22.io.out(CLKBIT2), 
                        lfsr23.io.out(CLKBIT2))

    when (maj_bit === lfsr19.io.out(CLKBIT1)) {
        lfsr19.io.inc := true.B
    } .otherwise {
        lfsr19.io.inc := false.B 
    }
    
    when (maj_bit === lfsr22.io.out(CLKBIT2)) {
        lfsr22.io.inc := true.B
    } .otherwise {
        lfsr22.io.inc := false.B
    }
    
    when (maj_bit === lfsr23.io.out(CLKBIT2)) {
        lfsr23.io.inc := true.B
    } .otherwise {
        lfsr23.io.inc := false.B
    }

    // output one bit at a time
    secretBit := (  lfsr19.io.out(SIZE1 - 1) ^ 
                    lfsr22.io.out(SIZE2 - 1) ^ 
                    lfsr23.io.out(SIZE3 - 1)    )
    
    // OCP stuff
    when (io.ocp.M.Cmd === OcpCmd.WR) {
        secretBit := io.ocp.M.Data
    }

    when (io.ocp.M.Cmd === OcpCmd.WR || io.ocp.M.Cmd === OcpCmd.RD) {
    	respReg := OcpResp.DVA
   	}

   	io.ocp.S.Data := secretBit  //return one bit of secret at a time
   	io.ocp.S.Resp := respReg
}
