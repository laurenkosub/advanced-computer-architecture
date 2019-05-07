/*
 * 16 bit LFSR 
 * author: Lauren Kosub s186193
 */

package io

import Chisel._
import patmos.Constants._
import ocp._

object LFSR extends DeviceObject {

    def init(params: Map[String, String]) = {}

    def create(params: Map[String, String]) : LFSR = { 
        Module(new LFSR())
    }

    trait Pins {}
}

class LFSR() extends CoreDevice() {
    
  // 16 bit
  val countReg = Reg(init = UInt(1, 16))
  countReg := Cat(countReg(0)^countReg(2)^countReg(3)^countReg(5),countReg(15,1))
  
  when (io.ocp.M.Cmd === OcpCmd.WR) {
    countReg := io.ocp.M.Data 
  }
  
  val respReg = Reg(init = OcpResp.NULL)
  respReg := OcpResp.NULL
  when(io.ocp.M.Cmd === OcpCmd.RD || io.ocp.M.Cmd === OcpCmd.WR) {
    respReg := OcpResp.DVA
  }
  
  io.ocp.S.Data := countReg  
  io.ocp.S.Resp := respReg
}
