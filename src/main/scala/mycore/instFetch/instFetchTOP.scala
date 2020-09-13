package mycore
package instFetch

import chisel3._

import common.constants._
import common.configurations._
import common.memReadIO

class instFetchTOP extends Module
{
  io = IO(new Bundle{
    val PCSel       = Input(UInt(PCSel_w.W))
    val instReadIO  = new memReadIO
    val ifToDecPC   = Output(UInt(XLEN.W))
    val ifToDecInst = Output(UInt(WID_INST.W))
  })

//--------------instFetch global status start--------------
  val regPC           = RegInit(UInt(XLEN.W), ADDR_START.U) 
//^^^^^^^^^^^^^^instFetch global status end^^^^^^^^^^^^^^

//--------------PC update start--------------
//input
  val brjmpTarget     = Wire(UInt(XLEN.W))
  val jmpRTarget      = Wire(UInt(XLEN.W))
  val exceptionTarget = Wire(UInt(XLEN.W))
//private
  val PC4             = Wire(UInt(XLEN.W))
  val PCNext          = Wire(UInt(XLEN.W))

  when (true.B) {
    regPC := PCNext
  }
  PCNext := Mux(io.PCSel === PC_4,      PC4,
            Mux(io.PCSel === PC_BRJMP,  brjmpTarget,
            Mux(io.PCSel === PC_JALR,   jmpRTarget,
          /*Mux(io.PCSel === PC_EXC,*/  exceptionTarget)))

  PC4 := regPC + 4.asUInt(XLEN.W)
//^^^^^^^^^^^^^^PC update end^^^^^^^^^^^^^^

//--------------inst read start--------------
  io.instReadIO.addr := regPC
  io.instReadIO.en   := true.B
//^^^^^^^^^^^^^^inst read end^^^^^^^^^^^^^^

//--------------io.output start--------------
  io.ifToDecPC   := regPC
  io.ifToDecInst := io.instReadIO.data  //TODO: inst should be 32
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

}
