#ifndef __RAM_H__
#define __RAM_H__

#include "common.h"

int readImage(wordLen_t* array, char* imgPath);  //This is also used by numeResult.cpp, so is not private

struct CPU_RAM_IO_t {
  bool* instReadIO_reqReady;
  wordLen_t* instReadIO_addr;
  bool* instReadIO_en;
  bool* instReadIO_respValid;
  wordLen_t* instReadIO_data;

  bool* dataReadIO_reqReady;
  wordLen_t* dataReadIO_addr;
  bool* dataReadIO_en;
  bool* dataReadIO_respValid;
  wordLen_t* dataReadIO_data;

  bool* dataWriteIO_reqReady;
  wordLen_t* dataWriteIO_addr;
  wordLen_t* dataWriteIO_data;
  bool* dataWriteIO_en;
  mask_t* dataWriteIO_mask;
  bool* dataWriteIO_respValid;
};

class ram_c {
public:
  ram_c(char* imgPath, CPU_RAM_IO_t inputCPU_RAM_IO);
  void eval();
  void eval_computeLogic();


private:
  wordLen_t ram[RAMSIZE / sizeof(wordLen_t)];
  struct CPU_RAM_IO_t CPU_RAM_IO;

  struct readReqBuffer {
    wordLen_t addr;
    bool en;

    bool busy;
  };
  struct writeReqBuffer {
    bool en;

    bool busy;
  };
  struct readReqBuffer instReadReqBuff_old, dataReadReqBuff_old,
                       instReadReqBuff_new, dataReadReqBuff_new;
  struct writeReqBuffer dataWriteReqBuff_old, dataWriteReqBuff_new;

  bool instRead_canFinish;
  bool dataRead_canFinish;
  bool dataWrite_canFinish;
  bool busy = false;

  //This is a quick version, for read, it return ram[addr-ADDR_START]
  wordLen_t memRead(wordLen_t addr);
  void memWrite(wordLen_t addr, wordLen_t data);
  //end

};

#endif
