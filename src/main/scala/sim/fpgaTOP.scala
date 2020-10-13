package sim

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils

import common.constants._
import common.configurations._
import mycore.{mycoreTOP, mycoreTOPIO}

class fpgaTOP extends Module {
  val io = IO(new Bundle{
    val goodTrap = Output(Bool())

    val diffTest_addra = Output(UInt(FPGADIFFDEPTH_w.W))
    val diffTest_to_dina = Output(UInt(FPGADIFFDATA_w.W))
    val diffTest_from_douta = Input(UInt(FPGADIFFDATA_w.W))
    val diffTest_to_ena = Output(Bool())
    val diffTest_to_wea = Output(UInt(FPGADIFFMASK_w.W))


    val inst_to_addra = Output(UInt(FPGAINSTDEPTH_w.W))
    val inst_to_dina = Output(UInt(XLEN.W))
    val inst_from_douta = Input(UInt(XLEN.W))
    val inst_to_ena = Output(Bool())
    val inst_to_wea = Output(UInt(memMask_w.W))
    val data_to_addra = Output(UInt(FPGADATADEPTH_w.W))
    val data_to_dina = Output(UInt(XLEN.W))
    val data_from_douta = Input(UInt(XLEN.W))
    val data_to_ena = Output(Bool())
    val data_to_wea = Output(UInt(memMask_w.W))
  })

  //goodTrapIO
  io := DontCare
  val goodTrapWire = Wire(Bool())
  goodTrapWire := DontCare
  val goodTrapReg = RegInit(false.B)
  BoringUtils.addSink(goodTrapWire, "GoodTrapNemu")
  when (goodTrapWire) {
    goodTrapReg := true.B
  }
  io.goodTrap := goodTrapReg

  //diffTestIO_PC
  val haveCommit = Wire(Bool())
  val haveCommit_r = RegNext(haveCommit, false.B)
  haveCommit := DontCare
  val commitCounter = RegInit(UInt(FPGADIFFDEPTH_w.W), 0.U)
  BoringUtils.addSink(haveCommit, "diffTestCommit")
  when (haveCommit_r && commitCounter=/=FPGADIFFDEPTHMAX.asUInt(FPGADIFFDEPTH_w.W)) {
    commitCounter := commitCounter + 8.asUInt(FPGADIFFDEPTH_w.W)
  }
  io.diffTest_addra := commitCounter
  val diffTestRegfile = Wire(Vec(NUM_REG, UInt(XLEN.W)))
  val diffTestPC = Wire(UInt(XLEN.W))
  diffTestRegfile := DontCare
  diffTestPC := DontCare
  BoringUtils.addSink(diffTestRegfile, "diffTestRegfile")
  BoringUtils.addSink(diffTestPC, "diffTestPC")
  io.diffTest_to_dina := Cat(diffTestRegfile.asUInt(), diffTestPC)(FPGADIFFDATA_w-1, 0)  //the first element appearing in the least-significant bits of the result
  //io.diffTest_from_douta
  io.diffTest_to_ena := haveCommit_r
  io.diffTest_to_wea := Mux(haveCommit_r, Fill(FPGADIFFMASK_w, 1.U), Fill(FPGADIFFMASK_w, 0.U))

  //BRAM
  val mycoreTOP = Module(new mycoreTOP())

  io.inst_to_addra := mycoreTOP.io.instReadIO.addr(FPGAINSTDEPTH_w-1,0)
  io.inst_to_dina := Fill(XLEN,0.U)
  mycoreTOP.io.instReadIO.data := io.inst_from_douta
  io.inst_to_ena := mycoreTOP.io.instReadIO.en
  io.inst_to_wea := Fill(memMask_w,0.U)

  io.data_to_addra := Mux(mycoreTOP.io.dataWriteIO.en, mycoreTOP.io.dataWriteIO.addr(FPGADATADEPTH_w-1,0),
                                                       mycoreTOP.io.dataReadIO.addr(FPGADATADEPTH_w-1,0))
  io.data_to_dina := mycoreTOP.io.dataWriteIO.data
  mycoreTOP.io.dataReadIO.data := io.data_from_douta
  io.data_to_ena := mycoreTOP.io.dataReadIO.en | mycoreTOP.io.dataWriteIO.en
  io.data_to_wea := Mux(mycoreTOP.io.dataWriteIO.en, mycoreTOP.io.dataWriteIO.mask, Fill(memMask_w,0.U))

}

object elaborateFPGA extends App {
  (new stage.ChiselStage).execute(args, Seq(stage.ChiselGeneratorAnnotation(() => new fpgaTOP)))
}