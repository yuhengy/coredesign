package mycore

import scala.language.reflectiveCalls

import chisel3._

class mycoreTOP extends Module
{
  val io = IO(new mycoreTOPIO)

  val instFetchTOP = new instFetchTOP
  val decodeTOP = new decodeTOP
  val executeTOP = new executeTOP
  val memoryTOP = new memoryTOP
  val writeBackTOP = new writeBackTOP

  instFetchTOP.io <> executeTOP.io
  instFetchTOP.instReadIO <> io.instReadIO

  decodeTOP.io <> instFetchTOP.io
  decodeTOP.io <> writeBackTOP.io

  executeTOP.io <> decodeTOP.io
  executeTOP.io.dataReadIO <> io.dataReadIO
  executeTOP.io.dataWriteIO <> io.dataWriteIO

  memoryTOP.io <> executeTOP.io
  memoryTOP.io.dataReadIO <> io.dataReadIO

  writeBackTOP.io <> memoryTOP.io


}
