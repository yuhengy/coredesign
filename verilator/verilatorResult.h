#include "ram.h"
#include "diffTestIO.h"
#include "VsimTOP.h"
#include "verilated_vcd_c.h"

class verilatorResult_c : public diffTestIO_c {
public:
  verilatorResult_c(ram_c* inputRam, long* inputSc_time);
  void step(int i);
  int getCycleCounter();
  ~verilatorResult_c();




private:
  ram_c* ram;
  int cycleCounter;  //cycles has finished since reset
  long* sc_time;
  VsimTOP *simTOP;
  VerilatedVcdC* tfp = NULL;
  void getDiffTestResult() override;

  void evalRam();
};
