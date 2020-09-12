package common

import chisel3.util.log2Ceil

object constants extends
  RISCVConstants
{
}


trait RISCVConstants
{
  val NUM_REG = 32
  val ADDR_START = 0x80000000L


  //-----------Derived constants begin-----------
  val WID_REG_ADDR = log2Ceil(NUM_REG)
  //-----------Derived constants end-----------
}
