#include "ram.h"
#include "diffTestIO.h"
#include "VsimTOP.h"

class verilatorResult_c {
public:
  verilatorResult_c(ram_c* inputRam);
  void step(int i);
  int getCycleCounter();
  void getDiffTestResult(diffTestIO_c* diffTestIO);
  ~verilatorResult_c();




private:
  ram_c* ram;
  int cycleCounter;  //cycles has finished since reset
  VsimTOP *simTOP;
};
