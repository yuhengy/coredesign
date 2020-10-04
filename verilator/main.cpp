#include "ram.h"
#include "verilatorResult.h"
#include "nemuResult.h"

#include <iostream>
#include <stdio.h>
#include <verilated.h>

long sc_time = 0;
double sc_time_stamp () {       // Called by $time in Verilog
    return sc_time;
}

int main(int argc, char** argv)
{
  ram_c* ram = new ram_c(argv[1]);
  verilatorResult_c* verilatorResult = new verilatorResult_c(ram, &sc_time);
  nemuResult_c* nemuResult = new nemuResult_c(ram);

  printf("                          **************************************************************\n");
  printf("                          ***************Reset Stage of Verilator RegFile***************\n");
  printf("                          **************************************************************\n");
  verilatorResult->dump();

  while (!Verilated::gotFinish()) {
    verilatorResult->step(1);
    nemuResult->step(1);
    verilatorResult->dump();

    if (verilatorResult->compareWith(nemuResult)) {
      printf("                          **************************************************************\n");
      printf("                          ***************Compare Verilator and Nemu Error***************\n");
      printf("                          **************************************************************\n");

      printf("                          ************************Nemu Result is************************\n");
      nemuResult->dump();
      break;
    }

#ifdef DEBUG
    if (nemuResult->getCycleCounter() == 10000) {
      break;
    }
#endif
  }

  delete ram;
  delete verilatorResult;
  delete nemuResult;
  return 0;
}
