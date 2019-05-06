/*
 * A5/1 Protocol using LFSRs
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

    override val io = new CoreDeviceIO() with A51.Pins  

    // register for requests from OCP master
    val masterReg = Reg(next = io.ocp.M)

    // get key from master
    val key = Reg(init = UInt(0, 64))
    key := masterReg.Data

    printf("key: %x", key);

    val count = 0.U

    // states
    val restart :: idle :: running :: finish :: Nil = Enum(UInt(), 4)
    val stateReg = Reg(init = idle)
    val secretKey = Reg(init = UInt(0,114))

    // necessary lfsr's for protocol
    val lfsr19 = Module(new NLFSR(19, key(18,0).litValue()))  // holds 19 bits of key
    val lfsr22 = Module(new NLFSR(22, key(40,19).litValue()))  // holds 22 bits of key
    val lfsr23 = Module(new NLFSR(23, key(63,41).litValue()))  // holds 23 bits of key
    lfsr19.io.inc := false.B
    lfsr22.io.inc := false.B        
    lfsr23.io.inc := false.B

/*
    when(stateReg === restart) {
        val lfsr19 = Module(new NLFSR(19, key(18,0)))  // holds 19 bits of key
        val lfsr22 = Module(new NLFSR(22, key(40,19)))  // holds 22 bits of key
        val lfsr23 = Module(new NLFSR(23, key(63,41)))  // holds 23 bits of key
    }
*/
    when(stateReg === idle) {
        lfsr19.io.inc := false.B
        lfsr22.io.inc := false.B
        lfsr23.io.inc := false.B
        stateReg := running
    }

    when (stateReg === running) {

        // clock register logic
    val maj = lfsr19.io.out(8) + lfsr22.io.out(10) + lfsr23.io.out(10)
    when (maj > 1.U) {
        when (lfsr19.io.out(8) === 1.U) {
            lfsr19.io.inc := true.B
        }.otherwise {
            lfsr19.io.inc := false.B
        }
        when (lfsr22.io.out(10) === 1.U) {
            lfsr22.io.inc := true.B
        }.otherwise {
            lfsr22.io.inc := false.B
        }
        when (lfsr23.io.out(10) === 1.U) {
            lfsr23.io.inc := true.B
        }.otherwise {
            lfsr23.io.inc := false.B
        }
    } .otherwise {
        when (lfsr19.io.out(8) === 0.U) {
            lfsr19.io.inc := true.B
        }.otherwise {
            lfsr19.io.inc := false.B
        }
        when (lfsr22.io.out(10) === 0.U) {
            lfsr22.io.inc := true.B
        }.otherwise {
            lfsr22.io.inc := false.B
        }
        when (lfsr23.io.out(10) === 0.U) {
            lfsr23.io.inc := true.B
        }.otherwise {
            lfsr23.io.inc := false.B
        }
    }
        
        // generate the 114-bit stream
        when (count < 114.U) {
            secretKey(count) := (lfsr19.io.out(18) ^ lfsr22.io.out(21) ^ lfsr23.io.out(22))
            count := count + 1.U
        } .otherwise {
            stateReg := finish
        }
    }
    

        // default response
        val respReg = Reg(init = OcpResp.NULL)
        respReg := OcpResp.NULL

            // OCP stuff
        when (io.ocp.M.Cmd === OcpCmd.WR) {
            secretKey := io.ocp.M.Data 
        }

        when(io.ocp.M.Cmd === OcpCmd.RD || io.ocp.M.Cmd === OcpCmd.WR) {
           respReg := OcpResp.DVA
        }

        io.ocp.S.Resp := respReg
        io.ocp.S.Data := secretKey // output keystream 
}
