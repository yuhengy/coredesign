package mycore
package instFetch

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

import common.constants._
import common.configurations._
import common.memReadIO
import mycore.preInstFetch.preifToIfDataIO

class instFetchTOP extends Module
{
  val io = IO(new Bundle{
  //preifToIfData
    val inDataIO = Input(new preifToIfDataIO)
  //preifToIfCtrl
    val inCtrlIO = Flipped(Decoupled(new Bundle{}))

  //ifToDecData
    val outDataIO = Output(new ifToDecDataIO)
  //ifToDecCtrl
    val outCtrlIO = Decoupled(new Bundle{})

  //exeOutKill
    val exeOutKill  = Input(Bool())

  //fromRam
    val instReadIO = new Bundle{
      val respValid = Input(Bool())
      val data = Input(UInt(XLEN.W))
    }
  })

//--------------instFetch global status start--------------
  val regDataIO = Reg(new preifToIfDataIO)
  when (io.inCtrlIO.valid && io.inCtrlIO.ready) {
    regDataIO <> io.inDataIO
  }
//^^^^^^^^^^^^^^instFetch global status end^^^^^^^^^^^^^^

//--------------state machine start--------------
//output
  object stateEnum extends ChiselEnum {
    val reset, idle, regIsUpdated, resultIsBuffered, waitDummyResp = Value
  }
  val state = RegInit(stateEnum.reset)

//private
  switch (state) {
    is (stateEnum.reset) {
      state := stateEnum.idle
    }
    is (stateEnum.idle) {
      when (io.inCtrlIO.valid && io.inCtrlIO.ready)
        {state := stateEnum.regIsUpdated}
    }
    is (stateEnum.regIsUpdated) {
      when (io.inCtrlIO.valid && io.inCtrlIO.ready)
        {state := stateEnum.regIsUpdated}.
      elsewhen (io.outCtrlIO.valid && io.outCtrlIO.ready || io.exeOutKill)
        {state := stateEnum.idle}.
      otherwise
        {state := stateEnum.resultIsBuffered}
    }
    is (stateEnum.resultIsBuffered) {
      when (io.inCtrlIO.valid && io.inCtrlIO.ready)
        {state := stateEnum.regIsUpdated}.
      elsewhen (io.outCtrlIO.valid && io.outCtrlIO.ready || io.exeOutKill)
        {state := stateEnum.idle}.
      otherwise
        {state := stateEnum.resultIsBuffered}
    }
  }
//^^^^^^^^^^^^^^state machine end^^^^^^^^^^^^^^

//--------------control signal start--------------
  //val stall = (state === stateEnum.regIsUpdated || state === stateEnum.resultIsBuffered ||
  //             state === stateEnum.waitDummyResp) &&
  val stall = false.B

  io.inCtrlIO.ready := state === stateEnum.reset || state === stateEnum.idle ||
                       io.outCtrlIO.ready && io.outCtrlIO.valid ||
                       io.exeOutKill
  io.outCtrlIO.valid := (state === stateEnum.regIsUpdated || state === stateEnum.resultIsBuffered) &&
                        !stall && !io.exeOutKill
//^^^^^^^^^^^^^^control signal end^^^^^^^^^^^^^^

//--------------io.output start--------------
  val dataAlign = io.instReadIO.data >> (regDataIO.addrAlign << 3.U)
  val regOutput = Reg(UInt(XLEN.W))  //Only reg those output that may change
  when (state =/= stateEnum.resultIsBuffered) {
    regOutput := dataAlign
  }

  io.outDataIO.PC   := regDataIO.PC
  io.outDataIO.inst := Mux(state === stateEnum.resultIsBuffered, regOutput, dataAlign)  //TODO: inst should be 32
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

}
