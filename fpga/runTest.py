
from pynq import Overlay, GPIO
import sys
import os
import time
import struct

#--------config begin--------
maxRunTime = 10
numPCToTrace = 16  #MAX is 2048
#--------config end--------

if __name__ == "__main__":
  if len(sys.argv) <= 2:
      print("at least 2 arguments needed!")
      print("Usage: " + sys.argv[0] + " vivadoBitFile testBinFileList")
      exit()
  vivadoBitFile = sys.argv[1]
  testBinFileList = sys.argv[2:]

  coredesign = Overlay(vivadoBitFile)

  for testBinFile in testBinFileList:
    print("---------TEST ON ", testBinFile, "---------")

    ## STEP1 write test bin file to BRAM
    with open(testBinFile, 'rb') as f:
        size = os.path.getsize(testBinFile)
        print("Instruction Number: ", size/4)
        for i in range (0, int(size/4)*4, 4):
          coredesign.axi_bram_ctrl_1.write(i, struct.unpack('I', f.read(4))[0])

    ## STEP2 reset and run CPU
    reset = GPIO(GPIO.get_gpio_pin(1), 'out')
    coredesign.axi_bram_ctrl_0.write(0x0, 0x0)
    reset.write(1)
    time.sleep(1)
    reset.write(0)

    hitGoodTrap = GPIO(GPIO.get_gpio_pin(0), 'in')
    timeOut = True
    for i in range(maxRunTime):
      if hitGoodTrap.read()==1:
        timeOut = False
        break
      time.sleep(1)
      print("Have waited %ds for fpga to finish" % i)

    ## STEP3 summary result
    for i in range(numPCToTrace):
      print("                  ---------------------------> PC = 0x%x <---------------------------" % coredesign.axi_bram_ctrl_0.read(i*16))
    if timeOut:
      print("                          **************************************************************");
      print("                          ***********************Error: Time Out************************");
      print("                          **************************************************************");
      break;
    else:
      print("                          **************************************************************");
      print("                          ************************Hit Good Trap*************************");
      print("                          **************************************************************");
