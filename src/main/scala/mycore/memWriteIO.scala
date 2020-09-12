package mycore

import chisel3._

import common.configurations._

class memWriteIO extends Bundle {
   val addr = Output(UInt(XLEN.W))
   val data = Output(UInt(XLEN.W))
   val mask = Output(UInt((XLEN/8).W))
   val en = Output(Bool())
}
