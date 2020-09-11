#include "diffTestIO.h"
#include "VsimTOP.h"

class verilatorResult_c {
public:
  verilatorResult_c();
  void step(int i);
  int getCycleCounter();
  void getDiffTestResult(diffTestIO_t* diffTestIO);
  ~verilatorResult_c();




private:
  VsimTOP *simTOP;
  int cycleCounter;  //cycles has finished since reset
};