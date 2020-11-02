#include "ram.h"
#include "verilatorResult.h"
#include "nemuResult.h"

#include <iostream>
#include <stdio.h>
#include <verilated.h>

#define DIFFTESTON false
#define DUMPTRACEON false

long sc_time = 0;
double sc_time_stamp () {       // Called by $time in Verilog
    return sc_time;
}

int main(int argc, char** argv)
{
  char* imgPath = argv[1];
  verilatorResult_c* verilatorResult = new verilatorResult_c(imgPath, &sc_time);
  nemuResult_c* nemuResult = new nemuResult_c(imgPath);

  printf("                          **************************************************************\n");
  printf("                          ***************Reset Stage of Verilator RegFile***************\n");
  printf("                          **************************************************************\n");
  verilatorResult->dump();

  while (!verilatorResult->hitGoodTrap()) {
    verilatorResult->step(1);
    nemuResult->step(1);
    if (DUMPTRACEON) verilatorResult->dump();

    if (DIFFTESTON && verilatorResult->compareWith(nemuResult)) {
      printf("                          **************************************************************\n");
      printf("                          ***************Compare Verilator and Nemu Error***************\n");
      printf("                          **************************************************************\n");

      printf("                          ************************Nemu Result is************************\n");
      nemuResult->dump();
      break;
    }

#ifdef DEBUG
    if (nemuResult->getCycleCounter() == 100000) {
      break;
    }
#endif
  }

  if (verilatorResult->hitGoodTrap()) {
    nemuResult->step(1);  //so that nemu can print `Hit Good Trap`
    printf("                          **************************************************************\n");
    printf("                          ************************Hit Good Trap*************************\n");
    printf("                          **************************************************************\n");
  }

  delete verilatorResult;
  delete nemuResult;
  return 0;
}
