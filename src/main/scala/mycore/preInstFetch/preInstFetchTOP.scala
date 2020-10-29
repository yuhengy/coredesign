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

  //exeOutKill
    val exeOutKill  = Input(Bool())

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

//--------------PC next start--------------
//input
  val exceptionTarget = Wire(UInt(XLEN.W))  //TODO: this does not have signal now
  exceptionTarget := 0.U
  val donotSend = Wire(Bool())
//private
  val PC4             = Wire(UInt(XLEN.W))
  val PCNext          = Wire(UInt(XLEN.W))

  PCNext := Mux(io.PCSel === PC_4,      PC4,
            Mux(io.PCSel === PC_BRJMP,  io.brjmpTarget,
            Mux(io.PCSel === PC_JALR,   io.jmpRTarget,
          /*Mux(io.PCSel === PC_EXC,*/  exceptionTarget)))

  PC4 := regPC + 4.asUInt(XLEN.W)
//^^^^^^^^^^^^^^PC next end^^^^^^^^^^^^^^

//--------------PC update start--------------
//input
  object stateEnum extends ChiselEnum {
    val reset, regIsUpdated, resultIsBuffered = Value
  }
  val state = RegInit(stateEnum.reset)

//private
  val regOutput = Reg(UInt(XLEN.W))
  when (state =/= stateEnum.resultIsBuffered || io.exeOutKill) {
    regOutput := PCNext
  }

  when (!donotSend) {
    regPC := Mux(state === stateEnum.resultIsBuffered && !io.exeOutKill, regOutput, PCNext)
  }
//^^^^^^^^^^^^^^PC update end^^^^^^^^^^^^^^

//--------------state machine start--------------
//output
  //object stateEnum extends ChiselEnum {
  //  val reset, regIsUpdated, resultIsBuffered = Value
  //}
  //val state = RegInit(stateEnum.reset)

//private
  switch (state) {
    is (stateEnum.reset) {
      state := stateEnum.regIsUpdated
    }
    is (stateEnum.regIsUpdated) {
      when (donotSend)
        {state := stateEnum.resultIsBuffered}
    }
    is (stateEnum.resultIsBuffered) {
      when (!donotSend)
        {state := stateEnum.regIsUpdated}
    }
  }
//^^^^^^^^^^^^^^state machine end^^^^^^^^^^^^^^

//--------------control signal start--------------
  donotSend := !io.outCtrlIO.ready || !io.instReadIO.reqReady
  
  io.outCtrlIO.valid := state === stateEnum.regIsUpdated || state === stateEnum.resultIsBuffered
//^^^^^^^^^^^^^^control signal end^^^^^^^^^^^^^^

//--------------io.output start--------------
//preifToIfData
  io.outDataIO.PC        := Mux(state === stateEnum.resultIsBuffered && !io.exeOutKill, regOutput, PCNext)
  io.outDataIO.addrAlign := Mux(state === stateEnum.resultIsBuffered && !io.exeOutKill, regOutput, PCNext)(addrAlign_w-1, 0)

//toRam
  io.instReadIO.addr := Cat(Mux(state === stateEnum.resultIsBuffered && !io.exeOutKill, regOutput, PCNext)(XLEN-1, addrAlign_w), Fill(addrAlign_w, 0.U))
  io.instReadIO.en   := (state === stateEnum.regIsUpdated || state === stateEnum.resultIsBuffered) && io.outCtrlIO.ready
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

}
