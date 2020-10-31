package mycore
package writeBack

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._

import common.constants._
import common.configurations._

// TODO: read/write sideeffect, exceptions
class CSRTOP extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })

  // explicit write
  val mhartidReg = Module(new mhartidReg)
  mhartidReg.io := DontCare
  mhartidReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mhartidReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mhartidAddr,
                                              io.CSRCtrlIO.CSRWriteType, CSRWT_U)

  val mtvecReg = Module(new mtvecReg)
  mtvecReg.io := DontCare
  mtvecReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mtvecReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mtvecAddr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)

  val mepcReg = Module(new mepcReg)
  mepcReg.io := DontCare
  mepcReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mepcReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mepcAddr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)

  // explicit read
  val TODOChange = ListLookup(io.CSRDataIO.CSRAddr, List(0.asUInt(XLEN.W)                   , N),
                       Array(BitPat(mhartidAddr)  -> List(mhartidReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mtvecAddr)    -> List(mtvecReg.io.CSRDataIO.CSRReadData  , Y),
                             BitPat(mepcAddr)     -> List(mepcReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(satpAddr)     -> List(0.asUInt(XLEN.W)                   , IgnoreCSR),
                             BitPat(pmpaddr0Addr) -> List(0.asUInt(XLEN.W)                   , IgnoreCSR),
                             BitPat(pmpcfg0Addr)  -> List(0.asUInt(XLEN.W)                   , IgnoreCSR),
                             BitPat(medelegAddr)  -> List(0.asUInt(XLEN.W)                   , IgnoreCSR),
                             BitPat(midelegAddr)  -> List(0.asUInt(XLEN.W)                   , IgnoreCSR)
                    ))
  val temp_CSRReadData :: temp_CSRSupported :: Nil = TODOChange
  io.CSRDataIO.CSRReadData := temp_CSRReadData
  io.CSRCtrlIO.CSRSupported := Y  //temp_CSRSupported

  // implicit read
  io.CSRDataIO.exceptionTarget := mepcReg.io.CSRDataIO.exceptionTarget
}
