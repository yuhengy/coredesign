package mycore
package instFetch

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util.experimental.BoringUtils

import common.constants._
import common.configurations._
import common.memReadIO

import mycore.decode.ifToDecDataIO

class instFetchTOP extends Module
{
  val io = IO(new Bundle{
  //ifToDecData
    val ifToDecDataIO = Output(new ifToDecDataIO)

  //exeToIfFeedback
    val brjmpTarget = Input(UInt(XLEN.W))
    val jmpRTarget  = Input(UInt(XLEN.W))
    val PCSel       = Input(UInt(PCSel_w.W))

  //toRam
    val instReadIO  = new memReadIO
  })

//--------------instFetch global status start--------------
  val regPC           = RegInit(UInt(XLEN.W), ADDR_START.U) 
//^^^^^^^^^^^^^^instFetch global status end^^^^^^^^^^^^^^

//--------------PC update start--------------
//input
  val exceptionTarget = Wire(UInt(XLEN.W))  //TODO: this does not have signal now
  exceptionTarget := 0.U
//private
  val PC4             = Wire(UInt(XLEN.W))
  val PCNext          = Wire(UInt(XLEN.W))

  when (true.B) {
    regPC := PCNext
  }
  PCNext := Mux(io.PCSel === PC_4,      PC4,
            Mux(io.PCSel === PC_BRJMP,  io.brjmpTarget,
            Mux(io.PCSel === PC_JALR,   io.jmpRTarget,
          /*Mux(io.PCSel === PC_EXC,*/  exceptionTarget)))

  PC4 := regPC + 4.asUInt(XLEN.W)
//^^^^^^^^^^^^^^PC update end^^^^^^^^^^^^^^

//--------------inst read start--------------
  io.instReadIO.addr := regPC
  io.instReadIO.en   := true.B
//^^^^^^^^^^^^^^inst read end^^^^^^^^^^^^^^

//--------------io.output start--------------
  io.ifToDecDataIO.PC   := regPC
  io.ifToDecDataIO.inst := io.instReadIO.data  //TODO: inst should be 32
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

  BoringUtils.addSource(regPC, "diffTestPC")
}
