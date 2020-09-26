## This is used to test:
#  1. testbench is compiled and strip out elf information
#  2. ram.cpp read from .bin and run
#  3. the reorgnized Makefile works well
set -v
cd ${COREDESIGN_HOME}
make clean
make getVerilog
make getTestbench
make getVerilator
make runVerilator

make clean
make runVerilator

set +v
echo "--------------------------------------------------------------------------"
echo "Please check verilatorResult PC increase from 0x80000000"
echo "Please check verilatorResult reg[5] = 0xffffffff80000000, which will happen begin at cycle 6"
echo "--------------------------------------------------------------------------"