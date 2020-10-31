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

  preInstFetchTOP.io.exeOutKill  := executeTOP.io.exeOutKill
  preInstFetchTOP.io.brjmpTarget := executeTOP.io.brjmpTarget
  preInstFetchTOP.io.jmpRTarget  := executeTOP.io.jmpRTarget
  preInstFetchTOP.io.exceptionTarget := writeBackTOP.io.exceptionTarget
  preInstFetchTOP.io.PCSel       := executeTOP.io.PCSel
  preInstFetchTOP.io.instReadIO.reqReady := io.instReadIO.reqReady
  io.instReadIO.addr             := preInstFetchTOP.io.instReadIO.addr
  io.instReadIO.en               := preInstFetchTOP.io.instReadIO.en

  instFetchTOP.io.inDataIO <> preInstFetchTOP.io.outDataIO
  instFetchTOP.io.inCtrlIO <> preInstFetchTOP.io.outCtrlIO
  instFetchTOP.io.exeOutKill      := executeTOP.io.exeOutKill
  instFetchTOP.io.instReadIO.respValid := io.instReadIO.respValid
  instFetchTOP.io.instReadIO.data      := io.instReadIO.data

  decodeTOP.io.inDataIO <> instFetchTOP.io.outDataIO
  decodeTOP.io.inCtrlIO <> instFetchTOP.io.outCtrlIO
  decodeTOP.io.exeOutKill    := executeTOP.io.exeOutKill
  decodeTOP.io.exeDest       <> executeTOP.io.exeDest
  decodeTOP.io.exeCSRWriteType := executeTOP.io.exeCSRWriteType
  decodeTOP.io.memDest       <> memoryTOP.io.memDest
  decodeTOP.io.memCSRWriteType := memoryTOP.io.memCSRWriteType
  decodeTOP.io.wbToDecWbAddr := writeBackTOP.io.wbToDecWbAddr
  decodeTOP.io.wbToDecWbdata := writeBackTOP.io.wbToDecWbdata
  decodeTOP.io.wbToDecWRfWen := writeBackTOP.io.wbToDecWRfWen
  decodeTOP.io.wbCSRWriteType := writeBackTOP.io.wbCSRWriteType

  executeTOP.io.inDataIO  <> decodeTOP.io.outDataIO
  executeTOP.io.inCtrlIO  <> decodeTOP.io.outCtrlIO
  executeTOP.io.dataReadIO.reqReady := io.dataReadIO.reqReady
  io.dataReadIO.addr      := executeTOP.io.dataReadIO.addr
  io.dataReadIO.en        := executeTOP.io.dataReadIO.en
  executeTOP.io.dataWriteIO.reqReady := io.dataWriteIO.reqReady
  io.dataWriteIO.addr     := executeTOP.io.dataWriteIO.addr
  io.dataWriteIO.data     := executeTOP.io.dataWriteIO.data
  io.dataWriteIO.mask     := executeTOP.io.dataWriteIO.mask
  io.dataWriteIO.en       := executeTOP.io.dataWriteIO.en

  memoryTOP.io.inDataIO  <> executeTOP.io.outDataIO
  memoryTOP.io.inCtrlIO  <> executeTOP.io.outCtrlIO
  memoryTOP.io.dataReadIO.respValid  := io.dataReadIO.respValid
  memoryTOP.io.dataReadIO.data       := io.dataReadIO.data
  memoryTOP.io.dataWriteIO.respValid := io.dataWriteIO.respValid

  writeBackTOP.io.inDataIO <> memoryTOP.io.outDataIO
  writeBackTOP.io.inCtrlIO <> memoryTOP.io.outCtrlIO


}
