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
    // Machinne Information Registers
  val mvendoridReg = Module(new mvendoridReg)
  mvendoridReg.io := DontCare
  mvendoridReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mvendoridReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mvendoridAddr,
                                              io.CSRCtrlIO.CSRWriteType, CSRWT_U)
  val marchidReg = Module(new marchidReg)
  marchidReg.io := DontCare
  marchidReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  marchidReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === marchidAddr,
                                              io.CSRCtrlIO.CSRWriteType, CSRWT_U)
  val mimpidReg = Module(new mimpidReg)
  mimpidReg.io := DontCare
  mimpidReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mimpidReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mimpidAddr,
                                              io.CSRCtrlIO.CSRWriteType, CSRWT_U)
  val mhartidReg = Module(new mhartidReg)
  mhartidReg.io := DontCare
  mhartidReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mhartidReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mhartidAddr,
                                              io.CSRCtrlIO.CSRWriteType, CSRWT_U)

    // Machine Trap Setup
  val mstatusReg = Module(new mstatusReg)
  mstatusReg.io := DontCare
  mstatusReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mstatusReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mstatusAddr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)
  val misaReg = Module(new misaReg)
  misaReg.io := DontCare
  misaReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  misaReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === misaAddr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)
  val mieReg = Module(new mieReg)
  mieReg.io := DontCare
  mieReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mieReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mieAddr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)
  val mtvecReg = Module(new mtvecReg)
  mtvecReg.io := DontCare
  mtvecReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mtvecReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mtvecAddr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)

    // Machine Trap Handling
  val mscratchReg = Module(new mscratchReg)
  mscratchReg.io := DontCare
  mscratchReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mscratchReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mscratchAddr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)
  val mepcReg = Module(new mepcReg)
  mepcReg.io := DontCare
  mepcReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mepcReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mepcAddr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)
  val mcauseReg = Module(new mcauseReg)
  mcauseReg.io := DontCare
  mcauseReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mcauseReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mcauseAddr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)
  val mtvalReg = Module(new mtvalReg)
  mtvalReg.io := DontCare
  mtvalReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mtvalReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mtvalAddr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)
  val mipReg = Module(new mipReg)
  mipReg.io := DontCare
  mipReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mipReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mipAddr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)

    // Machine Counter/Timers
  val mcycleReg = Module(new mcycleReg)
  mcycleReg.io := DontCare
  mcycleReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mcycleReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mcycleAddr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)
  val minstretReg = Module(new minstretReg)
  minstretReg.io := DontCare
  minstretReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  minstretReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === minstretAddr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)
  val mhpmcounterXReg = Module(new mhpmcounterXReg)
  mhpmcounterXReg.io := DontCare
  mhpmcounterXReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mhpmcounterXReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mhpmcounter3Addr  ||
     io.CSRDataIO.CSRAddr === mhpmcounter4Addr  || io.CSRDataIO.CSRAddr === mhpmcounter5Addr  ||
     io.CSRDataIO.CSRAddr === mhpmcounter6Addr  || io.CSRDataIO.CSRAddr === mhpmcounter7Addr  ||
     io.CSRDataIO.CSRAddr === mhpmcounter8Addr  || io.CSRDataIO.CSRAddr === mhpmcounter9Addr  ||
     io.CSRDataIO.CSRAddr === mhpmcounter10Addr || io.CSRDataIO.CSRAddr === mhpmcounter11Addr ||
     io.CSRDataIO.CSRAddr === mhpmcounter12Addr || io.CSRDataIO.CSRAddr === mhpmcounter13Addr ||
     io.CSRDataIO.CSRAddr === mhpmcounter14Addr || io.CSRDataIO.CSRAddr === mhpmcounter15Addr ||
     io.CSRDataIO.CSRAddr === mhpmcounter16Addr || io.CSRDataIO.CSRAddr === mhpmcounter17Addr ||
     io.CSRDataIO.CSRAddr === mhpmcounter18Addr || io.CSRDataIO.CSRAddr === mhpmcounter19Addr ||
     io.CSRDataIO.CSRAddr === mhpmcounter20Addr || io.CSRDataIO.CSRAddr === mhpmcounter21Addr ||
     io.CSRDataIO.CSRAddr === mhpmcounter22Addr || io.CSRDataIO.CSRAddr === mhpmcounter23Addr ||
     io.CSRDataIO.CSRAddr === mhpmcounter24Addr || io.CSRDataIO.CSRAddr === mhpmcounter25Addr ||
     io.CSRDataIO.CSRAddr === mhpmcounter26Addr || io.CSRDataIO.CSRAddr === mhpmcounter27Addr ||
     io.CSRDataIO.CSRAddr === mhpmcounter28Addr || io.CSRDataIO.CSRAddr === mhpmcounter29Addr ||
     io.CSRDataIO.CSRAddr === mhpmcounter30Addr || io.CSRDataIO.CSRAddr === mhpmcounter31Addr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)

    // Machine Counter Setup
  val mcountinhibitReg = Module(new mcountinhibitReg)
  mcountinhibitReg.io := DontCare
  mcountinhibitReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mcountinhibitReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mcountinhibitAddr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)
  val mhpmeventXReg = Module(new mhpmeventXReg)
  mhpmeventXReg.io := DontCare
  mhpmeventXReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mhpmeventXReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mhpmevent3Addr  ||
     io.CSRDataIO.CSRAddr === mhpmevent4Addr  || io.CSRDataIO.CSRAddr === mhpmevent5Addr  ||
     io.CSRDataIO.CSRAddr === mhpmevent6Addr  || io.CSRDataIO.CSRAddr === mhpmevent7Addr  ||
     io.CSRDataIO.CSRAddr === mhpmevent8Addr  || io.CSRDataIO.CSRAddr === mhpmevent9Addr  ||
     io.CSRDataIO.CSRAddr === mhpmevent10Addr || io.CSRDataIO.CSRAddr === mhpmevent11Addr ||
     io.CSRDataIO.CSRAddr === mhpmevent12Addr || io.CSRDataIO.CSRAddr === mhpmevent13Addr ||
     io.CSRDataIO.CSRAddr === mhpmevent14Addr || io.CSRDataIO.CSRAddr === mhpmevent15Addr ||
     io.CSRDataIO.CSRAddr === mhpmevent16Addr || io.CSRDataIO.CSRAddr === mhpmevent17Addr ||
     io.CSRDataIO.CSRAddr === mhpmevent18Addr || io.CSRDataIO.CSRAddr === mhpmevent19Addr ||
     io.CSRDataIO.CSRAddr === mhpmevent20Addr || io.CSRDataIO.CSRAddr === mhpmevent21Addr ||
     io.CSRDataIO.CSRAddr === mhpmevent22Addr || io.CSRDataIO.CSRAddr === mhpmevent23Addr ||
     io.CSRDataIO.CSRAddr === mhpmevent24Addr || io.CSRDataIO.CSRAddr === mhpmevent25Addr ||
     io.CSRDataIO.CSRAddr === mhpmevent26Addr || io.CSRDataIO.CSRAddr === mhpmevent27Addr ||
     io.CSRDataIO.CSRAddr === mhpmevent28Addr || io.CSRDataIO.CSRAddr === mhpmevent29Addr ||
     io.CSRDataIO.CSRAddr === mhpmevent30Addr || io.CSRDataIO.CSRAddr === mhpmevent31Addr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)

    // Memory-mapped Control Register
  val mtimeMapReg = Module(new mtimeMapReg)
  mtimeMapReg.io := DontCare
  mtimeMapReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mtimeMapReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mtimeMapAddr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)
  val mtimecmpMapReg = Module(new mtimecmpMapReg)
  mtimecmpMapReg.io := DontCare
  mtimecmpMapReg.io.CSRDataIO.CSRWriteData := io.CSRDataIO.CSRWriteData
  mtimecmpMapReg.io.CSRCtrlIO.CSRWriteType := Mux(io.CSRDataIO.CSRAddr === mtimecmpMapAddr,
                                            io.CSRCtrlIO.CSRWriteType, CSRWT_U)

  // explicit read
  val TODOChange = ListLookup(io.CSRDataIO.CSRAddr, List(0.asUInt(XLEN.W)                       , N),
                             // Machinne Information Registers
                       Array(BitPat(mvendoridAddr) -> List(mvendoridReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(marchidAddr)   -> List(marchidReg.io.CSRDataIO.CSRReadData  , Y),
                             BitPat(mimpidAddr)    -> List(mimpidReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhartidAddr)   -> List(mhartidReg.io.CSRDataIO.CSRReadData  , Y),

                             // Machine Trap Setup
                             BitPat(mstatusAddr) -> List(mstatusReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(misaAddr)    -> List(misaReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mieAddr)     -> List(mieReg.io.CSRDataIO.CSRReadData    , Y),
                             BitPat(mtvecAddr)   -> List(mtvecReg.io.CSRDataIO.CSRReadData  , Y),

                             // Machine Trap Handling
                             BitPat(mscratchAddr) -> List(mscratchReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mepcAddr)     -> List(mepcReg.io.CSRDataIO.CSRReadData    , Y),
                             BitPat(mcauseAddr)   -> List(mcauseReg.io.CSRDataIO.CSRReadData  , Y),
                             BitPat(mtvalAddr)    -> List(mtvalReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mipAddr)      -> List(mipReg.io.CSRDataIO.CSRReadData     , Y),

                             // Machine Counter/Timers
                             BitPat(mcycleAddr)        -> List(mcycleReg.io.CSRDataIO.CSRReadData      , Y),
                             BitPat(minstretAddr)      -> List(minstretReg.io.CSRDataIO.CSRReadData    , Y),
                             BitPat(mhpmcounter3Addr)  -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter4Addr)  -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter5Addr)  -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter6Addr)  -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter7Addr)  -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter8Addr)  -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter9Addr)  -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter10Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter11Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter12Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter13Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter14Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter15Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter16Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter17Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter18Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter19Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter20Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter21Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter22Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter23Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter24Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter25Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter26Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter27Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter28Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter29Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter30Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmcounter31Addr) -> List(mhpmcounterXReg.io.CSRDataIO.CSRReadData, Y),

                             // Machine Counter Setup
                             BitPat(mcountinhibitAddr) -> List(mcountinhibitReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mhpmevent3Addr)    -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent4Addr)    -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent5Addr)    -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent6Addr)    -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent7Addr)    -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent8Addr)    -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent9Addr)    -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent10Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent11Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent12Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent13Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent14Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent15Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent16Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent17Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent18Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent19Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent20Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent21Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent22Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent23Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent24Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent25Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent26Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent27Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent28Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent29Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent30Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),
                             BitPat(mhpmevent31Addr)   -> List(mhpmeventXReg.io.CSRDataIO.CSRReadData   , Y),

                             // Memory-mapped Control Register
                             BitPat(mtimeMapAddr)    -> List(mtimeMapReg.io.CSRDataIO.CSRReadData, Y),
                             BitPat(mtimecmpMapAddr) -> List(mtimecmpMapReg.io.CSRDataIO.CSRReadData, Y),

                             BitPat(satpAddr)     -> List(0.asUInt(XLEN.W)                   , IgnoreCSR),
                             BitPat(pmpaddr0Addr) -> List(0.asUInt(XLEN.W)                   , IgnoreCSR),
                             BitPat(pmpcfg0Addr)  -> List(0.asUInt(XLEN.W)                   , IgnoreCSR),
                             BitPat(medelegAddr)  -> List(0.asUInt(XLEN.W)                   , IgnoreCSR),
                             BitPat(midelegAddr)  -> List(0.asUInt(XLEN.W)                   , IgnoreCSR)
                    ))
  val temp_CSRReadData :: temp_CSRSupported :: Nil = TODOChange
  io.CSRDataIO.CSRReadData := temp_CSRReadData
  io.CSRCtrlIO.CSRSupported := temp_CSRSupported

  // implicit read
  io.CSRDataIO.exceptionTarget := mepcReg.io.CSRDataIO.exceptionTarget
}
