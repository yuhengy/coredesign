## This is used to test:
#  1. run TR-Thread and test command like `help` in it.

set +v
echo "--------------------------------------------------------------------------"
echo "Get started with command \`help \`"
echo "--------------------------------------------------------------------------"

set -v
cd ${COREDESIGN_HOME}
make clean
make getFromRtthreadRepo
make runVerilator
