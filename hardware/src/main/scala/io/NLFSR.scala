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
        }.elsewhen((n == 32).B) {
            feedback := ~(res(1) ^ res(2) ^ res(22))
        }.otherwise { feedback := ~(res(1)^res(2)^res(4)^res(15)) }
        
        val next_res = Cat(feedback,res(n-1, 1))
        res := next_res
    }

    io.out := res
}
