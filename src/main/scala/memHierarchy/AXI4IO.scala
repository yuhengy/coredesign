package memHierarchy

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

import common.configurations._
import common.AXIConstants._
import common.memWriteIO
import common.memReadIO

class AXI4IO extends Bundle {
  val awid    = Output(UInt(A_ID_W.W))
  val awvalid = Output(Bool())
  val awready = Input(Bool())
  val awaddr  = Output(UInt(A_ADDR_W.W))
  val awlen   = Output(UInt(A_LEN_W.W))
  val awsize  = Output(UInt(A_SIZE_W.W))
  val awburst = Output(UInt(A_BURST_W.W))
  val awqos   = Output(UInt(A_QOS_W.W))
  val awcache = Output(UInt(A_CACHE_W.W))
  val awlock  = Output(Bool())
  val awprot  = Output(UInt(A_PROT_W.W))
  val awuser  = Output(Bool())

  val wvalid = Output(Bool())
  val wready = Input(Bool())
  val wdata  = Output(UInt(A_DATA_W.W))
  val wstrb  = Output(UInt(A_STRB_W.W))
  val wlast  = Output(Bool())

  val bid    = Input(UInt(A_ID_W.W))
  val bvalid = Input(Bool())
  val bready = Output(Bool())
  val bresp  = Input(UInt(A_RESP_W.W))
  val buser  = Input(Bool())
     
  val arid    = Output(UInt(A_ID_W.W))
  val arvalid = Output(Bool())
  val arready = Input(Bool())
  val araddr  = Output(UInt(A_ADDR_W.W))
  val arlen   = Output(UInt(A_LEN_W.W))
  val arsize  = Output(UInt(A_SIZE_W.W))
  val arburst = Output(UInt(A_BURST_W.W))
  val arprot  = Output(UInt(A_PROT_W.W))
  val aruser  = Output(Bool())
  val arlock  = Output(Bool())
  val arcache = Output(UInt(A_CACHE_W.W))
  val arqos   = Output(UInt(A_QOS_W.W))   

  val rid    = Input(UInt(A_ID_W.W))
  val rvalid = Input(Bool())
  val rready = Output(Bool())
  val rdata  = Input(UInt(A_DATA_W.W))
  val rresp  = Input(UInt(A_RESP_W.W))
  val rlast  = Input(Bool())
  val ruser  = Input(Bool())

}
