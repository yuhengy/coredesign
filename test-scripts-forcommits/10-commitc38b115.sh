## This is used to test:
#  1. AMCPUTest dummy-riscv64-nutshell pass
set -v
cd ${COREDESIGN_HOME}
make clean
make getFromAMRepo
testName=AMCPUTest/dummy-riscv64-nutshell make runVerilator

set +v
echo "--------------------------------------------------------------------------"
echo "You should see \`Program execution has ended. ...\` before PC=0x0000000080000030"
echo "--------------------------------------------------------------------------"