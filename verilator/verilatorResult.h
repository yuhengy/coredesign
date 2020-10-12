#include "ram.h"
#include "diffTestIO.h"
#include "VverilatorTOP.h"
#include "verilated_vcd_c.h"

class verilatorResult_c : public diffTestIO_c {
public:
  verilatorResult_c(char* imgPath, long* inputSc_time);
  void step(int i);
  int getCycleCounter();
  bool hitGoodTrap();
  ~verilatorResult_c();




private:
  ram_c* ram;
  long* sc_time;
  VverilatorTOP *verilatorTOP;
  VerilatedVcdC* tfp = NULL;
  void getDiffTestResult() override;

};
