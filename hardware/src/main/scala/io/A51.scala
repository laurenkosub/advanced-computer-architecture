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

	// states 
	val step1 :: step2 :: step3 :: Nil = Enum(UInt(), 3)
	val state = Reg(init = step1)
	
	// declare vars	
	val done = (state === step3)
	val count = Reg(init = UInt(0,8))
	val key = Reg(init = UInt(1, 64))	// default key is alL 1s
   	val secretKey = Reg(init = UInt(1,114))

	// necessary lfsr's for protocol
    val lfsr19 = Module(new NLFSR(19, 0x7ffff))  // holds 19 bits of key
    val lfsr22 = Module(new NLFSR(22, 0x3fffff))  // holds 22 bits of key
    val lfsr23 = Module(new NLFSR(23, 0x7fffff))  // holds 23 bits of key

    // lfsr defaults
	lfsr19.io.inc := false.B
	lfsr22.io.inc := false.B
	lfsr23.io.inc := false.B

	lfsr19.io.rst_b := false.B
	lfsr22.io.rst_b := false.B
	lfsr23.io.rst_b := false.B

	lfsr19.io.rst := 0x7ffff.U
	lfsr22.io.rst := 0x3fffff.U
	lfsr23.io.rst := 0x7fffff.U
	
    // declare vars    
	switch(state) {
		is (step1) {
			// get key
			 key := Cat(masterReg.Data(31,0), masterReg.Data(31,0)) 
			 when (key =/= 0.U) {
			 	state := step2
                
                lfsr19.io.rst_b := true.B
                lfsr19.io.rst_b := true.B
                lfsr19.io.rst_b := true.B

                lfsr19.io.rst := key(18,0)  // holds 19 bits of key
    	        lfsr22.io.rst := key(40,19)  // holds 22 bits of key
    	        lfsr23.io.rst := key(63,41) // holds 23 bits of key

                lfsr19.io.rst_b := false.B
                lfsr19.io.rst_b := false.B
                lfsr19.io.rst_b := false.B
			 }
		}
		is (step2) {
            printf("SECRET KEY IS: ");
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
			    secretKey(count) := (lfsr19.io.out(18) ^ lfsr22.io.out(21) ^ lfsr23.io.out(22))
                printf("%d", (lfsr19.io.out(18)^lfsr22.io.out(21)^lfsr23.io.out(22)))
			    count := count + UInt(1)
			} .otherwise {
			    //stateReg := finish
			    printf("\nFINISHED KEY GEN\n");
                count := UInt(0)
			    state := step3
			}
		}
		is (step3) {
            Thread.sleep(1000)
			// return secret key and reset vars
			key := Reg(init = UInt(1,64))
			state := step1
		}
	}  

    // -------------------------- OCP ----------------------------------
 	// default response
    val respReg = Reg(init = OcpResp.NULL)
   	respReg := OcpResp.NULL

    // OCP stuff
   	when (io.ocp.M.Cmd === OcpCmd.WR && done) {
       	secretKey := io.ocp.M.Data 
   	}

	when(io.ocp.M.Cmd === OcpCmd.RD || io.ocp.M.Cmd === OcpCmd.WR) {
     	respReg := OcpResp.DVA
   	}
    
   	io.ocp.S.Resp := respReg
   	io.ocp.S.Data := secretKey // output keystream    
}
