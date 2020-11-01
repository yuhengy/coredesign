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

// 3.1.2 Machine Vendor ID Register mvendorid
class mvendoridReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal

  // explicit read
  io.CSRDataIO.CSRReadData := NOT_IMPLEMENTED.asUInt(XLEN.W)

  // explicit write

  // implicit read

  // implicit write

}

// 3.1.3 Machine Architecture ID Register marchid
class marchidReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal

  // explicit read
  io.CSRDataIO.CSRReadData := NOT_IMPLEMENTED.asUInt(XLEN.W)

  // explicit write

  // implicit read

  // implicit write

}

// 3.1.4 Machine Implementation ID Register mimpid
class mimpidReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal

  // explicit read
  io.CSRDataIO.CSRReadData := NOT_IMPLEMENTED.asUInt(XLEN.W)

  // explicit write

  // implicit read

  // implicit write

}

// 3.1.5 Hart ID Register mhartid
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

// 3.1.6 Machine Status Registers (mstatus and mstatush)
class mstatusReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal
    // 3.1.6.1 Privilege and Global Interrupt-Enable Stack in mstatus register
  val SIE  = NOT_IMPLEMENTED.asUInt(1.W)
  val MIE  = RegInit(0.asUInt(1.W))
  val SPIE = NOT_IMPLEMENTED.asUInt(1.W)
  val MPIE = RegInit(0.asUInt(1.W))
  val SPP  = NOT_IMPLEMENTED.asUInt(1.W)
  val MPP  = 3.asUInt(2.W)  // partial implemented

    // 3.1.6.2 Base ISA Control in mstatus Register
  val SXL = NOT_IMPLEMENTED.asUInt(2.W)
  val UXL = NOT_IMPLEMENTED.asUInt(2.W)

    // 3.1.6.3 Memory Privilege in mstatus Register
  val MPRV = NOT_IMPLEMENTED.asUInt(1.W)
  val MXR  = NOT_IMPLEMENTED.asUInt(1.W)
  val SUM  = NOT_IMPLEMENTED.asUInt(1.W)

    // 3.1.6.4 Endianness Control in mstatus and mstatush Registers
  val MBE = NOT_IMPLEMENTED.asUInt(1.W)  // TODO: need implement?
  val SBE = NOT_IMPLEMENTED.asUInt(1.W)
  val UBE = NOT_IMPLEMENTED.asUInt(1.W)

    // 3.1.6.5 Virtualization Support in mstatus Register
  val TVM = NOT_IMPLEMENTED.asUInt(1.W)
  val TW  = NOT_IMPLEMENTED.asUInt(1.W)
  val TSR = NOT_IMPLEMENTED.asUInt(1.W)

    // 3.1.6.6 Extension Context Status in mstatus Register
  val FS = NOT_IMPLEMENTED.asUInt(2.W)
  val XS = NOT_IMPLEMENTED.asUInt(2.W)
  val SD = NOT_IMPLEMENTED.asUInt(1.W)

  // explicit read
  io.CSRDataIO.CSRReadData := Cat(SD, Fill(XLEN-39, 0.U),
                                  MBE, SBE, SXL, UXL, Fill(9, 0.U),
                                  TSR, TW, TVM, MXR, SUM, MPRV, XS, FS, MPP, Fill(2, 0.U),
                                  SPP, MPIE, UBE, SPIE, Fill(1, 0.U),
                                  MIE, Fill(1, 0.U), SIE, Fill(1, 0.U))

  // explicit write
  when (io.CSRCtrlIO.CSRWriteType === CSRWT_W) {
    MIE  := io.CSRDataIO.CSRWriteData(3)
    MPIE := io.CSRDataIO.CSRWriteData(7)
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_S) {
    MIE  := io.CSRDataIO.CSRWriteData(3) | MIE
    MPIE := io.CSRDataIO.CSRWriteData(7) | MPIE
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_C) {
    MIE  := ~io.CSRDataIO.CSRWriteData(3) & MIE
    MPIE := ~io.CSRDataIO.CSRWriteData(7) & MPIE
  }

  // implicit read

  // implicit write

}

// 3.1.1 Machine ISA Register misa
class misaReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal

  // explicit read
  io.CSRDataIO.CSRReadData := Cat(2.asUInt(2.W), Fill(XLEN-28, 0.U), 0x100.asUInt(26.W))

  // explicit write

  // implicit read

  // implicit write

}

// 3.1.8 Machine Trap Delegation Registers (medeleg and mideleg) //not exist

// 3.1.9 Machine Interrupt Registers (mip and mie)
class mieReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal
  val MEIE = RegInit(0.asUInt(1.W))
  val MTIE = RegInit(0.asUInt(1.W))
  val MSIE = RegInit(0.asUInt(1.W))
  val SEIE = NOT_IMPLEMENTED.asUInt(1.W)
  val STIE = NOT_IMPLEMENTED.asUInt(1.W)
  val SSIE = NOT_IMPLEMENTED.asUInt(1.W)

  // explicit read
  io.CSRDataIO.CSRReadData := Cat(Fill(48, 0.U), Fill(4, 0.U),
                                  MEIE, Fill(1, 0.U), SEIE, Fill(1, 0.U),
                                  MTIE, Fill(1, 0.U), STIE, Fill(1, 0.U),
                                  MSIE, Fill(1, 0.U), SSIE, Fill(1, 0.U))

  // explicit write
  when (io.CSRCtrlIO.CSRWriteType === CSRWT_W) {
    MEIE := io.CSRDataIO.CSRWriteData(11)
    MTIE := io.CSRDataIO.CSRWriteData(7)
    MSIE := io.CSRDataIO.CSRWriteData(3)
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_S) {
    MEIE := io.CSRDataIO.CSRWriteData(11) | MEIE
    MTIE := io.CSRDataIO.CSRWriteData(7) | MTIE
    MSIE := io.CSRDataIO.CSRWriteData(3) | MSIE
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_C) {
    MEIE := ~io.CSRDataIO.CSRWriteData(11) & MEIE
    MTIE := ~io.CSRDataIO.CSRWriteData(7) & MTIE
    MSIE := ~io.CSRDataIO.CSRWriteData(3) & MSIE
  }

  // implicit read

  // implicit write

}

// 3.1.7 Machine Trap-Vector Base-Address Register (mtvec)
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
    partialBase := io.CSRDataIO.CSRWriteData(XLEN - 1,4) | partialBase
    mode        := io.CSRDataIO.CSRWriteData(1,0) | mode
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_C) {
    partialBase := ~io.CSRDataIO.CSRWriteData(XLEN - 1,4) & partialBase
    mode        := ~io.CSRDataIO.CSRWriteData(1,0) & mode
  }

  // implicit read

  // implicit write

}

// 3.1.12 Machine Counter-Enable Register (mcounteren)


//---------------------------------------------
// Machine Trap Handling
//---------------------------------------------

// 3.1.14 Machine Scratch Register (mscratch)
class mscratchReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal
  val mscratch = RegInit(0.asUInt((XLEN).W))

  // explicit read
  io.CSRDataIO.CSRReadData := mscratch

  // explicit write
  when (io.CSRCtrlIO.CSRWriteType === CSRWT_W) {
    mscratch := io.CSRDataIO.CSRWriteData
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_S) {
    mscratch := io.CSRDataIO.CSRWriteData | mscratch
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_C) {
    mscratch := ~io.CSRDataIO.CSRWriteData & mscratch
  }

  // implicit read

  // implicit write

}

// 3.1.15 Machine Exception Program Counter (mepc)
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
    partialAddr := io.CSRDataIO.CSRWriteData(XLEN - 1,2) | partialAddr
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_C) {
    partialAddr := ~io.CSRDataIO.CSRWriteData(XLEN - 1,2) & partialAddr
  }

  // implicit read
  io.CSRDataIO.exceptionTarget := Cat(partialAddr, Fill(2, 0.U))

  // implicit write

}

// 3.1.16 Machine Cause Register (mcause)
class mcauseReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal
  val interrupt     = RegInit(0.asUInt(1.W))
  val exceptionCode = RegInit(0.asUInt((XLEN - 1).W))

  // explicit read
  io.CSRDataIO.CSRReadData := Cat(interrupt, exceptionCode)

  // explicit write
  when (io.CSRCtrlIO.CSRWriteType === CSRWT_W) {
    interrupt     := io.CSRDataIO.CSRWriteData(XLEN - 1)
    exceptionCode := io.CSRDataIO.CSRWriteData(XLEN - 2,0)
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_S) {
    interrupt     := io.CSRDataIO.CSRWriteData(XLEN - 1)   | interrupt
    exceptionCode := io.CSRDataIO.CSRWriteData(XLEN - 2,0) | exceptionCode
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_C) {
    interrupt     := ~io.CSRDataIO.CSRWriteData(XLEN - 1)   & interrupt
    exceptionCode := ~io.CSRDataIO.CSRWriteData(XLEN - 2,0) & exceptionCode
  }

  // implicit read

  // implicit write

}

// 3.1.17 Machine Trap Value Register (mtval)
class mtvalReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal
  val mtval = RegInit(0.asUInt((XLEN).W))

  // explicit read
  io.CSRDataIO.CSRReadData := mtval

  // explicit write
  when (io.CSRCtrlIO.CSRWriteType === CSRWT_W) {
    mtval := io.CSRDataIO.CSRWriteData
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_S) {
    mtval := io.CSRDataIO.CSRWriteData | mtval
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_C) {
    mtval := ~io.CSRDataIO.CSRWriteData & mtval
  }

  // implicit read

  // implicit write

}

// 3.1.9 Machine Interrupt Registers (mip and mie)
class mipReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal
  val MEIP = RegInit(0.asUInt(1.W))
  val MTIP = RegInit(0.asUInt(1.W))
  val MSIP = RegInit(0.asUInt(1.W))
  val SEIP = NOT_IMPLEMENTED.asUInt(1.W)
  val STIP = NOT_IMPLEMENTED.asUInt(1.W)
  val SSIP = NOT_IMPLEMENTED.asUInt(1.W)

  // explicit read
  io.CSRDataIO.CSRReadData := Cat(Fill(48, 0.U), Fill(4, 0.U),
                                  MEIP, Fill(1, 0.U), SEIP, Fill(1, 0.U),
                                  MTIP, Fill(1, 0.U), STIP, Fill(1, 0.U),
                                  MSIP, Fill(1, 0.U), SSIP, Fill(1, 0.U))

  // explicit write

  // implicit read

  // implicit write

}


//---------------------------------------------
// Machine Counter/Timers
// 3.1.11 Hardware Performance Monitor
//---------------------------------------------

class mcycleReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal
  val mcycle = RegInit(0.asUInt(64.W))

  // explicit read
  io.CSRDataIO.CSRReadData := mcycle

  // explicit write
  when (io.CSRCtrlIO.CSRWriteType === CSRWT_W) {
    mcycle := io.CSRDataIO.CSRWriteData
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_S) {
    mcycle := io.CSRDataIO.CSRWriteData | mcycle
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_C) {
    mcycle := ~io.CSRDataIO.CSRWriteData & mcycle
  }.
  otherwise {  // implicit write
    mcycle := mcycle + 1.asUInt(64.W)
  }

  // implicit read

}

class minstretReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal
  val minstret = RegInit(0.asUInt(64.W))

  // explicit read
  io.CSRDataIO.CSRReadData := minstret

  // explicit write
  when (io.CSRCtrlIO.CSRWriteType === CSRWT_W) {
    minstret := io.CSRDataIO.CSRWriteData
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_S) {
    minstret := io.CSRDataIO.CSRWriteData | minstret
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_C) {
    minstret := ~io.CSRDataIO.CSRWriteData & minstret
  }//.
  //else {  // implicit write
  //  minstret := minstret + 1.asUInt(64.W)
  //}

  // implicit read

}

class mhpmcounterXReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal

  // explicit read
  io.CSRDataIO.CSRReadData := NOT_IMPLEMENTED.asUInt(64.W)

  // explicit write

  // implicit read

  // implicit write

}


//---------------------------------------------
// Machine Counter Setup
//---------------------------------------------

// 3.1.13 Machine Counter-Inhibit CSR (mcountinhibit)
class mcountinhibitReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal

  // explicit read
  io.CSRDataIO.CSRReadData := NOT_IMPLEMENTED.asUInt(32.W)

  // explicit write

  // implicit read

  // implicit write

}

// 3.1.11 Hardware Performance Monitor
class mhpmeventXReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal

  // explicit read
  io.CSRDataIO.CSRReadData := NOT_IMPLEMENTED.asUInt(XLEN.W)

  // explicit write

  // implicit read

  // implicit write

}


//---------------------------------------------
// Memory-mapped Control Register
// When we find a memory mapped control register address in execute stage,
// merge this ld/st into a CSRRW instruction
//---------------------------------------------

// 3.1.10 Machine Timer Registers (mtime and mtimecmp)
class mtimeMapReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal
  val mtime = RegInit(0.asUInt(64.W))

  // explicit read
  io.CSRDataIO.CSRReadData := mtime

  // explicit write
  when (io.CSRCtrlIO.CSRWriteType === CSRWT_W) {
    mtime := io.CSRDataIO.CSRWriteData
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_S) {
    mtime := io.CSRDataIO.CSRWriteData | mtime
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_C) {
    mtime := ~io.CSRDataIO.CSRWriteData & mtime
  }.
  otherwise {  // implicit write
    mtime := mtime + 1.asUInt(64.W)
  }

  // implicit read

}

// 3.1.10 Machine Timer Registers (mtime and mtimecmp)
class mtimecmpMapReg extends Module
{
  val io = IO(new Bundle{
    val CSRDataIO = new CSRDataIO
    val CSRCtrlIO = new CSRCtrlIO
  })
  io := DontCare

  // internal
  val mtimecmp = RegInit(0.asUInt(64.W))

  // explicit read
  io.CSRDataIO.CSRReadData := mtimecmp

  // explicit write
  when (io.CSRCtrlIO.CSRWriteType === CSRWT_W) {
    mtimecmp := io.CSRDataIO.CSRWriteData
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_S) {
    mtimecmp := io.CSRDataIO.CSRWriteData | mtimecmp
  }.
  elsewhen (io.CSRCtrlIO.CSRWriteType === CSRWT_C) {
    mtimecmp := ~io.CSRDataIO.CSRWriteData & mtimecmp
  }

  // implicit read

  // implicit write

}








