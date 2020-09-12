package sim

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util.experimental.BoringUtils

import mycore._

class simTOP extends Module {
  val io = IO(new Bundle{
    val diffTestIO = new diffTestIO
  })
  io := DontCare

  val mycoreTOP = Module(new mycoreTOP())

  // These three line is an alternative for `io := DontCare`
  // , which can avoid `error: Reference io is not fully initialized`
  //val diffTestIO = WireInit(0.U.asTypeOf(new diffTestIO))
  //BoringUtils.addSink(diffTestIO.regFile, "diffTestRegfile")
  //io.diffTestIO := diffTestIO
  BoringUtils.addSink(io.diffTestIO.regFile, "diffTestRegfile")
}

object elaborate extends App {
  (new stage.ChiselStage).execute(args, Seq(stage.ChiselGeneratorAnnotation(() => new simTOP)))
}