package common

import chisel3._
import chisel3.util.log2Ceil

object constants extends
  commonConstants with
  RISCVConstants with
  PCConstants with
  instConstants with
  brTypeConstants with
  regfileConstants with
  aluFuncConstants with
  memTypeConstants with
  opSelConstants
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
  val OP1_PC   = 1.asUInt(op1Sel_w.W) // PC
  val OP1_IMZ  = 2.asUInt(op1Sel_w.W) // Zero-extended Immediate from RS1 field, for use by CSRI instructions
  val OP1_X    = 0.asUInt(op1Sel_w.W)

  val op2Sel_w   = 3
  val OP2_RS2    = 0.asUInt(op2Sel_w.W) // Register Source #2
  val OP2_ITYPE  = 1.asUInt(op2Sel_w.W) // immediate, I-type
  val OP2_STYPE  = 2.asUInt(op2Sel_w.W) // immediate, S-type
  val OP2_SBTYPE = 3.asUInt(op2Sel_w.W) // immediate, B
  val OP2_UTYPE  = 4.asUInt(op2Sel_w.W) // immediate, U-type
  val OP2_UJTYPE = 5.asUInt(op2Sel_w.W) // immediate, J-type
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
  val wbSel_w = 2
  val WB_ALU  = 0.asUInt(wbSel_w.W)
  val WB_MEM  = 1.asUInt(wbSel_w.W)
  val WB_PC4  = 2.asUInt(wbSel_w.W)
  val WB_CSR  = 3.asUInt(wbSel_w.W)
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
  val ALU_AND    = 5.asUInt(aluFunc_w.W)
  val ALU_OR     = 6.asUInt(aluFunc_w.W)
  val ALU_XOR    = 7.asUInt(aluFunc_w.W)
  val ALU_SLT    = 8.asUInt(aluFunc_w.W)
  val ALU_SLTU   = 9.asUInt(aluFunc_w.W)
  val ALU_COPY_1 = 10.asUInt(aluFunc_w.W)
  val ALU_COPY_2 = 11.asUInt(aluFunc_w.W)
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
  val memMask_w = 3
  val MSK_B     = 0.asUInt(memMask_w.W)
  val MSK_BU    = 1.asUInt(memMask_w.W)
  val MSK_H     = 2.asUInt(memMask_w.W)
  val MSK_HU    = 3.asUInt(memMask_w.W)
  val MSK_W     = 4.asUInt(memMask_w.W)
  val MSK_X     = 4.asUInt(memMask_w.W)

}










