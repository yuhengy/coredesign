package mycore
package execute

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._

import common.configurations._
import common.constants._

class alu extends Module
{
  val io = IO(new Bundle{
    val aluFunc  = Input(UInt(aluFunc_w.W))
    val op1      = Input(UInt(XLEN.W))
    val op2      = Input(UInt(XLEN.W))

    val out      = Output(UInt(XLEN.W))
    val adderOut = Output(UInt(XLEN.W))

    val stall = Output(Bool())
  })

  // Multiplier
  val isMulDiv = io.aluFunc === ALU_MUL || io.aluFunc === ALU_MULH || io.aluFunc === ALU_MULHSU ||
                 io.aluFunc === ALU_MULHU || io.aluFunc === ALU_DIV || io.aluFunc === ALU_DIVU ||
                 io.aluFunc === ALU_REM || io.aluFunc === ALU_REMU || io.aluFunc === ALU_MULW ||
                 io.aluFunc === ALU_DIVW || io.aluFunc === ALU_DIVUW || io.aluFunc === ALU_REMW ||
                 io.aluFunc === ALU_REMUW
  val multiplier = Module(new Multiplier)
  multiplier.io.start := isMulDiv
  multiplier.io.a     := io.op1
  multiplier.io.b     := io.op2
  multiplier.io.op    := io.aluFunc
  io.stall            := isMulDiv && multiplier.io.stall_req


   // ALU
  val adderOut = (io.op1 + io.op2)(XLEN-1,0)
  val shamt    = io.op2(5,0).asUInt()
  io.out      := MuxCase(0.U, Array(
                  (io.aluFunc === ALU_ADD)   -> adderOut,
                  (io.aluFunc === ALU_SUB)   -> (io.op1 - io.op2).asUInt(),
                  (io.aluFunc === ALU_AND)   -> (io.op1 & io.op2).asUInt(),
                  (io.aluFunc === ALU_OR)    -> (io.op1 | io.op2).asUInt(),
                  (io.aluFunc === ALU_XOR)   -> (io.op1 ^ io.op2).asUInt(),
                  (io.aluFunc === ALU_SLT)   -> (io.op1.asSInt() < io.op2.asSInt()).asUInt(),
                  (io.aluFunc === ALU_SLTU)  -> (io.op1 < io.op2).asUInt(),
                  (io.aluFunc === ALU_SLL)   -> ((io.op1 << shamt)(XLEN-1, 0)).asUInt(),
                  (io.aluFunc === ALU_SRL)   -> (io.op1 >> shamt).asUInt(),
                  (io.aluFunc === ALU_SRA)   -> (io.op1.asSInt() >> shamt).asUInt(),
                  (io.aluFunc === ALU_SLLW)  -> ((io.op1 << shamt(4,0))(XLEN-1, 0)).asUInt(),
                  (io.aluFunc === ALU_SRLW)  -> (io.op1 >> shamt(4,0)).asUInt(),
                  (io.aluFunc === ALU_SRAW)  -> Cat(Fill(32, 0.U), (io.op1(31,0).asSInt() >> shamt(4,0)).asUInt()),
                  (io.aluFunc === ALU_COPY_1)-> io.op1,
                  (io.aluFunc === ALU_COPY_2)-> io.op2,
                  (isMulDiv)                 -> multiplier.io.mult_out
                  ))
  io.adderOut := adderOut

}
