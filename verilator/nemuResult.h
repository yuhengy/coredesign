#include "common.h"
#include "ram.h"
#include "diffTestIO.h"

class nemuResult_c {
public:
  nemuResult_c(ram_c* inputRam);
  void step(int i);
  int getCycleCounter();
  void getDiffTestResult(diffTestIO_c* diffTestIO);
  void setDiffTestStatus(diffTestIO_c* diffTestIO);




private:
  // --------------APT Begin--------------
  typedef void (* refMemcpyFromDUT_t) (wordLen_t dest, void *src, int n);
  typedef void (* refSetregs_t) (const void *c);
  typedef void (* refGetregs_t) (void *c);
  typedef void (* refStep_t) (int n);
  typedef void (* refInit_t) (void);

  refMemcpyFromDUT_t refMemcpyFromDUT;
  refSetregs_t refSetregs;
  refGetregs_t refGetregs;
  refStep_t refStep;
  refInit_t refInit;
  // --------------APT End--------------
  ram_c* ram;
  int cycleCounter;
};
