#include "diffTestIO.h"
#include "VsimTOP.h"
#include "verilated_vcd_c.h"
#include "verilatorResult.h"

verilatorResult_c::verilatorResult_c(ram_c* inputRam, long* inputSc_time)
{
  ram = inputRam;
  sc_time = inputSc_time;
  cycleCounter = 0;
  
  simTOP = new VsimTOP;

  if(vcdTrace)
  {
    Verilated::traceEverOn(true);

    tfp = new VerilatedVcdC;
    simTOP->trace(tfp, 99);

    tfp->open("build/generated-cpp/myVCD.vcd");
  }


  simTOP->eval();
  simTOP->eval();
  simTOP->clock = 0;
  simTOP->reset = 1;
  for (int i = 0; i < 9; i++) {
    simTOP->clock = simTOP->clock ? 0 : 1;
    simTOP->eval();
    cycleCounter++; (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }
  }
  simTOP->clock = 0;
  simTOP->reset = 0;
  simTOP->eval();
  cycleCounter++; (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }

  step(1);
  getDiffTestResult();
}

void verilatorResult_c::step(int i)
{
  for (; i > 0; i--) {
    while (!simTOP->io_diffTestIO_commit) {
      simTOP->clock = 1;
      simTOP->eval();
      evalRam();
      simTOP->eval();  //TODO: Why this eval can be will be done implicitly?
      cycleCounter++; (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }
      
      simTOP->clock = 0;
      simTOP->eval();
      cycleCounter++; (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }
    }

    simTOP->clock = 1;
    simTOP->eval();
    evalRam();
    simTOP->eval();
    cycleCounter++; (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }
      
    simTOP->clock = 0;
    simTOP->eval();
    cycleCounter++; (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }
  }
  getDiffTestResult();
}

int verilatorResult_c::getCycleCounter()
{
  return cycleCounter;
}

void verilatorResult_c::getDiffTestResult()
{
  regFile[ 0] = simTOP->io_diffTestIO_regFile_0;
  regFile[ 1] = simTOP->io_diffTestIO_regFile_1;
  regFile[ 2] = simTOP->io_diffTestIO_regFile_2;
  regFile[ 3] = simTOP->io_diffTestIO_regFile_3;
  regFile[ 4] = simTOP->io_diffTestIO_regFile_4;
  regFile[ 5] = simTOP->io_diffTestIO_regFile_5;
  regFile[ 6] = simTOP->io_diffTestIO_regFile_6;
  regFile[ 7] = simTOP->io_diffTestIO_regFile_7;
  regFile[ 8] = simTOP->io_diffTestIO_regFile_8;
  regFile[ 9] = simTOP->io_diffTestIO_regFile_9;
  regFile[10] = simTOP->io_diffTestIO_regFile_10;
  regFile[11] = simTOP->io_diffTestIO_regFile_11;
  regFile[12] = simTOP->io_diffTestIO_regFile_12;
  regFile[13] = simTOP->io_diffTestIO_regFile_13;
  regFile[14] = simTOP->io_diffTestIO_regFile_14;
  regFile[15] = simTOP->io_diffTestIO_regFile_15;
  regFile[16] = simTOP->io_diffTestIO_regFile_16;
  regFile[17] = simTOP->io_diffTestIO_regFile_17;
  regFile[18] = simTOP->io_diffTestIO_regFile_18;
  regFile[19] = simTOP->io_diffTestIO_regFile_19;
  regFile[20] = simTOP->io_diffTestIO_regFile_20;
  regFile[21] = simTOP->io_diffTestIO_regFile_21;
  regFile[22] = simTOP->io_diffTestIO_regFile_22;
  regFile[23] = simTOP->io_diffTestIO_regFile_23;
  regFile[24] = simTOP->io_diffTestIO_regFile_24;
  regFile[25] = simTOP->io_diffTestIO_regFile_25;
  regFile[26] = simTOP->io_diffTestIO_regFile_26;
  regFile[27] = simTOP->io_diffTestIO_regFile_27;
  regFile[28] = simTOP->io_diffTestIO_regFile_28;
  regFile[29] = simTOP->io_diffTestIO_regFile_29;
  regFile[30] = simTOP->io_diffTestIO_regFile_30;
  regFile[31] = simTOP->io_diffTestIO_regFile_31;
  PC = simTOP->io_diffTestIO_PC;
}

verilatorResult_c::~verilatorResult_c()
{
  tfp->close();
  simTOP->final();
  delete simTOP;
}

void verilatorResult_c::evalRam()
{
  simTOP->io_mycoreTOPIO_instReadIO_data = ram->InstReadResp();
  simTOP->io_mycoreTOPIO_dataReadIO_data = ram->DataReadResp();

  ram->InstReadReq(simTOP->io_mycoreTOPIO_instReadIO_addr, simTOP->io_mycoreTOPIO_instReadIO_en);
  ram->DataReadReq(simTOP->io_mycoreTOPIO_dataReadIO_addr, simTOP->io_mycoreTOPIO_dataReadIO_en);
  ram->DataWrite(simTOP->io_mycoreTOPIO_dataWriteIO_addr, 
                 simTOP->io_mycoreTOPIO_dataWriteIO_data, 
                 simTOP->io_mycoreTOPIO_dataWriteIO_en, 
                 simTOP->io_mycoreTOPIO_dataWriteIO_mask);

}






