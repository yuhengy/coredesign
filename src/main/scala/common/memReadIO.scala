package common

import chisel3._

import common.configurations._

class memReadIO extends Bundle {
  val reqReady = Input(Bool())
  val addr = Output(UInt(XLEN.W))
  val en = Output(Bool())

  val respValid = Input(Bool())
  val data = Input(UInt(XLEN.W))
}
