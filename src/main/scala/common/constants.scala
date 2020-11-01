package common

import chisel3._
import chisel3.util.log2Ceil

import configurations._

object constants extends
  commonConstants with
  RISCVConstants with
  PCConstants with
  instConstants with
  brTypeConstants with
  regfileConstants with
  aluFuncConstants with
  memTypeConstants with
  opSelConstants with
  CSRAddrConstants with
  CSRWriteTypeConstants with
  CSRImplementationCounstants
{
}


trait commonConstants
{
  val Y        = true.B
  val N        = false.B
}

trait RISCVConstants
{
  val NUM_REG = 32
  val ADDR_START = 0x80000000L
  val WID_INST = 32


  //-----------Derived constants begin-----------
  val WID_REG_ADDR = log2Ceil(NUM_REG)
  //-----------Derived constants end-----------
}

trait PCConstants
{
  val PCSel_w  = 2
  val PC_4     = 0.asUInt(PCSel_w.W)  // PC + 4
  val PC_BRJMP = 1.asUInt(PCSel_w.W)  // brjmp_target
  val PC_JALR  = 2.asUInt(PCSel_w.W)  // jump_reg_target
  val PC_EXC   = 3.asUInt(PCSel_w.W)  // exception
}

trait instConstants
{
   val RS2_MSB = 24
   val RS2_LSB = 20
   val RS1_MSB = 19
   val RS1_LSB = 15
   val RD_MSB  = 11
   val RD_LSB  = 7

}

trait brTypeConstants
{
  val brType_w = 4
  val BR_N     = 0.asUInt(brType_w.W)  // Next
  val BR_NE    = 1.asUInt(brType_w.W)  // Branch on NotEqual
  val BR_EQ    = 2.asUInt(brType_w.W)  // Branch on Equal
  val BR_GE    = 3.asUInt(brType_w.W)  // Branch on Greater/Equal
  val BR_GEU   = 4.asUInt(brType_w.W)  // Branch on Greater/Equal Unsigned
  val BR_LT    = 5.asUInt(brType_w.W)  // Branch on Less Than
  val BR_LTU   = 6.asUInt(brType_w.W)  // Branch on Less Than Unsigned
  val BR_J     = 7.asUInt(brType_w.W)  // Jump
  val BR_JR    = 8.asUInt(brType_w.W)  // Jump Register

}

trait opSelConstants
{
  val op1Sel_w = 2
  val OP1_RS1  = 0.asUInt(op1Sel_w.W) // Register Source #1
  val OP1_RS1W = 1.asUInt(op1Sel_w.W) // Register Source #1 cut for 32 bit
  val OP1_PC   = 2.asUInt(op1Sel_w.W) // PC
  val OP1_IMZ  = 3.asUInt(op1Sel_w.W) // Zero-extended Immediate from RS1 field, for use by CSRI instructions
  val OP1_X    = 0.asUInt(op1Sel_w.W)

  val op2Sel_w   = 3
  val OP2_RS2    = 0.asUInt(op2Sel_w.W) // Register Source #2
  val OP2_RS2W   = 1.asUInt(op2Sel_w.W) // Register Source #2 cut for 32 bit
  val OP2_ITYPE  = 2.asUInt(op2Sel_w.W) // immediate, I-type
  val OP2_STYPE  = 3.asUInt(op2Sel_w.W) // immediate, S-type
  val OP2_SBTYPE = 4.asUInt(op2Sel_w.W) // immediate, B
  val OP2_UTYPE  = 5.asUInt(op2Sel_w.W) // immediate, U-type
  val OP2_UJTYPE = 6.asUInt(op2Sel_w.W) // immediate, J-type
  val OP2_X      = 0.asUInt(op2Sel_w.W)
}

trait regfileConstants
{
  // Register Operand Output Enable Signal
  val OEN_0 = false.B
  val OEN_1 = true.B

  // Register File Write Enable Signal
  val REN_0 = false.B
  val REN_1 = true.B

  // Writeback Select Signal
  val wbSel_w = 3
  val WB_ALU  = 0.asUInt(wbSel_w.W)
  val WB_ALUW = 1.asUInt(wbSel_w.W)
  val WB_MEM  = 2.asUInt(wbSel_w.W)
  val WB_PC4  = 3.asUInt(wbSel_w.W)
  val WB_CSR  = 4.asUInt(wbSel_w.W)
  val WB_X    = 0.asUInt(wbSel_w.W)
}

trait aluFuncConstants
{
  val aluFunc_w  = 4
  val ALU_ADD    = 0.asUInt(aluFunc_w.W)
  val ALU_SUB    = 1.asUInt(aluFunc_w.W)
  val ALU_SLL    = 2.asUInt(aluFunc_w.W)
  val ALU_SRL    = 3.asUInt(aluFunc_w.W)
  val ALU_SRA    = 4.asUInt(aluFunc_w.W)
  val ALU_SLLW   = 5.asUInt(aluFunc_w.W)
  val ALU_SRLW   = 6.asUInt(aluFunc_w.W)
  val ALU_SRAW   = 7.asUInt(aluFunc_w.W)
  val ALU_AND    = 8.asUInt(aluFunc_w.W)
  val ALU_OR     = 9.asUInt(aluFunc_w.W)
  val ALU_XOR    = 10.asUInt(aluFunc_w.W)
  val ALU_SLT    = 11.asUInt(aluFunc_w.W)
  val ALU_SLTU   = 12.asUInt(aluFunc_w.W)
  val ALU_COPY_1 = 13.asUInt(aluFunc_w.W)
  val ALU_COPY_2 = 14.asUInt(aluFunc_w.W)
  val ALU_X      = 0.asUInt(aluFunc_w.W)
}

trait memTypeConstants
{
  // Memory Read Signal
  val MRD_0 = false.B
  val MRD_1 = true.B
  val MRD_X = false.B
  
  // Memory Write Signal
  val MWR_0 = false.B
  val MWR_1 = true.B
  val MWR_X = false.B

  // Memory Mask Type Signal
  val memMask_w = XLEN / 8
  val MSK_B     = Integer.parseInt("1", 2).asUInt(memMask_w.W)
  val MSK_H     = Integer.parseInt("11", 2).asUInt(memMask_w.W)
  val MSK_W     = Integer.parseInt("1111", 2).asUInt(memMask_w.W)
  val MSK_D     = Integer.parseInt("11111111", 2).asUInt(memMask_w.W)
  val MSK_X     = Integer.parseInt("11111111", 2).asUInt(memMask_w.W)  //TODO: How to be compatibale with 32bit machine?

  val memExt_w = 3
  val EXT_BS   = 0.asUInt(memExt_w.W)
  val EXT_BU   = 1.asUInt(memExt_w.W)
  val EXT_HS   = 2.asUInt(memExt_w.W)
  val EXT_HU   = 3.asUInt(memExt_w.W)
  val EXT_WS   = 4.asUInt(memExt_w.W)
  val EXT_WU   = 5.asUInt(memExt_w.W)
  val EXT_D    = 6.asUInt(memExt_w.W)
  val EXT_X    = 0.asUInt(memExt_w.W)
}

trait CSRAddrConstants
{
  val CSR_ADDR_w = 12

  // Machinne Information Registers
  val mvendoridAddr = 0xf11.asUInt(CSR_ADDR_w.W)
  val marchidAddr   = 0xf12.asUInt(CSR_ADDR_w.W)
  val mimpidAddr    = 0xf13.asUInt(CSR_ADDR_w.W)
  val mhartidAddr   = 0xf14.asUInt(CSR_ADDR_w.W)

  // Machine Trap Setup
  val mstatusAddr    = 0x300.asUInt(CSR_ADDR_w.W)
  val misaAddr       = 0x301.asUInt(CSR_ADDR_w.W)
  //val medelegAddr    = 0x302.asUInt(CSR_ADDR_w.W) //not exist
  //val midelegAddr    = 0x303.asUInt(CSR_ADDR_w.W) //not exist
  val mieAddr        = 0x304.asUInt(CSR_ADDR_w.W)
  val mtvecAddr      = 0x305.asUInt(CSR_ADDR_w.W)
  //val mcounterenAddr = 0x306.asUInt(CSR_ADDR_w.W) //not exist

  // Machine Trap Handling
  val mscratchAddr = 0x340.asUInt(CSR_ADDR_w.W)
  val mepcAddr     = 0x341.asUInt(CSR_ADDR_w.W)
  val mcauseAddr   = 0x342.asUInt(CSR_ADDR_w.W)
  val mtvalAddr    = 0x343.asUInt(CSR_ADDR_w.W)
  val mipAddr      = 0x344.asUInt(CSR_ADDR_w.W)
  //val mtinstAddr   = 0x34a.asUInt(CSR_ADDR_w.W)  //add in hypervisor
  //val mtval2Addr   = 0x34b.asUInt(CSR_ADDR_w.W)  //add in hypervisor

  // Machine Memory Protection

  // Machine Counter/Timers
  val mcycleAddr        = 0xb00.asUInt(CSR_ADDR_w.W)
  val minstretAddr      = 0xb02.asUInt(CSR_ADDR_w.W)
  val mhpmcounter3Addr  = 0xb03.asUInt(CSR_ADDR_w.W)
  val mhpmcounter4Addr  = 0xb04.asUInt(CSR_ADDR_w.W)
  val mhpmcounter5Addr  = 0xb05.asUInt(CSR_ADDR_w.W)
  val mhpmcounter6Addr  = 0xb06.asUInt(CSR_ADDR_w.W)
  val mhpmcounter7Addr  = 0xb07.asUInt(CSR_ADDR_w.W)
  val mhpmcounter8Addr  = 0xb08.asUInt(CSR_ADDR_w.W)
  val mhpmcounter9Addr  = 0xb09.asUInt(CSR_ADDR_w.W)
  val mhpmcounter10Addr = 0xb0a.asUInt(CSR_ADDR_w.W)
  val mhpmcounter11Addr = 0xb0b.asUInt(CSR_ADDR_w.W)
  val mhpmcounter12Addr = 0xb0c.asUInt(CSR_ADDR_w.W)
  val mhpmcounter13Addr = 0xb0d.asUInt(CSR_ADDR_w.W)
  val mhpmcounter14Addr = 0xb0e.asUInt(CSR_ADDR_w.W)
  val mhpmcounter15Addr = 0xb0f.asUInt(CSR_ADDR_w.W)
  val mhpmcounter16Addr = 0xb10.asUInt(CSR_ADDR_w.W)
  val mhpmcounter17Addr = 0xb11.asUInt(CSR_ADDR_w.W)
  val mhpmcounter18Addr = 0xb12.asUInt(CSR_ADDR_w.W)
  val mhpmcounter19Addr = 0xb13.asUInt(CSR_ADDR_w.W)
  val mhpmcounter20Addr = 0xb14.asUInt(CSR_ADDR_w.W)
  val mhpmcounter21Addr = 0xb15.asUInt(CSR_ADDR_w.W)
  val mhpmcounter22Addr = 0xb16.asUInt(CSR_ADDR_w.W)
  val mhpmcounter23Addr = 0xb17.asUInt(CSR_ADDR_w.W)
  val mhpmcounter24Addr = 0xb18.asUInt(CSR_ADDR_w.W)
  val mhpmcounter25Addr = 0xb19.asUInt(CSR_ADDR_w.W)
  val mhpmcounter26Addr = 0xb1a.asUInt(CSR_ADDR_w.W)
  val mhpmcounter27Addr = 0xb1b.asUInt(CSR_ADDR_w.W)
  val mhpmcounter28Addr = 0xb1c.asUInt(CSR_ADDR_w.W)
  val mhpmcounter29Addr = 0xb1d.asUInt(CSR_ADDR_w.W)
  val mhpmcounter30Addr = 0xb1e.asUInt(CSR_ADDR_w.W)
  val mhpmcounter31Addr = 0xb1f.asUInt(CSR_ADDR_w.W)

  // Machine Counter Setup
  val mcountinhibitAddr = 0x320.asUInt(CSR_ADDR_w.W)
  val mhpmevent3Addr    = 0x323.asUInt(CSR_ADDR_w.W)
  val mhpmevent4Addr    = 0x324.asUInt(CSR_ADDR_w.W)
  val mhpmevent5Addr    = 0x325.asUInt(CSR_ADDR_w.W)
  val mhpmevent6Addr    = 0x326.asUInt(CSR_ADDR_w.W)
  val mhpmevent7Addr    = 0x327.asUInt(CSR_ADDR_w.W)
  val mhpmevent8Addr    = 0x328.asUInt(CSR_ADDR_w.W)
  val mhpmevent9Addr    = 0x329.asUInt(CSR_ADDR_w.W)
  val mhpmevent10Addr   = 0x32a.asUInt(CSR_ADDR_w.W)
  val mhpmevent11Addr   = 0x32b.asUInt(CSR_ADDR_w.W)
  val mhpmevent12Addr   = 0x32c.asUInt(CSR_ADDR_w.W)
  val mhpmevent13Addr   = 0x32d.asUInt(CSR_ADDR_w.W)
  val mhpmevent14Addr   = 0x32e.asUInt(CSR_ADDR_w.W)
  val mhpmevent15Addr   = 0x32f.asUInt(CSR_ADDR_w.W)
  val mhpmevent16Addr   = 0x330.asUInt(CSR_ADDR_w.W)
  val mhpmevent17Addr   = 0x331.asUInt(CSR_ADDR_w.W)
  val mhpmevent18Addr   = 0x332.asUInt(CSR_ADDR_w.W)
  val mhpmevent19Addr   = 0x333.asUInt(CSR_ADDR_w.W)
  val mhpmevent20Addr   = 0x334.asUInt(CSR_ADDR_w.W)
  val mhpmevent21Addr   = 0x335.asUInt(CSR_ADDR_w.W)
  val mhpmevent22Addr   = 0x336.asUInt(CSR_ADDR_w.W)
  val mhpmevent23Addr   = 0x337.asUInt(CSR_ADDR_w.W)
  val mhpmevent24Addr   = 0x338.asUInt(CSR_ADDR_w.W)
  val mhpmevent25Addr   = 0x339.asUInt(CSR_ADDR_w.W)
  val mhpmevent26Addr   = 0x33a.asUInt(CSR_ADDR_w.W)
  val mhpmevent27Addr   = 0x33b.asUInt(CSR_ADDR_w.W)
  val mhpmevent28Addr   = 0x33c.asUInt(CSR_ADDR_w.W)
  val mhpmevent29Addr   = 0x33d.asUInt(CSR_ADDR_w.W)
  val mhpmevent30Addr   = 0x33e.asUInt(CSR_ADDR_w.W  )
  val mhpmevent31Addr   = 0x33f.asUInt(CSR_ADDR_w.W)
  
  // Debug/Trace Registers (shared with Debug Mode)

  // Debug Mode Registers

  // Memory-mapped Control Register
  // use custom read/write addr
  // TODO: check whether [11:10]==01 will be problem
  val mtimeMapAddr = 0x7c0.asUInt(CSR_ADDR_w.W)
  val mtimecmpMapAddr = 0x7c1.asUInt(CSR_ADDR_w.W)

  //if (DEBUG) {
  // ignore
  // To finish the testbench, we ignore operation to these CSR
  // instead of assert error
  val IgnoreCSR = true.B
  val medelegAddr    = 0x302.asUInt(CSR_ADDR_w.W)
  val midelegAddr    = 0x303.asUInt(CSR_ADDR_w.W)
  val satpAddr = 0x180.asUInt(CSR_ADDR_w.W)
  val pmpaddr0Addr = 0x3b0.asUInt(CSR_ADDR_w.W)
  val pmpcfg0Addr = 0x3a0.asUInt(CSR_ADDR_w.W)
  //}
}

trait CSRWriteTypeConstants
{
  val CSRWriteType_w = 3

  val CSRWT_U = 0.asUInt(CSRWriteType_w.W)  //Unable
  val CSRWT_W = 1.asUInt(CSRWriteType_w.W)  //Write
  val CSRWT_S = 2.asUInt(CSRWriteType_w.W)  //Set
  val CSRWT_C = 3.asUInt(CSRWriteType_w.W)  //Clear
  val CSRWT_IMP = 4.asUInt(CSRWriteType_w.W)  //implicit read write
  val CSRWT_X = 0.asUInt(CSRWriteType_w.W)
}

trait CSRImplementationCounstants
{
  // not implemented meanss the read of this csr is always 0
  // not exist means read write to it will cause exception
  val NOT_IMPLEMENTED = 0

}








