/*
 * Copyright: 2017, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * Schedules for the S4NOC, as described in:
 * 
 * Florian Brandner and Martin Schoeberl,
 * Static Routing in Symmetric Real-Time Network-on-Chips,
 * In Proceedings of the 20th International Conference on Real-Time
 * and Network Systems (RTNS 2012), 2012, 61-70
 * 
 * Available at:
 * https://github.com/t-crest/s4noc/tree/master/noc/vhdl/generated
 */

package s4noc_twoway

import Const._
import scala.util.Random


object Schedule {

  def getSchedule(n: Int, inverted : Boolean, nodeIndex: Int) = {
    
      val temps = n match {
        case 2 => ScheduleTable.FourNodes
        case 3 => ScheduleTable.NineNodes
        case 4 => ScheduleTable.SixTeenNodes
        case _ => throw new Error("Currently only 2x2, 3x3, and 4x4 NoCs supported, you requested: "+n+"x"+n)
      }
    
    def invertMap(c: Char) = { // map function from original to inverted schedule 
      c match {case 'w' => 'e' case 'e' => 'w' case 'n' => 's' case 's' => 'n' case _ => c}
    }
    var s = temps
    if(inverted){
      s = s.map(c => invertMap(c)) // if inverted is high the schedule should be inverted
   }
    
    def port(c: Char) = {
      c match {
        case 'n' => NORTH
        case 'e' => EAST
        case 's' => SOUTH
        case 'w' => WEST
        case 'l' => LOCAL
        case ' ' => if (inverted) SOUTH else 0
      }
    }

    def nextFrom(c: Char) = {
      c match {
        case 'n' => 's'
        case 'e' => 'w'
        case 's' => 'n'
        case 'w' => 'e'
        case 'l' => 'x' // no next for the last
        case ' ' => 'l' // stick to l on empty/waiting slots
      }
    }

    val split = s.split('|')
    val len = split.reduceLeft((a, b) => if (a.length > b.length) a else b).length
    val schedule = new Array[Array[Int]](len)
    val valid = new Array[Boolean](len)

    for (i <- 0 until len) {
      schedule(i) = new Array[Int](NR_OF_PORTS)
    }

    if(inverted){
      for(i <- 0 until len){
        for(j <- 0 until NR_OF_PORTS){
          schedule(i)(j) = SOUTH
        }
      }
    }
    for (i <- 0 until split.length) {
      var from = 'l'
      for (j <- 0 until split(i).length) {
        val to = split(i)(j)
        if (to != ' ') {
          schedule(j)(port(to)) = port(from)
          from = nextFrom(to)
        }
      }
    }
    var line = 0
    for (i <- 0 until len - 1) {
      valid(i) = split(line)(i) != ' '
      if (valid(i)) line += 1
    }
    println("Schedule is " + schedule.length + " clock cycles")
    // The following part generates a 'timeslot-to-recieve-node' look-up table, in the form of an array of integers.
    // Index into array specifies target node, and returned value is the timeslot which the package should be transmitted in.
    var startNode = nodeIndex
    var timeSlot = 0
    val timeSlotToNode = new Array[Int](n*n)
    
    for (i <- 0 until (n*n)-1){
      startNode = nodeIndex
      timeSlot = 0;
      for (j <- 0 until split(i).length()){
        split(i)(j) match {
          case ' ' => startNode += 0
                      timeSlot += 1
          
          case 'e' => if ((startNode + 1) % n == 0) {
                        startNode -= (n-1)
                      }else{
                        startNode += 1
                      }
          
          case 'w' => if(startNode % n == 0){
                        startNode += (n-1)
                      }else{
                        startNode -= 1
                      }
          
          case 'n' => if (startNode >= 0 && startNode < n){
                        startNode += n * (n - 1)
                      }else{
                        startNode -= n
                      }
          
          case 's' => if (startNode >= n * (n-1) && startNode < n * n){
                        startNode -= n * (n - 1)
                      }else{
                        startNode += n
                      }
          case 'l' => 
          case _ => println("invalid direction symbol")
        }
      }
      timeSlotToNode(startNode) = timeSlot
    }


    (schedule, valid, timeSlotToNode, len-split(0).length()-1)
  }

  /* A 2x2 schedule is as follows:
ne
  n
   e
 */

  def gen2x2Schedule() = {
    Array(Array(LOCAL, 0, 0, 0, 0), // P1: enter from local and exit to north register
      Array(0, SOUTH, 0, 0, 0), // P1: enter from south and exit to east register
      Array(LOCAL, 0, 0, 0, WEST), // P2: local to north, P1: from west to local
      Array(0, LOCAL, 0, 0, SOUTH), // P3: local to east, P2: south to local
      Array(0, 0, 0, 0, WEST)) // P3: from west to local
    // The last drain from west to local increases the schedule length by 1,
    // but could be overlapped.
    // Which means having it in the first slot, as there is no exit in the first slot.
  }

  def genRandomSchedule(slen: Int) = {
    val schedule = new Array[Array[Int]](slen)
    for (i <- 0 until slen) {
      val oneSlot = new Array[Int](NR_OF_PORTS)
      for (j <- 0 until NR_OF_PORTS) {
        oneSlot(j) = Random.nextInt(5)
      }
      schedule(i) = oneSlot
    }
    schedule
  }

  def main(args: Array[String]): Unit = {
    print(getSchedule(2,false,0)) // not sure what the purpose of this is -> guesing we won't need the timeslot to node look up table -> nodeIndex = 0
  }

}
