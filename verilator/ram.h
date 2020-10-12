#ifndef __RAM_H__
#define __RAM_H__

#include "common.h"

int readImage(wordLen_t* array, char* imgPath);  //This is also used by numeResult.cpp, so is not private

struct CPU_RAM_IO_t {
  wordLen_t* instReadIO_addr;
  wordLen_t* instReadIO_data;
  bool* instReadIO_en;

  wordLen_t* dataReadIO_addr;
  wordLen_t* dataReadIO_data;
  bool* dataReadIO_en;

  wordLen_t* dataWriteIO_addr;
  wordLen_t* dataWriteIO_data;
  bool* dataWriteIO_en;
  mask_t* dataWriteIO_mask;
};

class ram_c {
public:
  ram_c(char* imgPath, CPU_RAM_IO_t inputCPU_RAM_IO);
  void eval();


private:
  wordLen_t ram[RAMSIZE / sizeof(wordLen_t)];
  struct CPU_RAM_IO_t CPU_RAM_IO;

  struct readReqBuffer {
    wordLen_t addr;
    bool en;
  };
  struct readReqBuffer instReadReqBuff_old, dataReadReqBuff_old,
                       instReadReqBuff_new, dataReadReqBuff_new;

  //This is a quick version, for read, it return ram[addr-ADDR_START]
  wordLen_t memRead(wordLen_t addr);
  void memWrite(wordLen_t addr, wordLen_t data);
  //end

};

#endif
