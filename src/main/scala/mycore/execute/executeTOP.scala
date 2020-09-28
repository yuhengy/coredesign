package mycore
package execute

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._

import common.constants._
import common.configurations._
import common.memWriteIO

import mycore.decode.decToExeDataIO
import mycore.decode.decToExeCtrlIO

class executeTOP extends Module
{
  val io = IO(new Bundle{
  //decToExeData
    val decToExeDataIO = Input(new decToExeDataIO)
  //decToExeCtrl
    val decToExeCtrlIO = Flipped(Decoupled(new decToExeCtrlIO))

  //exeToMemData
    val exeToMemDataIO = Output(new exeToMemDataIO)
  //exeToMenCtrl
    val exeToMemCtrlIO = Decoupled(new exeToMemCtrlIO)

  //exeToIfFeedback
    val brjmpTarget = Output(UInt(XLEN.W))
    val jmpRTarget  = Output(UInt(XLEN.W))
    val PCSel       = Output(UInt(PCSel_w.W))
  //exeOutKill
    val exeOutKill  = Output(Bool())

  //exeToDecFeedback
    val exeDest = Valid(UInt(WID_REG_ADDR.W))

  //toRam
    val dataReadIO = new Bundle{
      val addr = Output(UInt(XLEN.W))
      val en = Output(Bool())
    }
    val dataWriteIO = new memWriteIO
  })

//--------------execute global status start--------------
  val regDataIO = Reg(new decToExeDataIO)
  val regCtrlIO = RegInit({
    val temp = Wire(new decToExeCtrlIO)
    temp.init
    temp
  })
  when (io.decToExeCtrlIO.valid && io.decToExeCtrlIO.ready) {
    regDataIO <> io.decToExeDataIO
    regCtrlIO <> io.decToExeCtrlIO.bits
  }
//^^^^^^^^^^^^^^execute global status end^^^^^^^^^^^^^^

//--------------alu start--------------
//output
  val aluOut   = Wire(UInt(XLEN.W))
  val adderOut = Wire(UInt(XLEN.W))
//private
  val alu      = Module(new alu)

  alu.io.aluFunc := regCtrlIO.aluFunc
  alu.io.op1     := regDataIO.aluop1
  alu.io.op2     := regDataIO.aluop2
  aluOut         := alu.io.out
  adderOut       := alu.io.adderOut
//^^^^^^^^^^^^^^alu end^^^^^^^^^^^^^^

//--------------writeBack data start--------------
//input 
  // aluOut
//output
  val wbData  = Wire(UInt(XLEN.W))
//private
  val PCPlus4  = (regDataIO.PC + 4.U)(XLEN-1,0)

  wbData := Mux((regCtrlIO.wbSel === WB_PC4), PCPlus4, aluOut)
//^^^^^^^^^^^^^^writeBack data end^^^^^^^^^^^^^^

//--------------branch/jump target start--------------
//input 
  // adderOut
//output
  val brjmpTarget = Wire(UInt(XLEN.W))
  val jmpRTarget  = Wire(UInt(XLEN.W))

  brjmpTarget := regDataIO.PC + regDataIO.aluop2
  jmpRTarget  := adderOut
//^^^^^^^^^^^^^^branch/jump target end^^^^^^^^^^^^^^

//--------------branch/jump select start--------------
//output
  val PCSel = Wire(UInt(PCSel_w.W))

//private
  val brEq  = (regDataIO.aluop1 === regDataIO.rfRs2Data)
  //TODO: Why in the soder graph, should use decode.regDataIO.rfRs2Data instead of execute.regDataIO.rfRs2Data?
  val brLt  = (regDataIO.aluop1.asSInt() < regDataIO.rfRs2Data.asSInt())
  val brLtu = (regDataIO.aluop1.asUInt() < regDataIO.rfRs2Data.asUInt())
  
  PCSel := //Mux(io.ctl.pipeline_kill     , PC_EXC,
           Mux(regCtrlIO.brType === BR_N  , PC_4,
           Mux(regCtrlIO.brType === BR_NE , Mux(!brEq,  PC_BRJMP, PC_4),
           Mux(regCtrlIO.brType === BR_EQ , Mux( brEq,  PC_BRJMP, PC_4),
           Mux(regCtrlIO.brType === BR_GE , Mux(!brLt,  PC_BRJMP, PC_4),
           Mux(regCtrlIO.brType === BR_GEU, Mux(!brLtu, PC_BRJMP, PC_4),
           Mux(regCtrlIO.brType === BR_LT , Mux( brLt,  PC_BRJMP, PC_4),
           Mux(regCtrlIO.brType === BR_LTU, Mux( brLtu, PC_BRJMP, PC_4),
           Mux(regCtrlIO.brType === BR_J  , PC_BRJMP,
           Mux(regCtrlIO.brType === BR_JR , PC_JALR,
                                            PC_4
           )))))))))//)
//^^^^^^^^^^^^^^branch/jump select end^^^^^^^^^^^^^^

//--------------stall&kill start--------------
  val stall = false.B
  val regIsUpdated = RegInit(false.B)
  when (io.decToExeCtrlIO.valid && io.decToExeCtrlIO.ready)
    {regIsUpdated := true.B}.
  elsewhen (io.exeToMemCtrlIO.valid && io.exeToMemCtrlIO.ready)
    {regIsUpdated := false.B}  //TODO: check otherwise can be left

  io.decToExeCtrlIO.ready := true.B
  io.exeToMemCtrlIO.valid := regIsUpdated && !stall
//^^^^^^^^^^^^^^stall&kill end^^^^^^^^^^^^^^

//--------------io.output start--------------
//exeToMemData
  io.exeToMemDataIO.PC     := regDataIO.PC
  io.exeToMemDataIO.inst   := regDataIO.inst
  io.exeToMemDataIO.wbData := wbData
  io.exeToMemDataIO.wbAddr := regDataIO.wbAddr

//decToExeCtrl
  io.exeToMemCtrlIO.bits.wbSel := regCtrlIO.wbSel
  io.exeToMemCtrlIO.bits.rfWen := regCtrlIO.rfWen
  io.exeToMemCtrlIO.bits.cs_val_inst := regCtrlIO.cs_val_inst

//exeToIfFeedback
  io.brjmpTarget := brjmpTarget
  io.jmpRTarget  := jmpRTarget
  io.PCSel       := PCSel
//exeOutKill
  io.exeOutKill  := (PCSel =/= PC_4) && regIsUpdated

//exeToDecFeedback
  io.exeDest.bits  := regDataIO.wbAddr
  io.exeDest.valid := regCtrlIO.rfWen

//toRam
  io.dataReadIO.addr  := aluOut
  io.dataReadIO.en    := regCtrlIO.memRd
  io.dataWriteIO.addr := aluOut
  io.dataWriteIO.data := regDataIO.rfRs2Data
  io.dataWriteIO.mask := regCtrlIO.memMask
  io.dataWriteIO.en   := regCtrlIO.memWr
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

}
