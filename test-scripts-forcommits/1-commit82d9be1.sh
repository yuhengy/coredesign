## This is used to test:
#  1. sbt compile into a correct .v file
set -v

make clean
make verilog

echo "-------------------------------"
cat build/generated-verilog/simTOP.v
echo "-------------------------------"
echo "Please check This file."