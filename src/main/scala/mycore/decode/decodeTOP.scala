package mycore
package decode

import chisel3._
import chisel3.util.MuxCase

import common.configurations._
import common.constants._
//TODO: It seems we can move configurations/constants for wide into IO totally.
import mycore.execute.decToExeDataIO
import mycore.execute.decToExeCtrlIO

class decodeTOP extends Module
{
  io = IO(new Bundle{
  //ifToDecData
    val ifToDecDataIO = new ifToDecDataIO
  //decToExeCtrl

  //decToExeData
    val decToExeData = Flipped(new decToExeDataIO)
  //exeToMenCtrl
    val decToExeCtrlIO = Flipped(new decToExeCtrlIO)
  })

//--------------decode global status start--------------
  val regDataIO = Reg(new Packet)
  regDataIO <> io.ifToDecDataIO
//^^^^^^^^^^^^^^decode global status end^^^^^^^^^^^^^^

//--------------inst decode start--------------
//output
  // decoder.io.allCtrlIO
//private
  val decoder = Module(new decoder)
  decoder.io.inst := regDataIO.inst
//^^^^^^^^^^^^^^inst decode end^^^^^^^^^^^^^^

//--------------regfile read start--------------
//output
  val rfRs1Data = Wire(UInt(XLEN.W))
  val rfRs2Data = Wire(UInt(XLEN.W))
  val wbAddr    = regDataIO.inst(RD_MSB, RD_LSB)
//private
  val decRsAddr2 = regDataIO.inst(RS2_MSB, RS2_LSB)
  val decRsAddr1 = regDataIO.inst(RS1_MSB, RS1_LSB)
  val regfile = Module(new regfile())

  regfile.io.rAddr2 := decRsAddr2
  regfile.io.rAddr1 := decRsAddr1
  rfRs1Data := regfile.io.rs1_data
  rfRs2Data := regfile.io.rs2_data
  regfile.io.wAddr := wb_reg_wbaddr
  regfile.io.wData := wb_reg_wbdata
  regfile.io.wEn   := wb_reg_ctrl_rf_wen
//^^^^^^^^^^^^^^regfile read end^^^^^^^^^^^^^^

//--------------immediate start--------------
//output
   val immZ          = Wire(UInt(XLEN.W))
   val immItypeSext  = Wire(UInt(XLEN.W))
   val immStypeSext  = Wire(UInt(XLEN.W))
   val immSbtypeSext = Wire(UInt(XLEN.W))
   val immUtypeSext  = Wire(UInt(XLEN.W))
   val immUjtypeSext = Wire(UInt(XLEN.W))
//private
   val imm_itype  = dec_reg_inst(31,20)
   val imm_stype  = Cat(dec_reg_inst(31,25), dec_reg_inst(11,7))
   val imm_sbtype = Cat(dec_reg_inst(31), dec_reg_inst(7), dec_reg_inst(30, 25), dec_reg_inst(11,8))
   val imm_utype  = dec_reg_inst(31, 12)
   val imm_ujtype = Cat(dec_reg_inst(31), dec_reg_inst(19,12), dec_reg_inst(20), dec_reg_inst(30,21))

   val immZ = Cat(Fill(59,0.U), dec_reg_inst(19,15))
   val immItypeSext  = Cat(Fill(52,imm_itype(11)), imm_itype)
   val immStypeSext  = Cat(Fill(52,imm_stype(11)), imm_stype)
   val immSbtypeSext = Cat(Fill(51,imm_sbtype(11)), imm_sbtype, 0.U)
   val immUtypeSext  = Cat(Fill(32,imm_utype(19)), imm_utype, Fill(12,0.U))
   val immUjtypeSext = Cat(Fill(43,imm_ujtype(19)), imm_ujtype, 0.U)
//^^^^^^^^^^^^^^immediate end^^^^^^^^^^^^^^

//--------------operand1 start--------------
//input
  // decoder.io.allCtrlIO.op1Sel
  // rf_rs1_data
  // imm_z
  // dec_reg_pc
//output
  val aluop1 = MuxCase(0.U, Array(
               (decoder.io.allCtrlIO.op1Sel === OP1_RS1) -> rfRs1Data,
               (decoder.io.allCtrlIO.op1Sel === OP1_IMZ) -> immZ,
               (decoder.io.allCtrlIO.op1Sel === OP1_PC)  -> regDataIO.PC
               ))
//^^^^^^^^^^^^^^operand1 end^^^^^^^^^^^^^^

//--------------operand2 start--------------
//input
  // decoder.io.allCtrlIO.op2Sel
  // rf_rs2_data
  // immItypeSext
  // immStypeSext
  // immSbtypeSext
  // immUtypeSext
  // immUjtypeSext
//output
  val aluop2 = MuxCase(0.U, Array(
              (decoder.io.allCtrlIO.op2Sel === OP2_RS2)    -> rfRs2Data,
              (decoder.io.allCtrlIO.op2Sel === OP2_ITYPE)  -> immItypeSext,
              (decoder.io.allCtrlIO.op2Sel === OP2_STYPE)  -> immStypeSext,
              (decoder.io.allCtrlIO.op2Sel === OP2_SBTYPE) -> immSbtypeSext,
              (decoder.io.allCtrlIO.op2Sel === OP2_UTYPE)  -> immUtypeSext,
              (decoder.io.allCtrlIO.op2Sel === OP2_UJTYPE) -> immUjtypeSext
              ))
//^^^^^^^^^^^^^^operand2 end^^^^^^^^^^^^^^

//--------------io.output start--------------
  io.decToExeData           <> regDataIO
  io.decToExeData.aluop1    := aluop1
  io.decToExeData.aluop2    := aluop2
  io.decToExeData.rfRs2Data := rfRs2Data
  io.decToExeData.wbAddr    := wbAddr
  io.decToExeCtrlIO         <> decoder.io.allCtrlIO
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

}
