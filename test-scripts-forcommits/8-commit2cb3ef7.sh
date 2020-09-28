## This is used to test:
#  1. All simu results are dumped perfectly.
#  2. Difftest stop when find a mismatch.
set -v
cd ${COREDESIGN_HOME}
make clean
make runVerilator
