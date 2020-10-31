package mycore
package writeBack

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._

import common.constants._
import common.configurations._


//---------------------------------------------
// Machinne Information Registers
//---------------------------------------------
class mhartidReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal

  // explicit read
  io.CSRDataIO.CSRReadData := 0.asUInt(XLEN.W)

  // explicit write

  // implicit read

  // implicit write

}


//---------------------------------------------
// Machine Trap Setup
//---------------------------------------------
class mtvecReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal
  val partialBase = RegInit(0.asUInt((XLEN - 4).W))
  val mode = RegInit(0.asUInt(2.W))

  // explicit read
  io.CSRDataIO.CSRReadData := Cat(partialBase, Fill(2, 0.U), mode)

  // explicit write
  when (io.CSRCtrlIO.CSRWriteType === CSRWT_W) {
    partialBase := io.CSRDataIO.CSRWriteData(XLEN - 1,4)
    mode        := io.CSRDataIO.CSRWriteData(1,0)
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_S) {
    partialBase := partialBase | io.CSRDataIO.CSRWriteData(XLEN - 1,4)
    mode        := mode        | io.CSRDataIO.CSRWriteData(1,0)
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_C) {
    partialBase := partialBase & ~io.CSRDataIO.CSRWriteData(XLEN - 1,4)
    mode        := mode        & ~io.CSRDataIO.CSRWriteData(1,0)
  }

  // implicit read

  // implicit write

}


//---------------------------------------------
// Machine Trap Handling
//---------------------------------------------
class mepcReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal
  val partialAddr = RegInit(0.asUInt((XLEN - 2).W))

  // explicit read
  io.CSRDataIO.CSRReadData := Cat(partialAddr, Fill(2, 0.U))

  // explicit write
  when (io.CSRCtrlIO.CSRWriteType === CSRWT_W) {
    partialAddr := io.CSRDataIO.CSRWriteData(XLEN - 1,2)
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_S) {
    partialAddr := partialAddr | io.CSRDataIO.CSRWriteData(XLEN - 1,2)
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_C) {
    partialAddr := partialAddr & ~io.CSRDataIO.CSRWriteData(XLEN - 1,2)
  }

  // implicit read
  io.CSRDataIO.exceptionTarget := Cat(partialAddr, Fill(2, 0.U))

  // implicit write

}








