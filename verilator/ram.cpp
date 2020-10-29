#include "ram.h"

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <assert.h>

int readImage(wordLen_t* array, char* imgPath)
{
//#ifdef DEBUG
//  array[0] = 0x800002b7;  // lui t0,0x80000
//  //array[1] = 0x0002a023;  // sw  zero,0(t0)
//  //array[2] = 0x0002a503;  // lw  a0,0(t0)
//  return 1;

//#else
  assert(imgPath && "No image file.");
  FILE *fp = fopen(imgPath, "rb");
  assert(fp && "Can not open image file.");

  fseek(fp, 0, SEEK_END);
  int imgSize = ftell(fp);

  fseek(fp, 0, SEEK_SET);
  int ret = fread(array, imgSize, 1, fp);
  assert(ret == 1 && "Can not read image file.");
  fclose(fp);

  return imgSize;
//#endif
}

ram_c::ram_c(char* imgPath, CPU_RAM_IO_t inputCPU_RAM_IO)
{
  readImage(ram, imgPath);
  CPU_RAM_IO = inputCPU_RAM_IO;
  //srand(time(NULL));
//#ifdef DEBUG
//  printf("RAM: %x\n", (unsigned int)ram[0]);
//#endif

}

void ram_c::eval()
{
  bool instRead_canFinish  = true;
  //bool dataRead_canFinish  = true;
  //bool dataWrite_canFinish = true;
  //bool instRead_canFinish  = (rand() % 4) == 0;
  bool dataRead_canFinish  = (rand() % 4) == 0;
  bool dataWrite_canFinish = (rand() % 4) == 0;

  // STEP1 Clock come, update status
  //       (Since reg_new do not depend on reg_old, we can merge reg_new and reg_old indeed)
  instReadReqBuff_old = instReadReqBuff_new;
  dataReadReqBuff_old = dataReadReqBuff_new;
  dataWriteReqBuff_old = dataWriteReqBuff_new;

  //STEP2 Compute IO
  if (instReadReqBuff_old.busy && !instRead_canFinish)
    *(CPU_RAM_IO.instReadIO_reqReady)  = false;
  else
    *(CPU_RAM_IO.instReadIO_reqReady)  = true;
  if (dataReadReqBuff_old.busy && !dataRead_canFinish)
    *(CPU_RAM_IO.dataReadIO_reqReady)  = false;
  else
    *(CPU_RAM_IO.dataReadIO_reqReady)  = true;
  if (dataWriteReqBuff_old.busy && !dataWrite_canFinish)
    *(CPU_RAM_IO.dataWriteIO_reqReady) = false;
  else
    *(CPU_RAM_IO.dataWriteIO_reqReady) = true;

  if (instReadReqBuff_old.en && instRead_canFinish) {
    *(CPU_RAM_IO.instReadIO_data) = memRead(instReadReqBuff_old.addr);
    *(CPU_RAM_IO.instReadIO_respValid) = true;
  } else {
    *(CPU_RAM_IO.instReadIO_respValid) = false;
  }
  if (dataReadReqBuff_old.en && dataRead_canFinish) {
    *(CPU_RAM_IO.dataReadIO_data) = memRead(dataReadReqBuff_old.addr);
    *(CPU_RAM_IO.dataReadIO_respValid) = true;
  } else {
    *(CPU_RAM_IO.dataReadIO_respValid) = false;
  }
  if (dataWriteReqBuff_old.en && dataWrite_canFinish) {
    *(CPU_RAM_IO.dataWriteIO_respValid) = true;
  } else {
    *(CPU_RAM_IO.dataWriteIO_respValid) = false;
  }

  //STEP3 Compute logic
  if (instReadReqBuff_old.busy && !instRead_canFinish) {
    instReadReqBuff_new.busy = !instRead_canFinish;
  } else {
    instReadReqBuff_new.en   = *(CPU_RAM_IO.instReadIO_en);
    instReadReqBuff_new.addr = *(CPU_RAM_IO.instReadIO_addr);
    instReadReqBuff_new.busy = *(CPU_RAM_IO.instReadIO_en);
  }
  if (dataReadReqBuff_old.busy && !dataRead_canFinish) {
    dataReadReqBuff_new.busy = !dataRead_canFinish;
  } else {
    dataReadReqBuff_new.en   = *(CPU_RAM_IO.dataReadIO_en);
    dataReadReqBuff_new.addr = *(CPU_RAM_IO.dataReadIO_addr);
    dataReadReqBuff_new.busy = *(CPU_RAM_IO.dataReadIO_en);
  }
  if (dataWriteReqBuff_old.busy && !dataWrite_canFinish) {
    dataWriteReqBuff_new.busy = !dataWrite_canFinish;
  } else {
    dataWriteReqBuff_new.en  = *(CPU_RAM_IO.dataWriteIO_en);
    dataWriteReqBuff_new.busy = *(CPU_RAM_IO.dataWriteIO_en);
  }

  if (*(CPU_RAM_IO.dataWriteIO_en) && !(dataWriteReqBuff_old.busy && !dataWrite_canFinish)) {
    wordLen_t fullMask = 0;
    for(int i = 0; i < sizeof(wordLen_t); i++) {
      wordLen_t byteEn = ((*(CPU_RAM_IO.dataWriteIO_mask) & (1<<i)) != 0);
      for (int j = 0; j < 8; j++) {
        fullMask |= (byteEn << (8*i+j));
      }
    }
    wordLen_t dataToWrite = (fullMask & *(CPU_RAM_IO.dataWriteIO_data)) 
                          | ((~fullMask) & memRead(*(CPU_RAM_IO.dataWriteIO_addr)));
    memWrite(*(CPU_RAM_IO.dataWriteIO_addr), dataToWrite);
  }
}

wordLen_t ram_c::memRead(wordLen_t addr)
{
  assert((addr % sizeof(wordLen_t)) == 0
        && "Ram Addr should be aligned");
  if (ADDR_START <= addr 
                   && addr <= ADDR_START + RAMSIZE / sizeof(wordLen_t)
        && "Addr out of range") {
    return ram[(addr - ADDR_START) / sizeof(wordLen_t)];
  } else {
    printf("Warning: Addr out of range!!!\n");
    return 0;
  }
}

void ram_c::memWrite(wordLen_t addr, wordLen_t data)
{
  assert((addr % sizeof(wordLen_t)) == 0
        && "Ram Addr should be aligned");
  assert(ADDR_START <= addr 
                   && addr <= ADDR_START + RAMSIZE / sizeof(wordLen_t)
        && "Addr out of range");
  ram[(addr - ADDR_START) / sizeof(wordLen_t)] = data;
}
