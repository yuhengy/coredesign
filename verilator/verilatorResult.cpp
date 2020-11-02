#include "diffTestIO.h"
#include "VverilatorTOP.h"  //TODO name of this file should be defined in makefile
#include "verilated_vcd_c.h"
#include "verilatorResult.h"

verilatorResult_c::verilatorResult_c(char* imgPath, long* inputSc_time)
{
  sc_time = inputSc_time;
  
  verilatorTOP = new VverilatorTOP;
  struct CPU_RAM_IO_t CPU_RAM_IO = {
    .instReadIO_reqReady = (bool*)&(verilatorTOP->io_mycoreTOPIO_instReadIO_reqReady),
    .instReadIO_addr = &(verilatorTOP->io_mycoreTOPIO_instReadIO_addr),
    .instReadIO_en = (bool*)&(verilatorTOP->io_mycoreTOPIO_instReadIO_en),
    .instReadIO_respValid = (bool*)&(verilatorTOP->io_mycoreTOPIO_instReadIO_respValid),
    .instReadIO_data = &(verilatorTOP->io_mycoreTOPIO_instReadIO_data),
    
    .dataReadIO_reqReady = (bool*)&(verilatorTOP->io_mycoreTOPIO_dataReadIO_reqReady),
    .dataReadIO_addr = &(verilatorTOP->io_mycoreTOPIO_dataReadIO_addr),
    .dataReadIO_en = (bool*)&(verilatorTOP->io_mycoreTOPIO_dataReadIO_en),
    .dataReadIO_respValid = (bool*)&(verilatorTOP->io_mycoreTOPIO_dataReadIO_respValid),
    .dataReadIO_data = &(verilatorTOP->io_mycoreTOPIO_dataReadIO_data),

    .dataWriteIO_reqReady = (bool*)&(verilatorTOP->io_mycoreTOPIO_dataWriteIO_reqReady),
    .dataWriteIO_addr = &(verilatorTOP->io_mycoreTOPIO_dataWriteIO_addr),
    .dataWriteIO_data = &(verilatorTOP->io_mycoreTOPIO_dataWriteIO_data),
    .dataWriteIO_en = (bool*)&(verilatorTOP->io_mycoreTOPIO_dataWriteIO_en),
    .dataWriteIO_mask = (mask_t*)&(verilatorTOP->io_mycoreTOPIO_dataWriteIO_mask),
    .dataWriteIO_respValid = (bool*)&(verilatorTOP->io_mycoreTOPIO_dataWriteIO_respValid)
  };
  ram = new ram_c(imgPath, CPU_RAM_IO);

  if(vcdTrace)
  {
    Verilated::traceEverOn(true);

    tfp = new VerilatedVcdC;
    verilatorTOP->trace(tfp, 99);

    tfp->open("build/generated-cpp/myVCD.vcd");
  }

  verilatorTOP->clock = 0;
  verilatorTOP->reset = 1;
  for (int i = 0; i < 9; i++) {
    verilatorTOP->clock = verilatorTOP->clock ? 0 : 1;
    verilatorTOP->eval();
    ram->eval();
    (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }
  }
  verilatorTOP->clock = 0;
  verilatorTOP->reset = 0;
  verilatorTOP->eval();
  ram->eval();
  (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }

  step(1);
  getDiffTestResult();
}

void verilatorResult_c::step(int i)
{
  for (; i > 0; i--) {
    while (!verilatorTOP->io_diffTestIO_commit) {
      verilatorTOP->clock = 1;

      // Here, we want to simulate the interference between two logic modules(`verilatorTOP` and `ram`)
      // These two line of eval() include more programing logic them they seem to
      // To learn more, please check the develop log for `commit-bfbad1b` in documents/developLog.md
      verilatorTOP->eval();
      ram->eval();
      verilatorTOP->eval();
      ram->eval_computeLogic();  // there exists logic MEM-CPU-MEM
      ram->eval_updateReg();
      (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }
      
      verilatorTOP->clock = 0;
      verilatorTOP->eval();
      (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }
    }

    verilatorTOP->clock = 1;
    verilatorTOP->eval();
    ram->eval();
    verilatorTOP->eval();
    ram->eval_computeLogic();  // there exists logic MEM-CPU-MEM
      ram->eval_updateReg();
    (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }
      
    verilatorTOP->clock = 0;
    verilatorTOP->eval();
    (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }
  }
  getDiffTestResult();
}

void verilatorResult_c::getDiffTestResult()
{
  regFile[ 0] = verilatorTOP->io_diffTestIO_regFile_0;
  regFile[ 1] = verilatorTOP->io_diffTestIO_regFile_1;
  regFile[ 2] = verilatorTOP->io_diffTestIO_regFile_2;
  regFile[ 3] = verilatorTOP->io_diffTestIO_regFile_3;
  regFile[ 4] = verilatorTOP->io_diffTestIO_regFile_4;
  regFile[ 5] = verilatorTOP->io_diffTestIO_regFile_5;
  regFile[ 6] = verilatorTOP->io_diffTestIO_regFile_6;
  regFile[ 7] = verilatorTOP->io_diffTestIO_regFile_7;
  regFile[ 8] = verilatorTOP->io_diffTestIO_regFile_8;
  regFile[ 9] = verilatorTOP->io_diffTestIO_regFile_9;
  regFile[10] = verilatorTOP->io_diffTestIO_regFile_10;
  regFile[11] = verilatorTOP->io_diffTestIO_regFile_11;
  regFile[12] = verilatorTOP->io_diffTestIO_regFile_12;
  regFile[13] = verilatorTOP->io_diffTestIO_regFile_13;
  regFile[14] = verilatorTOP->io_diffTestIO_regFile_14;
  regFile[15] = verilatorTOP->io_diffTestIO_regFile_15;
  regFile[16] = verilatorTOP->io_diffTestIO_regFile_16;
  regFile[17] = verilatorTOP->io_diffTestIO_regFile_17;
  regFile[18] = verilatorTOP->io_diffTestIO_regFile_18;
  regFile[19] = verilatorTOP->io_diffTestIO_regFile_19;
  regFile[20] = verilatorTOP->io_diffTestIO_regFile_20;
  regFile[21] = verilatorTOP->io_diffTestIO_regFile_21;
  regFile[22] = verilatorTOP->io_diffTestIO_regFile_22;
  regFile[23] = verilatorTOP->io_diffTestIO_regFile_23;
  regFile[24] = verilatorTOP->io_diffTestIO_regFile_24;
  regFile[25] = verilatorTOP->io_diffTestIO_regFile_25;
  regFile[26] = verilatorTOP->io_diffTestIO_regFile_26;
  regFile[27] = verilatorTOP->io_diffTestIO_regFile_27;
  regFile[28] = verilatorTOP->io_diffTestIO_regFile_28;
  regFile[29] = verilatorTOP->io_diffTestIO_regFile_29;
  regFile[30] = verilatorTOP->io_diffTestIO_regFile_30;
  regFile[31] = verilatorTOP->io_diffTestIO_regFile_31;
  PC = verilatorTOP->io_diffTestIO_PC;
}

bool verilatorResult_c::hitGoodTrap()
{
  if (verilatorTOP->io_goodTrapIO_nemu == true) {
    return true;
  }
  return false;
}

verilatorResult_c::~verilatorResult_c()
{
  tfp->close();
  verilatorTOP->final();
  delete verilatorTOP;
}
