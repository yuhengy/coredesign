package common

import chisel3.util.log2Ceil

object configurations extends
  TODOConfigurations with
  debugConfigurations
{
}


trait TODOConfigurations
{
  val XLEN = 64  //TODO: for now, signed/unsigned extend do not use this configuration

  //-----------Derived configurations begin-----------
  val addrAlign_w = log2Ceil(XLEN/8)
  //-----------Derived configurations end-----------
}

trait debugConfigurations
{
  val DEBUG = true
  val FPGAINSTDEPTH_w = 16  //fpga InstRAM size is 16K * 64 = 1M
  val FPGADATADEPTH_w = 16  //fpga DataRAM size is 16K * 64 = 1M

  val FPGADIFFDEPTH_w = 18  //fpga DIFFTESTRAM size is 1K * 2112 = 2112K
  val FPGADIFFDEPTHMAX = 0x3fff8
  val FPGADIFFDATA_w = 128
  val FPGADIFFMASK_w = FPGADIFFDATA_w / 8
}
