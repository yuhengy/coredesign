#include "diffTestIO.h"
#include "VsimTOP.h"
#include "verilatorResult.h"

verilatorResult_c::verilatorResult_c(ram_c* inputRam)
{
  ram = inputRam;
  cycleCounter = 0;
  
  simTOP = new VsimTOP;
  simTOP->eval();
  simTOP->eval();
  simTOP->clock = 0;
  simTOP->reset = 1;
  for (int i = 0; i < 10; i++) {
    simTOP->clock = simTOP->clock ? 0 : 1;
    simTOP->eval();
  }
  simTOP->reset = 0;

}

void verilatorResult_c::step(int i)
{
  cycleCounter += i;

  for (; i > 0; i--) {
    while (!simTOP->io_diffTestIO_commit) {
      simTOP->clock = simTOP->clock ? 0 : 1;
      simTOP->eval();
      simTOP->clock = simTOP->clock ? 0 : 1;
      simTOP->eval();

      simTOP->io_mycoreTOPIO_instReadIO_data = 
        ram->InstRead(simTOP->io_mycoreTOPIO_instReadIO_addr, simTOP->io_mycoreTOPIO_instReadIO_en);
      simTOP->io_mycoreTOPIO_dataReadIO_data = 
        ram->DataRead(simTOP->io_mycoreTOPIO_dataReadIO_addr, simTOP->io_mycoreTOPIO_dataReadIO_en);
      ram->DataWrite(simTOP->io_mycoreTOPIO_dataWriteIO_addr, 
                     simTOP->io_mycoreTOPIO_dataWriteIO_data, 
                     simTOP->io_mycoreTOPIO_dataWriteIO_en, 
                     simTOP->io_mycoreTOPIO_dataWriteIO_mask);
    }
    simTOP->clock = simTOP->clock ? 0 : 1;
    simTOP->eval();
    simTOP->clock = simTOP->clock ? 0 : 1;
    simTOP->eval();

    simTOP->io_mycoreTOPIO_instReadIO_data = 
      ram->InstRead(simTOP->io_mycoreTOPIO_instReadIO_addr, simTOP->io_mycoreTOPIO_instReadIO_en);
    simTOP->io_mycoreTOPIO_dataReadIO_data = 
      ram->DataRead(simTOP->io_mycoreTOPIO_dataReadIO_addr, simTOP->io_mycoreTOPIO_dataReadIO_en);
    ram->DataWrite(simTOP->io_mycoreTOPIO_dataWriteIO_addr, 
                   simTOP->io_mycoreTOPIO_dataWriteIO_data, 
                   simTOP->io_mycoreTOPIO_dataWriteIO_en, 
                   simTOP->io_mycoreTOPIO_dataWriteIO_mask);
  }
}

int verilatorResult_c::getCycleCounter()
{
  return cycleCounter;
}

void verilatorResult_c::getDiffTestResult(diffTestIO_c* diffTestIO)
{
  diffTestIO->regFile[ 0] = simTOP->io_diffTestIO_regFile_0;
  diffTestIO->regFile[ 1] = simTOP->io_diffTestIO_regFile_1;
  diffTestIO->regFile[ 2] = simTOP->io_diffTestIO_regFile_2;
  diffTestIO->regFile[ 3] = simTOP->io_diffTestIO_regFile_3;
  diffTestIO->regFile[ 4] = simTOP->io_diffTestIO_regFile_4;
  diffTestIO->regFile[ 5] = simTOP->io_diffTestIO_regFile_5;
  diffTestIO->regFile[ 6] = simTOP->io_diffTestIO_regFile_6;
  diffTestIO->regFile[ 7] = simTOP->io_diffTestIO_regFile_7;
  diffTestIO->regFile[ 8] = simTOP->io_diffTestIO_regFile_8;
  diffTestIO->regFile[ 9] = simTOP->io_diffTestIO_regFile_9;
  diffTestIO->regFile[10] = simTOP->io_diffTestIO_regFile_10;
  diffTestIO->regFile[11] = simTOP->io_diffTestIO_regFile_11;
  diffTestIO->regFile[12] = simTOP->io_diffTestIO_regFile_12;
  diffTestIO->regFile[13] = simTOP->io_diffTestIO_regFile_13;
  diffTestIO->regFile[14] = simTOP->io_diffTestIO_regFile_14;
  diffTestIO->regFile[15] = simTOP->io_diffTestIO_regFile_15;
  diffTestIO->regFile[16] = simTOP->io_diffTestIO_regFile_16;
  diffTestIO->regFile[17] = simTOP->io_diffTestIO_regFile_17;
  diffTestIO->regFile[18] = simTOP->io_diffTestIO_regFile_18;
  diffTestIO->regFile[19] = simTOP->io_diffTestIO_regFile_19;
  diffTestIO->regFile[20] = simTOP->io_diffTestIO_regFile_20;
  diffTestIO->regFile[21] = simTOP->io_diffTestIO_regFile_21;
  diffTestIO->regFile[22] = simTOP->io_diffTestIO_regFile_22;
  diffTestIO->regFile[23] = simTOP->io_diffTestIO_regFile_23;
  diffTestIO->regFile[24] = simTOP->io_diffTestIO_regFile_24;
  diffTestIO->regFile[25] = simTOP->io_diffTestIO_regFile_25;
  diffTestIO->regFile[26] = simTOP->io_diffTestIO_regFile_26;
  diffTestIO->regFile[27] = simTOP->io_diffTestIO_regFile_27;
  diffTestIO->regFile[28] = simTOP->io_diffTestIO_regFile_28;
  diffTestIO->regFile[29] = simTOP->io_diffTestIO_regFile_29;
  diffTestIO->regFile[30] = simTOP->io_diffTestIO_regFile_30;
  diffTestIO->regFile[31] = simTOP->io_diffTestIO_regFile_31;
  diffTestIO->PC = simTOP->io_diffTestIO_PC;
}

verilatorResult_c::~verilatorResult_c()
{
  simTOP->final();
  delete simTOP;
}










