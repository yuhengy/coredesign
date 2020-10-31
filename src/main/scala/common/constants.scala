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
  CSRConstants
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

trait CSRConstants
{

  val CSRWriteType_w = 3
  val CSRWT_U = 0.asUInt(CSRWriteType_w.W)  //Unable
  val CSRWT_W = 1.asUInt(CSRWriteType_w.W)  //Write
  val CSRWT_S = 2.asUInt(CSRWriteType_w.W)  //Set
  val CSRWT_C = 3.asUInt(CSRWriteType_w.W)  //Clear
  val CSRWT_R = 3.asUInt(CSRWriteType_w.W)  //will read CSR
  val CSRWT_X = 0.asUInt(CSRWriteType_w.W)

  // Address of CSR
  val CSR_ADDR_w = 12
  // Machinne Information Registers
  val mvendoridAddr = 0xf11.asUInt(CSR_ADDR_w.W)
  val marchidAddr   = 0xf12.asUInt(CSR_ADDR_w.W)
  val mimpidAddr    = 0xf13.asUInt(CSR_ADDR_w.W)
  val mhartidAddr   = 0xf14.asUInt(CSR_ADDR_w.W)

  // Machine Trap Setup
  val mstatusAddr    = 0x300.asUInt(CSR_ADDR_w.W)
  val misaAddr       = 0x301.asUInt(CSR_ADDR_w.W)
  val medelegAddr    = 0x302.asUInt(CSR_ADDR_w.W)
  val midelegAddr    = 0x303.asUInt(CSR_ADDR_w.W)
  val mieAddr        = 0x304.asUInt(CSR_ADDR_w.W)
  val mtvecAddr      = 0x305.asUInt(CSR_ADDR_w.W)
  val mcounterenAddr = 0x306.asUInt(CSR_ADDR_w.W)
  val mstatushAddr   = 0x310.asUInt(CSR_ADDR_w.W)

  // Machine Trap Handling
  val mscratchAddr = 0x340.asUInt(CSR_ADDR_w.W)
  val mepcAddr     = 0x341.asUInt(CSR_ADDR_w.W)
  val mcauseAddr   = 0x342.asUInt(CSR_ADDR_w.W)
  val mtvalAddr    = 0x343.asUInt(CSR_ADDR_w.W)
  val mipAddr      = 0x344.asUInt(CSR_ADDR_w.W)
  val mtinstAddr   = 0x34a.asUInt(CSR_ADDR_w.W)
  val mtval2Addr   = 0x34b.asUInt(CSR_ADDR_w.W)

  // Machine Memory Protection

  // Machine Counter/Timers

  // Machine Counter Setup

  // Debug/Trace Registers (shared with Debug Mode)

  // Debug Mode Registers

  //if (DEBUG) {
  // ignore
  // To finish the testbench, we ignore operation to these CSR
  // instead of assert error
  val IgnoreCSR = true.B
  val satpAddr = 0x180.asUInt(CSR_ADDR_w.W)
  val pmpaddr0Addr = 0x3b0.asUInt(CSR_ADDR_w.W)
  val pmpcfg0Addr = 0x3a0.asUInt(CSR_ADDR_w.W)
  //}
}









