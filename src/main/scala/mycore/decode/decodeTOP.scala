package mycore
package decode

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._

import common.configurations._
import common.constants._
//TODO: It seems we can move configurations/constants for wide into IO totally.
import mycore.instFetch.ifToDecDataIO

class decodeTOP extends Module
{
  val io = IO(new Bundle{
  //ifToDecData
    val ifToDecDataIO = Input(new ifToDecDataIO)
  //ifToDecCtrl
    val ifToDecCtrlIO = Flipped(Decoupled(new Bundle{}))

  //decToExeData
    val decToExeDataIO = Output(new decToExeDataIO)
  //decToExeCtrl
    val decToExeCtrlIO = Decoupled(new decToExeCtrlIO)

  //*ToDexFeedback
    val exeDest = Flipped(Valid(UInt(WID_REG_ADDR.W)))
    val memDest = Flipped(Valid(UInt(WID_REG_ADDR.W)))
    
  //wbToDecRegWrite
    val wbToDecWbAddr = Input(UInt(WID_REG_ADDR.W))
    val wbToDecWbdata = Input(UInt(XLEN.W))
    val wbToDecWRfWen = Input(Bool())
  })

//--------------decode global status start--------------
  val regDataIO = Reg(new ifToDecDataIO)
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
  val decRsAddr2 = regDataIO.inst(RS2_MSB, RS2_LSB)
  val decRsAddr1 = regDataIO.inst(RS1_MSB, RS1_LSB)
  val rfRs2Data = Wire(UInt(XLEN.W))
  val rfRs1Data = Wire(UInt(XLEN.W))
  val wbAddr    = regDataIO.inst(RD_MSB, RD_LSB)
//private
  val regfile = Module(new regfile())

  regfile.io.rAddr2 := decRsAddr2
  regfile.io.rAddr1 := decRsAddr1
  rfRs2Data := regfile.io.rData2
  rfRs1Data := regfile.io.rData1
  regfile.io.wAddr := io.wbToDecWbAddr
  regfile.io.wData := io.wbToDecWbdata
  regfile.io.wEn   := io.wbToDecWRfWen
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
   val imm_itype  = regDataIO.inst(31,20)
   val imm_stype  = Cat(regDataIO.inst(31,25), regDataIO.inst(11,7))
   val imm_sbtype = Cat(regDataIO.inst(31), regDataIO.inst(7), regDataIO.inst(30, 25), regDataIO.inst(11,8))
   val imm_utype  = regDataIO.inst(31, 12)
   val imm_ujtype = Cat(regDataIO.inst(31), regDataIO.inst(19,12), regDataIO.inst(20), regDataIO.inst(30,21))

   immZ          := Cat(Fill(59,0.U), regDataIO.inst(19,15))
   immItypeSext  := Cat(Fill(52,imm_itype(11)), imm_itype)
   immStypeSext  := Cat(Fill(52,imm_stype(11)), imm_stype)
   immSbtypeSext := Cat(Fill(51,imm_sbtype(11)), imm_sbtype, 0.U)
   immUtypeSext  := Cat(Fill(32,imm_utype(19)), imm_utype, Fill(12,0.U))
   immUjtypeSext := Cat(Fill(43,imm_ujtype(19)), imm_ujtype, 0.U)
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

//--------------stall&kill start--------------
  val stall = io.exeDest.valid && ((decoder.io.allCtrlIO.op2Sel===OP2_RS2) && io.exeDest.bits===decRsAddr2 ||
                                   (decoder.io.allCtrlIO.op1Sel===OP1_RS1) && io.exeDest.bits===decRsAddr1) ||
              io.memDest.valid && ((decoder.io.allCtrlIO.op2Sel===OP2_RS2) && io.memDest.bits===decRsAddr2 ||
                                   (decoder.io.allCtrlIO.op1Sel===OP1_RS1) && io.memDest.bits===decRsAddr1) ||
              io.wbToDecWRfWen && ((decoder.io.allCtrlIO.op2Sel===OP2_RS2) && io.wbToDecWbAddr===decRsAddr2 ||
                                   (decoder.io.allCtrlIO.op1Sel===OP1_RS1) && io.wbToDecWbAddr===decRsAddr1)
  val regIsUpdated = RegInit(false.B)
  when (io.ifToDecCtrlIO.valid && io.ifToDecCtrlIO.ready)
  {regIsUpdated := true.B}.
  elsewhen (io.decToExeCtrlIO.valid && io.decToExeCtrlIO.ready)
  {regIsUpdated := false.B}  //TODO: check otherwise can be left

  io.ifToDecCtrlIO.ready := true.B
  io.decToExeCtrlIO.valid := regIsUpdated && !stall
//^^^^^^^^^^^^^^stall&kill end^^^^^^^^^^^^^^

//--------------io.output start--------------
  io.decToExeDataIO.PC        := regDataIO.PC
  io.decToExeDataIO.inst      := regDataIO.inst
  io.decToExeDataIO.aluop1    := aluop1
  io.decToExeDataIO.aluop2    := aluop2
  io.decToExeDataIO.rfRs2Data := rfRs2Data
  io.decToExeDataIO.wbAddr    := wbAddr
  io.decToExeCtrlIO.bits.brType    := decoder.io.allCtrlIO.brType
  io.decToExeCtrlIO.bits.aluFunc   := decoder.io.allCtrlIO.aluFunc
  io.decToExeCtrlIO.bits.wbSel     := decoder.io.allCtrlIO.wbSel
  io.decToExeCtrlIO.bits.rfWen     := decoder.io.allCtrlIO.rfWen && wbAddr=/=0.U  //sothat stall judgement donot consider io.wbToDecWbAddr===0.U
  io.decToExeCtrlIO.bits.memRd     := decoder.io.allCtrlIO.memRd
  io.decToExeCtrlIO.bits.memWr     := decoder.io.allCtrlIO.memWr
  io.decToExeCtrlIO.bits.memMask   := decoder.io.allCtrlIO.memMask
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

}
