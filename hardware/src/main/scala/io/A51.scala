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

// given 64 bit session key
class A51() extends CoreDevice() {

	//override val io = new CoreDeviceIO() with A51.Pins

    // hardcode key for now ...
    val secretBit = Reg(init = UInt(1, 1))
    val key = Reg(init = UInt(0, 64))

    // use LFSR to generate random 64 bit key because why not
    val lfsr_key = Module(new NLFSR(64))
    lfsr_key.io.inc := true.B
    key := lfsr_key.io.out
    
    //key := Cat(UInt("h1fabcd1f".U, 32), UInt("h1facbd1f".U, 32))
    
    // establish OCP default response
    val masterReg = Reg(next = io.ocp.M)
   	val respReg = Reg(init = OcpResp.NULL)
   	respReg := OcpResp.NULL
	
	// necessary lfsr's for protocol
    val lfsr19 = Module(new NLFSR(19))
    lfsr19.io.seed := key(18,0)

   	val lfsr22 = Module(new NLFSR(22))
    lfsr22.io.seed := key(40,19)

   	val lfsr23 = Module(new NLFSR(23)) 
    lfsr23.io.seed := key(63,41)

    lfsr19.io.inc := false.B
    lfsr22.io.inc := false.B
    lfsr23.io.inc := false.B
  
	val maj = UInt(lfsr19.io.out(8)) + UInt(lfsr22.io.out(10)) + UInt(lfsr23.io.out(10))

	when (maj === 2.U || maj === 3.U) {
        when (lfsr19.io.out(8) === 0.U) {
            lfsr19.io.inc := false.B
        }.otherwise {
            lfsr19.io.inc := true.B
        }
        when (lfsr22.io.out(10) === 0.U) {
            lfsr22.io.inc := false.B
        }.otherwise {
            lfsr22.io.inc := true.B
        }
        when (lfsr23.io.out(10) === 0.U) {
            lfsr23.io.inc := false.B
        }.otherwise {
            lfsr23.io.inc := true.B
        }
    } .elsewhen(maj === 1.U || maj === 0.U) {
        when (lfsr19.io.out(8) === 1.U) {
            lfsr19.io.inc := false.B
        }.otherwise {
            lfsr19.io.inc := true.B
        }
        when (lfsr22.io.out(10) === 1.U) {
            lfsr22.io.inc := false.B
        }.otherwise {
            lfsr22.io.inc := true.B
        }
        when (lfsr23.io.out(10) === 1.U) {
            lfsr23.io.inc := false.B
        }.otherwise {
            lfsr23.io.inc := true.B
        }
    }.otherwise {
        printf("ERROR IN DETERMINING MAJORITY BIT");
    }
    
    // output one bit at a time
    secretBit := (lfsr19.io.out(18) ^ lfsr22.io.out(21) ^ lfsr23.io.out(22))
    
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
