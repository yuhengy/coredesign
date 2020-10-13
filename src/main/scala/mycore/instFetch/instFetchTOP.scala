package mycore
package instFetch

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._

import common.constants._
import common.configurations._
import common.memReadIO
import mycore.preInstFetch.preifToIfDataIO

class instFetchTOP extends Module
{
  val io = IO(new Bundle{
  //preifToIfData
    val preifToIfDataIO = Input(new preifToIfDataIO)
  //preifToIfCtrl
    val preifToIfCtrlIO = Flipped(Decoupled(new Bundle{}))

  //ifToDecData
    val ifToDecDataIO = Output(new ifToDecDataIO)
  //ifToDecCtrl
    val ifToDecCtrlIO = Decoupled(new Bundle{})

  //exeOutKill
    val exeOutKill  = Input(Bool())

  //fromRam
    val instReadIO = new Bundle{
      val data = Input(UInt(XLEN.W))
    }
  })

//--------------instFetch global status start--------------
  val regDataIO = Reg(new preifToIfDataIO)
  when (io.preifToIfCtrlIO.valid && io.preifToIfCtrlIO.ready) {
    regDataIO <> io.preifToIfDataIO
  }
//^^^^^^^^^^^^^^instFetch global status end^^^^^^^^^^^^^^

//--------------stall&kill start--------------
  val stall = !io.ifToDecCtrlIO.ready
  val regIsUpdated = RegInit(false.B)
  when (io.preifToIfCtrlIO.valid && io.preifToIfCtrlIO.ready)
    {regIsUpdated := true.B}.
  elsewhen (io.ifToDecCtrlIO.valid && io.ifToDecCtrlIO.ready || io.exeOutKill)
    {regIsUpdated := false.B}  //TODO: check otherwise can be left
  val resultIsBuffered = RegInit(false.B)
  when (io.exeOutKill)
    {resultIsBuffered := false.B}.
  elsewhen (regIsUpdated && !(io.ifToDecCtrlIO.valid && io.ifToDecCtrlIO.ready))
    {resultIsBuffered := true.B}.
  elsewhen (io.ifToDecCtrlIO.valid && io.ifToDecCtrlIO.ready)
    {resultIsBuffered := false.B}  //TODO: check otherwise can be left

  io.preifToIfCtrlIO.ready := !stall || io.exeOutKill
  io.ifToDecCtrlIO.valid := regIsUpdated && !stall && !io.exeOutKill
//^^^^^^^^^^^^^^stall&kill end^^^^^^^^^^^^^^

//--------------io.output start--------------
  val dataAlign = io.instReadIO.data >> (regDataIO.addrAlign << 3.U)
  val regOutput = Reg(UInt(XLEN.W))  //Only reg those output that may change
  when (!resultIsBuffered) {
    regOutput := dataAlign
  }

  io.ifToDecDataIO.PC   := regDataIO.PC
  io.ifToDecDataIO.inst := Mux(resultIsBuffered, regOutput, dataAlign)  //TODO: inst should be 32
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

}
