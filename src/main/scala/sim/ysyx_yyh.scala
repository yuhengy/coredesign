package sim

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util.experimental.BoringUtils

import common.constants._
import common.configurations._
import mycore.mycoreTOP
import memHierarchy._

class ysyx_yyh extends Module {
  val io = IO(new Bundle{
    val mem = new AXI4IO
    val mmio = new AXI4IO

    val mtip = Input(Bool())
    val meip = Input(Bool())
  })

  val mycoreTOP = Module(new mycoreTOP)
  val splitMap_inst = Module(new splitMap)
  val splitMap_data = Module(new splitMap)
  val SRAMLike_AXI4_MEM = Module(new SRAMLike_AXI4)
  val SRAMLike_AXI4_MMIO = Module(new SRAMLike_AXI4)

  // split mem and mmio
  splitMap_data.io.dataReadIO <> mycoreTOP.io.dataReadIO
  splitMap_data.io.dataWriteIO <> mycoreTOP.io.dataWriteIO
  splitMap_inst.io.dataReadIO <> mycoreTOP.io.instReadIO
  splitMap_inst.io.dataWriteIO := DontCare
  splitMap_inst.io.dataWriteIO.en := false.B

  // mem bridge
  SRAMLike_AXI4_MEM.io.instReadIO  <> splitMap_inst.io.MEMReadIO
  splitMap_inst.io.MEMWriteIO := DontCare
  SRAMLike_AXI4_MEM.io.dataReadIO  <> splitMap_data.io.MEMReadIO
  SRAMLike_AXI4_MEM.io.dataWriteIO <> splitMap_data.io.MEMWriteIO

  // mmio bridge
  SRAMLike_AXI4_MMIO.io.instReadIO    <> splitMap_inst.io.MMIOReadIO
  splitMap_inst.io.MMIOWriteIO := DontCare
  SRAMLike_AXI4_MMIO.io.dataReadIO    <> splitMap_data.io.MMIOReadIO
  SRAMLike_AXI4_MMIO.io.dataWriteIO   <> splitMap_data.io.MMIOWriteIO

  // output
  io.mem  <> SRAMLike_AXI4_MEM.io.AXI4IO
  io.mmio <> SRAMLike_AXI4_MMIO.io.AXI4IO

  // DontCare

  if (DIFFTEST) {
    val diffTestRegfile = Wire(Vec(NUM_REG, UInt(XLEN.W)))
    val diffTestPC      = Wire(UInt(XLEN.W))
    val diffTestCommit  = Wire(Bool())
    val GoodTrapNemu    = Wire(Bool())
    diffTestRegfile := DontCare
    diffTestPC      := DontCare
    diffTestCommit  := DontCare
    GoodTrapNemu    := DontCare 
    BoringUtils.addSink(diffTestRegfile, "diffTestRegfile")
    BoringUtils.addSink(diffTestPC, "diffTestPC")
    BoringUtils.addSink(diffTestCommit, "diffTestCommit")
    BoringUtils.addSink(GoodTrapNemu, "GoodTrapNemu")
  }
}

object elaborateysyx_yyh extends App {
  (new chisel3.stage.ChiselStage).execute(args,
    Seq(
      chisel3.stage.ChiselGeneratorAnnotation(() => new ysyx_yyh),
      firrtl.stage.RunFirrtlTransformAnnotation(new AddModulePrefix()),
      ModulePrefixAnnotation("yyh_")
    )
  )
}
