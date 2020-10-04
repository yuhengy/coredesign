package mycore
package preInstFetch

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._

import common.constants._
import common.configurations._
import common.memReadIO

class preInstFetchTOP extends Module
{
  val io = IO(new Bundle{
  //preifToIfData
    val preifToIfDataIO = Output(new preifToIfDataIO)
  //preifToIfCtrl
    val preifToIfCtrlIO = Decoupled(new Bundle{})

  //exeToIfFeedback
    val brjmpTarget = Input(UInt(XLEN.W))
    val jmpRTarget  = Input(UInt(XLEN.W))
    val PCSel       = Input(UInt(PCSel_w.W))

  //toRam
    val instReadIO = new Bundle{
      val addr = Output(UInt(XLEN.W))
      val en = Output(Bool())
    }
  })

//--------------instFetch global status start--------------
  val regPC           = RegInit(UInt(XLEN.W), ADDR_START.U - 8.U)
//^^^^^^^^^^^^^^instFetch global status end^^^^^^^^^^^^^^

//--------------PC update start--------------
//input
  val exceptionTarget = Wire(UInt(XLEN.W))  //TODO: this does not have signal now
  exceptionTarget := 0.U
  val stall = Wire(Bool())
//private
  val PC4             = Wire(UInt(XLEN.W))
  val PCNext          = Wire(UInt(XLEN.W))

  when (!stall) {
    regPC := PCNext
  }
  PCNext := Mux(io.PCSel === PC_4,      PC4,
            Mux(io.PCSel === PC_BRJMP,  io.brjmpTarget,
            Mux(io.PCSel === PC_JALR,   io.jmpRTarget,
          /*Mux(io.PCSel === PC_EXC,*/  exceptionTarget)))

  PC4 := regPC + 4.asUInt(XLEN.W)
//^^^^^^^^^^^^^^PC update end^^^^^^^^^^^^^^

//--------------inst read start--------------
//output
  val resetDelay0 = RegInit(false.B)
  val resetDelay1 = RegNext(resetDelay0, true.B)
  val resetDelay2 = RegNext(resetDelay1, true.B)
//private
  io.instReadIO.addr := PCNext
  io.instReadIO.en   := !resetDelay1
//^^^^^^^^^^^^^^inst read end^^^^^^^^^^^^^^

//--------------stall&kill start--------------
  stall := !io.preifToIfCtrlIO.ready

  io.preifToIfCtrlIO.valid := !resetDelay1
//^^^^^^^^^^^^^^stall&kill end^^^^^^^^^^^^^^

//--------------io.output start--------------
  io.preifToIfDataIO.PC   := PCNext
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

}
