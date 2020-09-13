package mycore
package decode

import chisel3._
import chisel3.util.ListLookup

import common.constants._
import common.instructions._

class decoder extends Module
{
  io = IO(new Bundle{
    val inst      = Input(UInt(WID_INST.W))
    val allCtrlIO = new allCtrlIO
  })

  // TODO: csrCmd is a foo one for now.
  val csignals = ListLookup(io.inst,
                            List(N, BR_N  , OP1_X  , OP2_X     , OEN_0, OEN_0, ALU_X     , WB_X  , REN_0, MRD_0, MWR_0, MSK_X ,    N, N),
               Array(     /* val  |  BR   |   op1  |    op2    |  R1  |  R2  |    ALU    |  wb   | rf   | mem  | mem  | mask | csr  | fence.i */
                          /* inst | type  |   sel  |    sel    |  oen |  oen |    fcn    |  sel  | wen  |  rd  |  wr  | type | cmd  |         */
                  LW     -> List(Y, BR_N  , OP1_RS1, OP2_ITYPE , OEN_1, OEN_0, ALU_ADD   , WB_MEM, REN_1, MRD_1, MWR_0, MSK_W,     N, N),
                  SW     -> List(Y, BR_N  , OP1_RS1, OP2_STYPE , OEN_1, OEN_1, ALU_ADD   , WB_X  , REN_0, MRD_0, MWR_1, MSK_W,     N, N),
                  LUI    -> List(Y, BR_N  , OP1_X  , OP2_UTYPE , OEN_0, OEN_0, ALU_COPY_2, WB_ALU, REN_1, MRD_0, MWR_0, MSK_X,     N, N)
                  ))

  // Put these control signals in variables
  // TODO: all val within () is useless for now
  val (cs_val_inst: Bool) :: io.allCtrlIO.brType :: io.allCtrlIO.op1Sel :: io.allCtrlIO.op2Sel :: (cs_rs1_oen: Bool) :: (cs_rs2_oen: Bool) :: subCsignals := csignals
  val io.allCtrlIO.aluFunc :: io.allCtrlIO.wbSel :: io.allCtrlIO.rfWen :: io.allCtrlIO.memRd :: io.allCtrlIO.memWr :: io.allCtrlIO.memMask :: (cs_csr_cmd: Bool) :: (cs_fencei: Bool) :: Nil := subCsignals
}