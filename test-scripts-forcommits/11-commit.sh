## This is used to test:
#  1. run most of AMCPUTest and print `PASS`
set -v
cd ${COREDESIGN_HOME}
make clean
make getFromAMRepo
make runVerilator

set +v
echo "--------------------------------------------------------------------------"
echo "You should see \`PASS\` and cannot search for \`ERROR\`"
echo "--------------------------------------------------------------------------"