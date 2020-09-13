## This is used to test:
#  1. ram.cpp feeds chisel data successfully.
#  2. chisel write to regfile successfully.
#  3. chisel boringUtils the regfile to verilatorResult.cpp successfully.
set -v
cd ${COREDESIGN_HOME}
make clean
make runVerilator

set +v
echo "--------------------------------------------------------------------------"
echo "Please check **verilatorResult reg[5] = 0x800002b7**"
echo "i.e. you can search for **verilatorCycle *: PC=0---reg[5] = 0x800002b7**"
echo "--------------------------------------------------------------------------"