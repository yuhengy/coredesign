#include "ram.h"
#include "diffTestIO.h"
#include "VverilatorTOP.h"
#include "verilated_vcd_c.h"

class verilatorResult_c : public diffTestIO_c {
public:
  verilatorResult_c(ram_c* inputRam, long* inputSc_time);
  void step(int i);
  int getCycleCounter();
  bool hitGoodTrap();
  ~verilatorResult_c();




private:
  ram_c* ram;
  int cycleCounter;  //cycles has finished since reset
  long* sc_time;
  VverilatorTOP *verilatorTOP;
  VerilatedVcdC* tfp = NULL;
  void getDiffTestResult() override;

  void evalRam();

};
