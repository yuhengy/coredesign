#include "common.h"
#include "ram.h"
#include "diffTestIO.h"

class nemuResult_c : public diffTestIO_c {
public:
  nemuResult_c(char* imgPath);
  void step(int i);
  int getCycleCounter();
  void setDiffTestStatus();




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
  int cycleCounter;
  wordLen_t commitedPC = ADDR_START;
  void getDiffTestResult() override;
};
