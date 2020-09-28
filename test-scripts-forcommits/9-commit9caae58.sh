## This is used to test:
#  1. jal is executed in Chisel and next instruction is unimplement assert error
set -v
cd ${COREDESIGN_HOME}
make clean
make getFromOfficialRepo
testName=officialTest/rv64ui-p-lui make runVerilator

set +v
echo "--------------------------------------------------------------------------"
echo "jal is executed in Chisel and next instruction is unimplement assert error"
echo "--------------------------------------------------------------------------"