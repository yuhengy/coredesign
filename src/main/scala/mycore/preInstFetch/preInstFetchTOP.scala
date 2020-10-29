package mycore
package preInstFetch

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

import common.constants._
import common.configurations._
import common.memReadIO

class preInstFetchTOP extends Module
{
  val io = IO(new Bundle{
  //preifToIfData
    val outDataIO = Output(new preifToIfDataIO)
  //preifToIfCtrl
    val outCtrlIO = Decoupled(new Bundle{})

  //exeToIfFeedback
    val brjmpTarget = Input(UInt(XLEN.W))
    val jmpRTarget  = Input(UInt(XLEN.W))
    val PCSel       = Input(UInt(PCSel_w.W))

  //toRam
    val instReadIO = new Bundle{
      val reqReady = Input(Bool())
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

//--------------state machine start--------------
//output
  object stateEnum extends ChiselEnum {
    val reset, canSend = Value
  }
  val state = RegInit(stateEnum.reset)

//private
  switch (state) {
    is (stateEnum.reset) {
      state := stateEnum.canSend
    }
    is (stateEnum.canSend) {
      state := stateEnum.canSend
    }
  }
//^^^^^^^^^^^^^^state machine end^^^^^^^^^^^^^^

//--------------control signal start--------------
  stall := !io.outCtrlIO.ready || !io.instReadIO.reqReady
  //TODO: this stall is different from stall in other stage 
  //      in the sense that it include !io.outCtrlIO.ready.
  //      This is becuase this stall is used by reg update in this stage itself
  //      This need be more consistent in future
  
  io.outCtrlIO.valid := state === stateEnum.canSend
//^^^^^^^^^^^^^^stall&kill end^^^^^^^^^^^^^^

//--------------control signal start--------------
//preifToIfData
  io.outDataIO.PC        := PCNext
  io.outDataIO.addrAlign := PCNext(addrAlign_w-1, 0)

//toRam
  io.instReadIO.addr := Cat(PCNext(XLEN-1, addrAlign_w), Fill(addrAlign_w, 0.U))
  io.instReadIO.en   := state === stateEnum.canSend
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

}
