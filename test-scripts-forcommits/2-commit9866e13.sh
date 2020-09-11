## This is used to test:
#  1. verilator generate .cpp and further runable
#  2. Makefile works well
set -v

make clean
make verilog
make verilator
make runVerilator

make clean
make runVerilator