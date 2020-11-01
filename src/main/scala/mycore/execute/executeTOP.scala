package mycore
package execute

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

import common.constants._
import common.configurations._
import common.memWriteIO

import mycore.decode.decToExeDataIO
import mycore.decode.decToExeCtrlIO

class executeTOP extends Module
{
  val io = IO(new Bundle{
  //decToExeData
    val inDataIO = Input(new decToExeDataIO)
  //decToExeCtrl
    val inCtrlIO = Flipped(Decoupled(new decToExeCtrlIO))

  //exeToMemData
    val outDataIO = Output(new exeToMemDataIO)
  //exeToMenCtrl
    val outCtrlIO = Decoupled(new exeToMemCtrlIO)

  //exeToPreifFeedback
    val brjmpTarget = Output(UInt(XLEN.W))
    val jmpRTarget  = Output(UInt(XLEN.W))
    val PCSel       = Output(UInt(PCSel_w.W))
  //exeOutKill
    val exeOutKill  = Output(Bool())

  //exeToDecFeedback
    val exeDest = Valid(UInt(WID_REG_ADDR.W))
    val exeCSRWriteType = Output(UInt(CSRWriteType_w.W))

  //toRam
    val dataReadIO = new Bundle{
      val reqReady = Input(Bool())
      val addr = Output(UInt(XLEN.W))
      val en = Output(Bool())
    }
    val dataWriteIO = new Bundle {
      val reqReady = Input(Bool())
      val addr = Output(UInt(XLEN.W))
      val data = Output(UInt(XLEN.W))
      val mask = Output(UInt((XLEN/8).W))
      val en = Output(Bool())
    }
  })

//--------------execute global status start--------------
//input
  object stateEnum extends ChiselEnum {
    val reset, idle, regIsUpdated = Value
  }
  val state = RegInit(stateEnum.reset)
//output
  val regDataIO = Reg(new decToExeDataIO)
  // regCtrlIO
//private
  val regCtrlIO_r = RegInit(decToExeCtrlIO.init)
  when (io.inCtrlIO.valid && io.inCtrlIO.ready) {
    regDataIO <> io.inDataIO
    regCtrlIO_r <> io.inCtrlIO.bits
  }
  val regCtrlIO = Mux(state === stateEnum.regIsUpdated, regCtrlIO_r, decToExeCtrlIO.init)
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

//--------------memWrite data start--------------
//output
  val memWriteData  = MuxCase(regDataIO.rfRs2Data, Array(
                      (regCtrlIO.memMask === MSK_B) -> Fill(8, regDataIO.rfRs2Data(7,0)),
                      (regCtrlIO.memMask === MSK_H) -> Fill(4, regDataIO.rfRs2Data(15,0)),
                      (regCtrlIO.memMask === MSK_W) -> Fill(2, regDataIO.rfRs2Data(31,0)),
                      (regCtrlIO.memMask === MSK_D) ->         regDataIO.rfRs2Data
                      ))
  val memWriteMask  = regCtrlIO.memMask << aluOut(2,0)
//^^^^^^^^^^^^^^memWrite data end^^^^^^^^^^^^^^

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
  
  PCSel := Mux(regCtrlIO.exception        , PC_EXC,
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
           ))))))))))
//^^^^^^^^^^^^^^branch/jump select end^^^^^^^^^^^^^^

//--------------state machine start--------------
//output
  //object stateEnum extends ChiselEnum {
  //  val reset, idle, regIsUpdated = Value
  //}
  //val state = RegInit(stateEnum.reset)

//private
  switch (state) {
    is (stateEnum.reset) {
      state := stateEnum.idle
    }
    is (stateEnum.idle) {
      when (io.inCtrlIO.valid && io.inCtrlIO.ready)
        {state := stateEnum.regIsUpdated}
    }
    is (stateEnum.regIsUpdated) {
      when (io.inCtrlIO.valid && io.inCtrlIO.ready)
        {state := stateEnum.regIsUpdated}.
      elsewhen (io.outCtrlIO.valid && io.outCtrlIO.ready)
        {state := stateEnum.idle}
    }
  }
//^^^^^^^^^^^^^^state machine end^^^^^^^^^^^^^^

//--------------control signal start--------------
  val stall = state === stateEnum.regIsUpdated &&  //TODO: this line should be skiped
              (regCtrlIO.memRd && !(io.dataReadIO.reqReady && io.outCtrlIO.ready) ||
               regCtrlIO.memWr && !(io.dataWriteIO.reqReady && io.outCtrlIO.ready)) ||
              alu.io.stall
  //TODO: check whether stall need consider reset state

  io.inCtrlIO.ready := state === stateEnum.reset || state === stateEnum.idle ||
                       io.outCtrlIO.ready && io.outCtrlIO.valid
  io.outCtrlIO.valid := state === stateEnum.regIsUpdated && !stall
//^^^^^^^^^^^^^^control signal end^^^^^^^^^^^^^^

//--------------io.output start--------------
//exeToMemData
  io.outDataIO.PC           := regDataIO.PC
  io.outDataIO.inst         := regDataIO.inst
  io.outDataIO.wbData       := wbData
  io.outDataIO.wbAddr       := regDataIO.wbAddr
  io.outDataIO.addrAlign    := aluOut(addrAlign_w-1, 0)
  io.outDataIO.CSRWriteData := regDataIO.aluop1
  io.outDataIO.CSRAddr      := regDataIO.aluop2(CSR_ADDR_w-1, 0)

//decToExeCtrl
  io.outCtrlIO.bits.wbSel := regCtrlIO.wbSel
  io.outCtrlIO.bits.rfWen := regCtrlIO.rfWen
  io.outCtrlIO.bits.memRd := regCtrlIO.memRd
  io.outCtrlIO.bits.memWr := regCtrlIO.memWr
  io.outCtrlIO.bits.memMask := regCtrlIO.memMask
  io.outCtrlIO.bits.memExt  := regCtrlIO.memExt
  io.outCtrlIO.bits.cs_val_inst := regCtrlIO.cs_val_inst
  io.outCtrlIO.bits.CSRWriteType := regCtrlIO.CSRWriteType
  if (DEBUG) {
    io.outCtrlIO.bits.goodTrapNemu := regCtrlIO.goodTrapNemu
  }

//exeToIfFeedback
  io.brjmpTarget := brjmpTarget
  io.jmpRTarget  := jmpRTarget
  io.PCSel       := PCSel
//exeOutKill
  io.exeOutKill  := (PCSel =/= PC_4) && state === stateEnum.regIsUpdated

//exeToDecFeedback
  io.exeDest.bits  := regDataIO.wbAddr
  io.exeDest.valid := regCtrlIO.rfWen
  io.exeCSRWriteType := regCtrlIO.CSRWriteType

//toRam
  io.dataReadIO.addr  := Cat(aluOut(XLEN-1, addrAlign_w), Fill(addrAlign_w, 0.U))
  io.dataReadIO.en    := regCtrlIO.memRd && io.outCtrlIO.ready
  io.dataWriteIO.addr := Cat(aluOut(XLEN-1, addrAlign_w), Fill(addrAlign_w, 0.U))
  io.dataWriteIO.data := memWriteData
  io.dataWriteIO.mask := memWriteMask
  io.dataWriteIO.en   := regCtrlIO.memWr && io.outCtrlIO.ready
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

}
