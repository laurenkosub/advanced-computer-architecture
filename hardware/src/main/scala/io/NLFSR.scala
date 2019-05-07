/*
 * n bit LFSR 
 * author: Lauren Kosub s186193
 */

package io

import Chisel._
import patmos.Constants._
import java.math.BigInteger

/* n = number of bits */
class NLFSR(n : Int, start : BigInt) extends Module() {

    val io = IO(new Bundle {
        val inc = Input(Bool())
        val rst_b = Input(Bool())
        val rst = Input(UInt(width = n))
        val out = Output(UInt(width = n))
    })
    
    // 16 bit
    val feedback = RegInit(UInt(1, 1))
    val res = RegInit(UInt(Math.abs(start.toInt), 64))

    when(io.rst_b) {
        res := io.rst
    }  
 
    when (io.inc) { 
        // tap values determined via table @ 
        // https://www.embedded.com/print/4015086
        
        when((n == 8).B) {
            feedback := ~(res(1)^res(2)^res(3)^res(7))
        }.elsewhen((n == 16).B) {
            feedback := ~(res(1)^res(2)^res(4)^res(15))
        }.elsewhen((n == 19).B) {
            feedback := ~(res(0)^res(4)^res(1)^res(18))
        }.elsewhen((n == 22).B) {
            feedback := ~(res(0)^res(21))
        }.elsewhen((n == 23).B) {
            feedback := ~(res(4)^res(22))
        }.otherwise { feedback := res(0) }
        
        val next_res = Cat(feedback,res(n-1, 1))
        res := next_res
    }

    io.out := res
}
