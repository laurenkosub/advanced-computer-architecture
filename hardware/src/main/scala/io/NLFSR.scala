/*
 * n bit LFSR 
 * author: Lauren Kosub s186193
 */

package io

import Chisel._
import patmos.Constants._
import java.math.BigInteger

/* n = number of bits */
class NLFSR(n : Int) extends Module() {

    /*  inc determines whether or not LFSR advances
     *   seed represents the init value for the LFSR
     *   out is the LFSR's output 
     */
    val io = IO(new Bundle {
        val inc = Input(Bool())
        val seed = Input(UInt(width = n))
        val out = Output(UInt(width = n))
    })
    
    // 16 bit
    val feedback = RegInit(UInt(0, 1))
    val res = RegInit(io.seed)

    when (io.inc) { 
        // tap values determined via table @ 
        // https://www.embedded.com/print/4015086
        
        when((n == 2).B) {
            feedback := res(1)^res(0)
	    }.elsewhen((n == 3).B) {
	        feedback := res(0)^res(2)
        }.elsewhen((n == 4).B) {
            feedback := res(0)^res(3)
        }.elsewhen((n == 5).B) {
            feedback := res(1)^res(4)
        }.elsewhen((n == 6).B) {
            feedback := res(0)^res(5)
        }.elsewhen((n == 7).B) {
            feedback := res(0)^res(6)
        }.elsewhen((n == 8).B) {
            feedback := res(1) ^ res(2) ^ res(3) ^ res(7)
	    }.elsewhen((n == 9).B) {
	        feedback := res(3)^res(8)
        }.elsewhen((n == 10).B) {
            feedback := res(9)^res(2)
	    }.elsewhen((n == 11).B) {
	        feedback := res(1)^res(10)
        }.elsewhen((n == 12).B) {
            feedback := res(0)^res(3)^res(5)^res(11)
        }.elsewhen((n == 13).B) {
            feedback := res(0)^res(2)^res(3)^res(12)
        }.elsewhen((n == 14).B) {
            feedback := res(0)^res(2)^res(4)^res(13)
        }.elsewhen((n == 15).B) {
            feedback := res(0)^res(14)
        }.elsewhen((n == 16).B) {
            feedback := res(1) ^ res(2) ^ res(4) ^ res(15)        
	    }.elsewhen((n == 17).B) {
            feedback := res(2)^res(16)
        }.elsewhen((n == 18).B) {
            feedback := res(6)^res(17)
        }.elsewhen((n == 19).B) {
            feedback := res(0)^res(1)^res(4)^res(18)
        }.elsewhen((n == 20).B) {
            feedback := res(2) ^ res(19)
	    }.elsewhen((n == 21).B) {
	        feedback := res(1)^res(20)
	    }.elsewhen((n == 22).B) {
            feedback := res(0) ^ res(21)
        }.elsewhen((n == 23).B) {
            feedback := res(4) ^ res(22)
        }.elsewhen((n == 24).B) {
            feedback := res(0)^res(2)^res(3)^res(23)
        }.elsewhen((n == 25).B) {
            feedback := res(2)^res(24)
        }.elsewhen((n == 26).B) {
            feedback := res(1)^res(5) ^ res(25) ^ res(0)
        }.elsewhen((n == 27).B) {
            feedback := res(0) ^ res(1) ^ res(4) ^res(26)
        }.elsewhen((n == 28).B) {
            feedback := res(2)^res(27)
        }.elsewhen((n == 29).B) {
            feedback := res(1)^res(28)
        }.elsewhen((n == 30).B) {
            feedback := res(0)^res(3)^res(5)^res(29)
        }.elsewhen((n == 31).B) {
            feedback := res(2)^res(30)
        }.elsewhen((n == 32).B) {
            feedback := res(1) ^ res(5) ^ res(6) ^ res(31)
        }.otherwise { 
            feedback := res(1)^res(2)^res(4)^res(15) //16 bit default
        }
        
        val next_res = Cat(feedback,res(n-1, 1))
        res := next_res
    }

    io.out := res
}
