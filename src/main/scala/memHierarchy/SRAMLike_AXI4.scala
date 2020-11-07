package memHierarchy

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

import common.configurations._
import common.AXIConstants._
import common.memWriteIO
import common.memReadIO

class SRAMLike_AXI4 extends Module
{
  val io = IO(new Bundle{
    val instReadIO = Flipped(new memReadIO)
    val dataReadIO = Flipped(new memReadIO)
    val dataWriteIO = Flipped(new memWriteIO)

    val AXI4IO = new AXI4IO
  })
  io := DontCare

//--------------buffer start--------------
  val instReadBufferEmpty = RegInit(true.B)
  // This will be served directly from idle state
  // insted of buffered
  // val dataReadBufferEmpty = RegInit(true.B)
  val dataWriteBufferEmpty = RegInit(true.B)

  val instReadBufferAddr  = Reg(UInt(XLEN.W))
  val dataReadBufferAddr  = Reg(UInt(XLEN.W))
  val dataWriteBufferAddr = Reg(UInt(XLEN.W))
  val dataWriteBufferData = Reg(UInt(XLEN.W))
  val dataWriteBufferMask = Reg(UInt((XLEN/8).W))

//^^^^^^^^^^^^^^buffer end^^^^^^^^^^^^^^

//--------------state machine start--------------
//output
  object stateEnum extends ChiselEnum {
    val reset, idle = Value
    val instReadAddr, instReadData = Value
    val dataReadAddr, dataReadData = Value
    val dataWriteAddr, dataWriteData, dataWriteResp = Value
  }
  val state = RegInit(stateEnum.reset)

//private
  switch (state) {
    is (stateEnum.reset) {
      state := stateEnum.idle
    }
    is (stateEnum.idle) {
      when (io.dataReadIO.en)
        {state := stateEnum.dataReadAddr}.
      elsewhen (io.dataWriteIO.en)
        {state := stateEnum.dataWriteAddr}.
      elsewhen (io.instReadIO.en)
        {state := stateEnum.instReadAddr}
    }

    // instReadIO
    is (stateEnum.instReadAddr) {
      when (io.AXI4IO.arready)
        {state := stateEnum.instReadData}
    }
    is (stateEnum.instReadData) {
      when (io.AXI4IO.rvalid)
        {state := stateEnum.idle}
    }

    // dataReadIO
    is (stateEnum.dataReadAddr) {
      when (io.AXI4IO.arready)
        {state := stateEnum.dataReadData}
    }
    is (stateEnum.dataReadData) {
      when (io.AXI4IO.rvalid && dataWriteBufferEmpty && instReadBufferEmpty)
        {state := stateEnum.idle}.
      elsewhen (io.AXI4IO.rvalid && !dataWriteBufferEmpty)
        {state := stateEnum.dataWriteAddr}.
      elsewhen (io.AXI4IO.rvalid && !instReadBufferEmpty)
        {state := stateEnum.instReadAddr}
    }

    // dataWriteIO
    is (stateEnum.dataWriteAddr) {
      when (io.AXI4IO.awready)
        {state := stateEnum.dataWriteData}
    }
    is (stateEnum.dataWriteData) {
      when (io.AXI4IO.wready)
        {state := stateEnum.dataWriteResp}
    }
    is (stateEnum.dataWriteResp) {
      when (io.AXI4IO.bvalid && instReadBufferEmpty)
        {state := stateEnum.idle}.
      elsewhen (io.AXI4IO.bvalid && !instReadBufferEmpty)
        {state := stateEnum.instReadAddr}
    }
  }
//^^^^^^^^^^^^^^state machine end^^^^^^^^^^^^^^

//--------------state behaviour start--------------
  when (state === stateEnum.reset) {
    // SRAMLike
    io.instReadIO.reqReady   := false.B
    io.instReadIO.respValid  := false.B
    io.dataReadIO.reqReady   := false.B
    io.dataReadIO.respValid  := false.B
    io.dataWriteIO.reqReady  := false.B
    io.dataWriteIO.respValid := false.B

    // AXI4
    io.AXI4IO.awvalid := false.B
    io.AXI4IO.wvalid  := false.B
    io.AXI4IO.bready  := false.B
    io.AXI4IO.arvalid := false.B
    io.AXI4IO.rready  := false.B

  }.
  elsewhen (state === stateEnum.idle) {
    // SRAMLike
    io.instReadIO.reqReady   := true.B
    io.instReadIO.respValid  := false.B
    io.dataReadIO.reqReady   := true.B
    io.dataReadIO.respValid  := false.B
    io.dataWriteIO.reqReady  := true.B
    io.dataWriteIO.respValid := false.B

    // buffer
    instReadBufferEmpty  := !(io.instReadIO.en &&
                              (io.dataReadIO.en || io.dataWriteIO.en))
    dataWriteBufferEmpty := !(io.dataWriteIO.en && 
                              io.dataReadIO.en)
    instReadBufferAddr   := io.instReadIO.addr
    dataReadBufferAddr   := io.dataReadIO.addr
    dataWriteBufferAddr  := io.dataWriteIO.addr
    dataWriteBufferData  := io.dataWriteIO.data
    dataWriteBufferMask  := io.dataWriteIO.mask

    // AXI4
    io.AXI4IO.awvalid := false.B
    io.AXI4IO.wvalid  := false.B
    io.AXI4IO.bready  := false.B
    io.AXI4IO.arvalid := false.B
    io.AXI4IO.rready  := false.B
    
  }.
  elsewhen (state === stateEnum.instReadAddr) {
    // SRAMLike
    io.instReadIO.reqReady   := false.B
    io.instReadIO.respValid  := false.B
    io.dataReadIO.reqReady   := false.B
    io.dataReadIO.respValid  := false.B
    io.dataWriteIO.reqReady  := false.B
    io.dataWriteIO.respValid := false.B

    // buffer
    instReadBufferEmpty  := true.B

    // AXI4
    io.AXI4IO.awvalid := false.B
    io.AXI4IO.wvalid  := false.B
    io.AXI4IO.bready  := false.B
    io.AXI4IO.arvalid := true.B
    io.AXI4IO.rready  := false.B

    io.AXI4IO.arid    := AXI_ID.U
    io.AXI4IO.araddr  := instReadBufferAddr(A_ADDR_W-1,0)
    io.AXI4IO.arlen   := AXI_LEN_1.U
    io.AXI4IO.arsize  := AXI_SIZE_64bit.U
    io.AXI4IO.arburst := AXI_BURST_FIXED.U
    io.AXI4IO.arprot  := AXI_PROT.U
    io.AXI4IO.aruser  := AXI_USER.U
    io.AXI4IO.arlock  := AXI_LOCK_NORMAL.U
    io.AXI4IO.arcache := AXI_CACHE_DeviceNonBuffer.U
    io.AXI4IO.arqos   := AXI_QOS_NON.U
    
  }.
  elsewhen (state === stateEnum.instReadData) {
    // SRAMLike
    io.instReadIO.reqReady   := false.B
    // TODO: this may be a critical path
    io.instReadIO.respValid  := io.AXI4IO.rvalid
    io.dataReadIO.reqReady   := false.B
    io.dataReadIO.respValid  := false.B
    io.dataWriteIO.reqReady  := false.B
    io.dataWriteIO.respValid := false.B

    io.instReadIO.data  := io.AXI4IO.rdata

    // AXI4
    io.AXI4IO.awvalid := false.B
    io.AXI4IO.wvalid  := false.B
    io.AXI4IO.bready  := false.B
    io.AXI4IO.arvalid := false.B
    io.AXI4IO.rready  := true.B
    
  }.
  elsewhen (state === stateEnum.dataReadAddr) {
    // SRAMLike
    io.instReadIO.reqReady   := false.B
    io.instReadIO.respValid  := false.B
    io.dataReadIO.reqReady   := false.B
    io.dataReadIO.respValid  := false.B
    io.dataWriteIO.reqReady  := false.B
    io.dataWriteIO.respValid := false.B

    // AXI4
    io.AXI4IO.awvalid := false.B
    io.AXI4IO.wvalid  := false.B
    io.AXI4IO.bready  := false.B
    io.AXI4IO.arvalid := true.B
    io.AXI4IO.rready  := false.B

    io.AXI4IO.arid    := AXI_ID.U
    io.AXI4IO.araddr  := dataReadBufferAddr(A_ADDR_W-1,0)
    io.AXI4IO.arlen   := AXI_LEN_1.U
    io.AXI4IO.arsize  := AXI_SIZE_64bit.U
    io.AXI4IO.arburst := AXI_BURST_FIXED.U
    io.AXI4IO.arprot  := AXI_PROT.U
    io.AXI4IO.aruser  := AXI_USER.U
    io.AXI4IO.arlock  := AXI_LOCK_NORMAL.U
    io.AXI4IO.arcache := AXI_CACHE_DeviceNonBuffer.U
    io.AXI4IO.arqos   := AXI_QOS_NON.U
    
  }.
  elsewhen (state === stateEnum.dataReadData) {
    // SRAMLike
    io.instReadIO.reqReady   := false.B
    io.instReadIO.respValid  := false.B
    io.dataReadIO.reqReady   := false.B
    io.dataReadIO.respValid  := io.AXI4IO.rvalid
    io.dataWriteIO.reqReady  := false.B
    io.dataWriteIO.respValid := false.B

    io.dataReadIO.data  := io.AXI4IO.rdata

    // AXI4
    io.AXI4IO.awvalid := false.B
    io.AXI4IO.wvalid  := false.B
    io.AXI4IO.bready  := false.B
    io.AXI4IO.arvalid := false.B
    io.AXI4IO.rready  := true.B
    
  }.
  elsewhen (state === stateEnum.dataWriteAddr) {
    // SRAMLike
    io.instReadIO.reqReady   := false.B
    io.instReadIO.respValid  := false.B
    io.dataReadIO.reqReady   := false.B
    io.dataReadIO.respValid  := false.B
    io.dataWriteIO.reqReady  := false.B
    io.dataWriteIO.respValid := false.B

    // buffer
    dataWriteBufferEmpty  := true.B

    // AXI4
    io.AXI4IO.awvalid := true.B
    io.AXI4IO.wvalid  := false.B
    io.AXI4IO.bready  := false.B
    io.AXI4IO.arvalid := false.B
    io.AXI4IO.rready  := false.B

    io.AXI4IO.awid    := AXI_ID.U
    io.AXI4IO.awaddr  := dataWriteBufferAddr(A_ADDR_W-1,0)
    io.AXI4IO.awlen   := AXI_LEN_1.U
    //io.AXI4IO.awsize  := AXI_SIZE_64bit.U
    io.AXI4IO.awsize  := Mux(dataWriteBufferAddr(A_ADDR_W-1,0)===0x4100000f.U || dataWriteBufferAddr(A_ADDR_W-1,0)===0x41000000.U, 0.U, AXI_SIZE_64bit.U)
    io.AXI4IO.awburst := AXI_BURST_FIXED.U
    io.AXI4IO.awqos   := AXI_QOS_NON.U
    io.AXI4IO.awcache := AXI_CACHE_DeviceNonBuffer.U
    io.AXI4IO.arlock  := AXI_LOCK_NORMAL.U
    io.AXI4IO.awprot  := AXI_PROT.U
    io.AXI4IO.awuser  := AXI_USER.U
    
  }.
  elsewhen (state === stateEnum.dataWriteData) {
    // SRAMLike
    io.instReadIO.reqReady   := false.B
    io.instReadIO.respValid  := false.B
    io.dataReadIO.reqReady   := false.B
    io.dataReadIO.respValid  := false.B
    io.dataWriteIO.reqReady  := false.B
    io.dataWriteIO.respValid := false.B

    // AXI4
    io.AXI4IO.awvalid := false.B
    io.AXI4IO.wvalid  := true.B
    io.AXI4IO.bready  := false.B
    io.AXI4IO.arvalid := false.B
    io.AXI4IO.rready  := false.B

    io.AXI4IO.wdata := dataWriteBufferData
    io.AXI4IO.wstrb := dataWriteBufferMask
    io.AXI4IO.wlast := true.B
    
  }.
  elsewhen (state === stateEnum.dataWriteResp) {
    // SRAMLike
    io.instReadIO.reqReady   := false.B
    io.instReadIO.respValid  := false.B
    io.dataReadIO.reqReady   := false.B
    io.dataReadIO.respValid  := false.B
    io.dataWriteIO.reqReady  := false.B
    io.dataWriteIO.respValid := io.AXI4IO.bvalid

    // AXI4
    io.AXI4IO.awvalid := false.B
    io.AXI4IO.wvalid  := false.B
    io.AXI4IO.bready  := true.B
    io.AXI4IO.arvalid := false.B
    io.AXI4IO.rready  := false.B
    
  }
//^^^^^^^^^^^^^^state behaviour end^^^^^^^^^^^^^^



}

