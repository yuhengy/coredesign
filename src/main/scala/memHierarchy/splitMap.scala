package memHierarchy

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._

import common.configurations._
import common.memMapConstants._
import common.memWriteIO
import common.memReadIO

// TODO: why such a simple function looks so complicated in code
class splitMap extends Module
{
  val io = IO(new Bundle{
    val dataWriteIO = Flipped(new memWriteIO)
    val MMIOWriteIO = new memWriteIO
    val MEMWriteIO = new memWriteIO

    val dataReadIO = Flipped(new memReadIO)
    val MMIOReadIO = new memReadIO
    val MEMReadIO = new memReadIO
  })

  // request
    // one request on fly
  io.dataWriteIO.reqReady := io.MMIOWriteIO.reqReady && io.MEMWriteIO.reqReady
  io.dataReadIO.reqReady  := io.MMIOReadIO.reqReady  && io.MEMReadIO.reqReady

    // set arbiter
  io.MMIOWriteIO.en := io.dataWriteIO.en && ((io.dataWriteIO.addr & MMIOSPACE_MASK.asUInt(XLEN.W)) === MMIOSPCAE_ID.asUInt(XLEN.W))
  io.MEMWriteIO.en  := io.dataWriteIO.en && ((io.dataWriteIO.addr & MEMSPACE_MASK.asUInt(XLEN.W) ) === MEMSPACE_ID.asUInt(XLEN.W) )

  io.MMIOReadIO.en  := io.dataReadIO.en  && ((io.dataReadIO.addr  & MMIOSPACE_MASK.asUInt(XLEN.W)) === MMIOSPCAE_ID.asUInt(XLEN.W))
  io.MEMReadIO.en   := io.dataReadIO.en  && ((io.dataReadIO.addr  & MEMSPACE_MASK.asUInt(XLEN.W) ) === MEMSPACE_ID.asUInt(XLEN.W) )

    // duplicate output
  io.MMIOWriteIO.addr := io.dataWriteIO.addr
  io.MMIOWriteIO.data := io.dataWriteIO.data
  io.MMIOWriteIO.mask := io.dataWriteIO.mask
  io.MEMWriteIO.addr  := io.dataWriteIO.addr
  io.MEMWriteIO.data  := io.dataWriteIO.data
  io.MEMWriteIO.mask  := io.dataWriteIO.mask

  io.MMIOReadIO.addr := io.dataReadIO.addr
  io.MEMReadIO.addr  := io.dataReadIO.addr

  // response
    // the respone is targeting **the only** on-fly request
  io.dataWriteIO.respValid := io.MMIOWriteIO.respValid || io.MEMWriteIO.respValid
  io.dataReadIO.respValid  := io.MMIOReadIO.respValid  || io.MEMReadIO.respValid

    // selection input
  io.dataReadIO.data := Mux(io.MMIOReadIO.respValid, io.MMIOReadIO.data, io.MEMReadIO.data)

}



