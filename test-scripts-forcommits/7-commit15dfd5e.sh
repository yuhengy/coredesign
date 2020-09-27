## This is used to test:
#  1. official tests are compiled and copy to `build/testbench/officialTest`
#  2. .obj in official tests are stripped to .bin
#  3. chisel and nemu run on these tests
set -v
cd ${COREDESIGN_HOME}
make clean
make getFromOfficialRepo
testName=officialTest/rv64ui-p-lui make runVerilator

set +v
echo "--------------------------------------------------------------------------"
echo "Chisel will assert error at the first instructioin."
echo "To check nemu works will, you can disable chisel simulation in \`verilator/main.cpp\`"
echo "--------------------------------------------------------------------------"