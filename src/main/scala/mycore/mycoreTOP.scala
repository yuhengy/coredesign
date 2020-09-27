package mycore

import scala.language.reflectiveCalls

import chisel3._

import mycore.instFetch.instFetchTOP
import mycore.decode.decodeTOP
import mycore.execute.executeTOP
import mycore.memory.memoryTOP
import mycore.writeBack.writeBackTOP

class mycoreTOP extends Module
{
  val io = IO(new mycoreTOPIO)

  val instFetchTOP = Module(new instFetchTOP)
  val decodeTOP = Module(new decodeTOP)
  val executeTOP = Module(new executeTOP)
  val memoryTOP = Module(new memoryTOP)
  val writeBackTOP = Module(new writeBackTOP)

  instFetchTOP.io.brjmpTarget := executeTOP.io.brjmpTarget
  instFetchTOP.io.jmpRTarget  := executeTOP.io.jmpRTarget
  instFetchTOP.io.PCSel       := executeTOP.io.PCSel
  instFetchTOP.io.instReadIO  <> io.instReadIO

  decodeTOP.io.ifToDecDataIO <> instFetchTOP.io.ifToDecDataIO
  decodeTOP.io.ifToDecCtrlIO <> instFetchTOP.io.ifToDecCtrlIO
  decodeTOP.io.exeDest       <> executeTOP.io.exeDest
  decodeTOP.io.memDest       <> memoryTOP.io.memDest
  decodeTOP.io.wbToDecWbAddr := writeBackTOP.io.wbToDecWbAddr
  decodeTOP.io.wbToDecWbdata := writeBackTOP.io.wbToDecWbdata
  decodeTOP.io.wbToDecWRfWen := writeBackTOP.io.wbToDecWRfWen

  executeTOP.io.decToExeDataIO  <> decodeTOP.io.decToExeDataIO
  executeTOP.io.decToExeCtrlIO  <> decodeTOP.io.decToExeCtrlIO
  io.dataReadIO.addr            := executeTOP.io.dataReadIO.addr
  io.dataReadIO.en              := executeTOP.io.dataReadIO.en
  executeTOP.io.dataWriteIO     <> io.dataWriteIO

  memoryTOP.io.exeToMemDataIO  <> executeTOP.io.exeToMemDataIO
  memoryTOP.io.exeToMemCtrlIO  <> executeTOP.io.exeToMemCtrlIO
  memoryTOP.io.dataReadIO.data := io.dataReadIO.data

  writeBackTOP.io.memToWbDataIO <> memoryTOP.io.memToWbDataIO
  writeBackTOP.io.memToWbCtrlIO <> memoryTOP.io.memToWbCtrlIO


}
