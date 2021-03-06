package sim

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util.experimental.BoringUtils

import mycore.{mycoreTOP, mycoreTOPIO}

class verilatorTOP extends Module {
  val io = IO(new Bundle{
    val goodTrapIO = new goodTrapIO
    val diffTestIO = new diffTestIO
    val mycoreTOPIO = new mycoreTOPIO
  })
  io := DontCare
  BoringUtils.addSink(io.goodTrapIO.nemu, "GoodTrapNemu")

  BoringUtils.addSink(io.diffTestIO.regFile, "diffTestRegfile")
  BoringUtils.addSink(io.diffTestIO.commit, "diffTestCommit")
  BoringUtils.addSink(io.diffTestIO.PC, "diffTestPC")

  val mycoreTOP = Module(new mycoreTOP())
  io.mycoreTOPIO <> mycoreTOP.io

}

object elaborateverilatorTOP extends App {
  (new stage.ChiselStage).execute(args, Seq(stage.ChiselGeneratorAnnotation(() => new verilatorTOP)))
}