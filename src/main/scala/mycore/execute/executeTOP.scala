package mycore
package execute

import chisel3._

import common.constants._
import common.configurations._
import mycore.memory.exeToMenDataIO
import mycore.memory.exeToMemCtrlIO

class executeTOP extends Module
{
  io = IO(new Bundle{
  //decToExeData
    val decToExeDataIO = new decToExeDataIO
  //decToExeCtrl
    val decToExeCtrlIO = new decToExeCtrlIO

  //exeToMemData
    val exeToMenDataIO = Flipped(new exeToMenDataIO)
  //exeToMenCtrl
    val exeToMemCtrlIO = Flipped(new exeToMemCtrlIO)

  //exeToIfFeedback
    val brjmpTarget = Output(UInt(XLEN.W))
    val jmpRTarget  = Output(UInt(XLEN.W))
    val PCSel       = Output(UInt(PCSel_w.W))

  //toRam
    val dataReadIO = new Bundle{
      val addr = Output(UInt(XLEN.W))
      val en = Output(Bool())
    }
    val dataWriteIO = new memWriteIO
  })

//--------------execute global status start--------------
  val regDataIO = Reg(new Packet)
  regDataIO <> io.decToExeDataIO

  val regCtrlIO = RegInit({
    val temp = wire(new decToExeCtrlIO)
    temp.init
    temp
  })
  regCtrlIO <> io.decToExeCtrlIO
//^^^^^^^^^^^^^^execute global status end^^^^^^^^^^^^^^

//--------------alu start--------------
//output
  val aluOut   = Wire(UInt(XLEN.W))
  val adderOut = Wire(UInt(XLEN.W))
//private
  val alu      = Module(new alu)

  alu.aluFunc := io.aluFunc
  alu.op1     := regDataIO.aluop1
  alu.op2     := regDataIO.aluop2
  aluOut      := alu.out
  adderOut    := alu.adderOut
//^^^^^^^^^^^^^^alu end^^^^^^^^^^^^^^

//--------------writeBack data start--------------
//input 
  // aluOut
//output
  val wbData  = Wire(UInt(XLEN.W))
//private
  val PCPlus4  = (exe_reg_pc + 4.U)(XLEN-1,0)

  wbData := Mux((regCtrlIO.wbSel === WB_PC4), PCPlus4, aluOut)
//^^^^^^^^^^^^^^writeBack data end^^^^^^^^^^^^^^

//--------------branch/jump target start--------------
//input 
  // adderOut
//output
  val brjmpTarget = Wire(UInt(XLEN.W))
  val jmpRTarget  = Wire(UInt(XLEN.W))

  brjmpTarget := regDataIO.PC + regAluop2
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

//--------------io.output start--------------
//exeToMemData
  io.exeToMemDataIO <> regDataIO
  io.exeToMemwbData := wbData  //TODO: check this will rewrite the assignment above

//decToExeCtrl
  io.exeToMemCtrlIO <> regCtrlIO

//exeToIfFeedback
  io.brjmpTarget   := brjmpTarget
  io.jmpRTarget := jmpRTarget
  io.PCSel := PCSel

//toRam
  io.dataReadIO.addr := aluOut
  io.dataReadIO.en := regCtrlIO.memRd
  io.dataWriteIO.addr := aluOut
  io.dataWriteIO.data := regDataIO.rfRs2Data
  io.dataWriteIO.mask := regCtrlIO.memMask
  io.dataWriteIO.en := regCtrlIO.memWr
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^






