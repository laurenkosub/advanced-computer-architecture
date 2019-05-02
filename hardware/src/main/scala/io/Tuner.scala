/*
 * Seven Segment Hex Display
 */

package io

import Chisel._
import patmos.Constants._
import ocp._

object Tuner extends DeviceObject {
  var displayCnt = -1
  var polarity = -1

  def init(params: Map[String, String]) = {
      displayCnt = getPosIntParam(params, "displayCnt")
      polarity = getIntParam(params, "polarity")
  }

  def create(params: Map[String, String]) : Tuner = {
    Module(new Tuner(displayCnt, polarity))
  }

  trait Pins {
    val tunerPins = new Bundle() {
      val hexDisp = Vec.fill(displayCnt) {Bits(OUTPUT, 7)}
    }
  }
}

class Tuner(displayCnt : Int, polarity: Int) extends CoreDevice() {

    // Override
    override val io = new CoreDeviceIO() with Tuner.Pins

    // Decode hardware
    def sevenSegBCDDecode(data : Bits) : Bits = {
        val result = Bits(width = 7)
        result := Bits("b1000001")
        switch(data(3,0)){
            is(Bits("b0000")){ result := Bits("b1000000") } //0
            is(Bits("b0001")){ result := Bits("b1111001") }
            is(Bits("b0010")){ result := Bits("b0100100") }
            is(Bits("b0011")){ result := Bits("b0110000") }
            is(Bits("b0100")){ result := Bits("b0011001") }
            is(Bits("b0101")){ result := Bits("b0010010") }
            is(Bits("b0110")){ result := Bits("b0000010") }
            is(Bits("b0111")){ result := Bits("b1111000") }
            is(Bits("b1000")){ result := Bits("b0000000") }
            is(Bits("b1001")){ result := Bits("b0011000") }
            is(Bits("b1010")){ result := Bits("b0001000") } //a
            is(Bits("b1011")){ result := Bits("b0000011") } // b
            is(Bits("b1100")){ result := Bits("b1000110") } // c
            is(Bits("b1101")){ result := Bits("b0100001") } //d 
            is(Bits("b1110")){ result := Bits("b0000110") } // e 
            is(Bits("b1111")){ result := Bits("b0001110") } // f
        }
        if (polarity==0) { result } else { ~result }
    } 

/*
    def sevsegFreqToNote(data : Double) : Bits = {
        val result = Bits(width = 7)
        result := Bits("b1000001")

        // C
        when((data > 16 && data < 17) || (data > 32 && data < 35) ||
            (data > 65 && data < 69) || (data > 130 && data < 138) ||
            (data > 261 && data < 277) || (data > 523 && data < 554) ||
            (data > 1046 && data < 1108) || (data > 2093 && data < 2217) ||
            (data > 4186 && data < 4434)) { result := Bits("b1000110") }
        
        // D 
        when((data > 17 && data < 17) || (data > 32 && data < 35) ||
            (data > 65 && data < 69) || (data > 130 && data < 138) ||
            (data > 261 && data < 277) || (data > 523 && data < 554) ||
            (data > 1046 && data < 1108) || (data > 2093 && data < 2217) ||
            (data > 4186 && data < 4434)) { result := Bits("b0100001") }
       
        // E
        when((data > 16 && data < 17) || (data > 32 && data < 35) ||
            (data > 65 && data < 69) || (data > 130 && data < 138) ||
            (data > 261 && data < 277) || (data > 523 && data < 554) ||
            (data > 1046 && data < 1108) || (data > 2093 && data < 2217) ||
            (data > 4186 && data < 4434)) { result := Bits("b0000110") }

        // F
        when((data > 16 && data < 17) || (data > 32 && data < 35) ||
            (data > 65 && data < 69) || (data > 130 && data < 138) ||
            (data > 261 && data < 277) || (data > 523 && data < 554) ||
            (data > 1046 && data < 1108) || (data > 2093 && data < 2217) ||
            (data > 4186 && data < 4434)) { result := Bits("b0001110") }

        // G
        when((data > 16 && data < 17) || (data > 32 && data < 35) ||
            (data > 65 && data < 69) || (data > 130 && data < 138) ||
            (data > 261 && data < 277) || (data > 523 && data < 554) ||
            (data > 1046 && data < 1108) || (data > 2093 && data < 2217) ||
            (data > 4186 && data < 4434)) { result := Bits("b0011000") }

        // A
        when((data > 16 && data < 17) || (data > 32 && data < 35) ||
            (data > 65 && data < 69) || (data > 130 && data < 138) ||
            (data > 261 && data < 277) || (data > 523 && data < 554) ||
            (data > 1046 && data < 1108) || (data > 2093 && data < 2217) ||
            (data > 4186 && data < 4434)) { result := Bits("b0001000") }
        
        // B
        when((data > 16 && data < 17) || (data > 32 && data < 35) ||
            (data > 65 && data < 69) || (data > 130 && data < 138) ||
            (data > 261 && data < 277) || (data > 523 && data < 554) ||
            (data > 1046 && data < 1108) || (data > 2093 && data < 2217) ||
            (data > 4186 && data < 4434)) { result := Bits("b0000011") }

        }
    }
*/

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
    io.tunerPins.hexDisp := dispReg
    
}
