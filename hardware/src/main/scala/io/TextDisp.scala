/*
 * Seven Segment Hex Display for Magic 8 ball program
 * Not your average seven segment hex display
 * author: Lauren Kosub s186193
 */

package io

import Chisel._
import patmos.Constants._
import ocp._

object TextDisp extends DeviceObject {
  var displayCnt = -1
  var polarity = -1

  def init(params: Map[String, String]) = {
      displayCnt = getPosIntParam(params, "displayCnt")
      polarity = getIntParam(params, "polarity")
  }

  def create(params: Map[String, String]) : TextDisp = {
    Module(new TextDisp(displayCnt, polarity))
  }

  trait Pins {
    val textDispPins = new Bundle() {
      val hexDisp = Vec.fill(displayCnt) {Bits(OUTPUT, 7)}
    }
  }
}

class TextDisp(displayCnt : Int, polarity: Int) extends CoreDevice() {

    // Override
    override val io = new CoreDeviceIO() with TextDisp.Pins

    // Decode hardware
    def sevenSegBCDDecode(data : Bits) : Bits = {
        val result = Bits(width = 7)
        result := Bits("b1000001")
        switch(data(3,0)){
            is(Bits("b0000")){ result := Bits("b1000000") } //0
            is(Bits("b0001")){ result := Bits("b1111001") } 
            is(Bits("b0010")){ result := Bits("b0010001") } // change 2 to Y
            is(Bits("b0011")){ result := Bits("b1001000") } // change 3 to n
            is(Bits("b0100")){ result := Bits("b0000111") } // change 4 to t
            is(Bits("b0101")){ result := Bits("b0010010") }
            is(Bits("b0110")){ result := Bits("b1110111") } // change 6 to _ 
            is(Bits("b0111")){ result := Bits("b1001100") } // change 7 to r
            is(Bits("b1000")){ result := Bits("b0000000") }  
            is(Bits("b1001")){ result := Bits("b1000001") } // change 9 to u
            is(Bits("b1010")){ result := Bits("b0001000") } //a
            is(Bits("b1011")){ result := Bits("b0000011") } // b
            is(Bits("b1100")){ result := Bits("b1000110") } // c
            is(Bits("b1101")){ result := Bits("b0100001") } //d 
            is(Bits("b1110")){ result := Bits("b0000110") } // e 
            is(Bits("b1111")){ result := Bits("b0001110") } // f
        }
        if (polarity==0) { result } else { ~result }
    } 

    // Master register
    val masterReg = Reg(next = io.ocp.M)
    
    // Default response
    val respReg = Reg(init = OcpResp.NULL)
    respReg := OcpResp.NULL

    val dataReg = Reg(init = Bits(0, width = DATA_WIDTH))

    // Display register
    val dispReg = RegInit(Vec.fill(displayCnt){Bits(polarity, width = 7)})

    // Decoded master data
    val decodedMaster = Bits(width = 7)

    decodedMaster := sevenSegBCDDecode(masterReg.Data)

    // Read/Write seven segment displays
    for(i <- 0 to displayCnt-1 by 1){
        when(masterReg.Addr(4,2) === i.U){
            when(masterReg.Cmd === OcpCmd.WR){
                when(masterReg.Data(7)){
                    dispReg(i) := masterReg.Data(6, 0) //Drive display seven segments directly from OCP
                } .otherwise {
                    dispReg(i) := decodedMaster //Drive display through decoded hex number from OCP
                }
            }
            dataReg := dispReg(i)
        }
    }

    when(masterReg.Cmd === OcpCmd.WR || masterReg.Cmd === OcpCmd.RD){
        respReg := OcpResp.DVA
    }

    // Connections to master
    io.ocp.S.Resp := respReg
    io.ocp.S.Data := dataReg

    // Connections to IO
    io.textDispPins.hexDisp := dispReg
    
}
