package sim

import chisel3._

class goodTrapIO extends Bundle {
  val nemu  = Output(Bool())
}