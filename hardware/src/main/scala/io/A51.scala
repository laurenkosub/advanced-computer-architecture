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

	override val io = new CoreDeviceIO() with A51.Pins

	// register for requests from OCP master and default response
    val masterReg = Reg(next = io.ocp.M)
   	val respReg = Reg(init = OcpResp.NULL)
   	respReg := OcpResp.NULL

	val key = Reg(init = UInt(1, 64))	// default key is 1
	when(io.ocp.M.Cmd === OcpCmd.RD) {
    	respReg := OcpResp.DVA
        key := io.ocp.M.Data
   	}

	// states 
	val step1 :: step2 :: step3 :: Nil = Enum(UInt(), 3)
	val state = Reg(init = step1)
	
	// declare vars	
	val done = (state === step3)
	val count = Reg(init = UInt(0,32))
   	val secretKey = Reg(init = UInt(1,114))
   	val secretBit = Reg(init = UInt(1,1))

	// necessary lfsr's for protocol
    val lfsr19 = Module(new NLFSR(19, 0x7ffff))  // holds 19 bits of key
   	val lfsr22 = Module(new NLFSR(22, 0x3fffff))  // holds 22 bits of key
   	val lfsr23 = Module(new NLFSR(23, 0x7fffff))  // holds 23 bits of key

    lfsr19.io.inc := false.B
    lfsr22.io.inc := false.B
    lfsr23.io.inc := false.B
	lfsr19.io.rst := 0x7ffff.U 
   	lfsr22.io.rst := 0x3fffff.U
   	lfsr23.io.rst := 0x7fffff.U 
    lfsr19.io.rst_b := false.B
    lfsr22.io.rst_b := false.B
    lfsr23.io.rst_b := false.B

	// declare vars    
	switch(state) {
		is(step1) {
			// get key and start the LFSRs
	    when(io.ocp.M.Cmd === OcpCmd.RD) {
    	    respReg := OcpResp.DVA
            key := Cat(io.ocp.M.Data(31,0), io.ocp.M.Data(31,0))
   	    }
			//key := Cat(masterReg.Data(31,0), masterReg.Data(31,0))
            lfsr19.io.rst_b := true.B
            lfsr22.io.rst_b := true.B
            lfsr23.io.rst_b := true.B
            lfsr19.io.rst := key(18,0)  // holds 19 bits of key
            lfsr22.io.rst := key(40,19)  // holds 22 bits of key
            lfsr23.io.rst := key(63,41)  // holds 23 bits of key
            lfsr19.io.rst_b := false.B
            lfsr22.io.rst_b := false.B
            lfsr23.io.rst_b := false.B    
            lfsr19.io.inc := true.B
            lfsr22.io.inc := true.B
            lfsr23.io.inc := true.B
            state := step2

            printf("GENERATING SECRET: ");
		}
		is (step2) {
			// calculate bit stream
			val maj = lfsr19.io.out(8) + lfsr22.io.out(10) + lfsr23.io.out(10)
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
			    	printf("ERROR");
			}

		    	// generate the 114-bit stream
			when (count < UInt(114)) {
			    secretBit := (lfsr19.io.out(18) ^ lfsr22.io.out(21) ^ lfsr23.io.out(22))
			    printf("%d", secretBit);
                secretKey(count) := secretBit
			    count := count + UInt(1)
                
                
			} .otherwise {
			    //stateReg := finish
			    printf("\nFINISHED KEY GEN\n");
			    when (secretKey =/= key) { state := step3 }
                printf("SECRET KEY %x%x: ", secretKey(63,32), secretKey(31,0)); 
			}
		}
		is (step3) {
			// return secret key and reset vars
			key := Reg(init = UInt(1,64))
			state := step1
		}
	}  

    // -------------------------- OCP ----------------------------------
 	
    // OCP stuff
	when(io.ocp.M.Cmd === OcpCmd.WR) {
    	respReg := OcpResp.DVA
   	}

   	io.ocp.S.Resp := respReg
   	io.ocp.S.Data := secretBit  //return one bit of secret at a time
}
