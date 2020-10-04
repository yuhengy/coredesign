package mycore

import scala.language.reflectiveCalls

import chisel3._

import mycore.preInstFetch.preInstFetchTOP
import mycore.instFetch.instFetchTOP
import mycore.decode.decodeTOP
import mycore.execute.executeTOP
import mycore.memory.memoryTOP
import mycore.writeBack.writeBackTOP

class mycoreTOP extends Module
{
  val io = IO(new mycoreTOPIO)

  val preInstFetchTOP = Module(new preInstFetchTOP)
  val instFetchTOP = Module(new instFetchTOP)
  val decodeTOP = Module(new decodeTOP)
  val executeTOP = Module(new executeTOP)
  val memoryTOP = Module(new memoryTOP)
  val writeBackTOP = Module(new writeBackTOP)

  preInstFetchTOP.io.brjmpTarget := executeTOP.io.brjmpTarget
  preInstFetchTOP.io.jmpRTarget  := executeTOP.io.jmpRTarget
  preInstFetchTOP.io.PCSel       := executeTOP.io.PCSel
  io.instReadIO.addr             := preInstFetchTOP.io.instReadIO.addr
  io.instReadIO.en               := preInstFetchTOP.io.instReadIO.en

  instFetchTOP.io.preifToIfDataIO <> preInstFetchTOP.io.preifToIfDataIO
  instFetchTOP.io.preifToIfCtrlIO <> preInstFetchTOP.io.preifToIfCtrlIO
  instFetchTOP.io.exeOutKill      := executeTOP.io.exeOutKill
  instFetchTOP.io.instReadIO.data := io.instReadIO.data

  decodeTOP.io.ifToDecDataIO <> instFetchTOP.io.ifToDecDataIO
  decodeTOP.io.ifToDecCtrlIO <> instFetchTOP.io.ifToDecCtrlIO
  decodeTOP.io.exeOutKill    := executeTOP.io.exeOutKill
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
