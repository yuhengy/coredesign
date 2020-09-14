## This is used to test:
#  1. run chisel and nemu together for LUI.
set -v
cd ${COREDESIGN_HOME}
make clean
make runVerilator

set +v
echo "--------------------------------------------------------------------------"
echo "Please check verilatorResult PC increase from 0x80000000"
echo "Please check verilatorResult reg[5] = 0xffffffff80000000, which will happen begin at cycle 6"
echo "--------------------------------------------------------------------------"