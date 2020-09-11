#include "verilatorResult.h"
#include "diffTestIO.h"

#include <iostream>
#include <verilated.h>

int main(int argc, char** argv)
{
  diffTestIO_t VerilatorResult;
  verilatorResult_c* verilatorResult = new verilatorResult_c();

  while (!Verilated::gotFinish()) {
    verilatorResult->step(1);
    verilatorResult->getDiffTestResult(&VerilatorResult);

#ifdef DEBUG
    std::cout << "Cycle " << verilatorResult->getCycleCounter() 
    << ": reg[0] = " << VerilatorResult.regFile[0] << std::endl;

    if (verilatorResult->getCycleCounter() == 10) {
      break;
    }
#endif
  }

  delete verilatorResult;
  return 0;
}
