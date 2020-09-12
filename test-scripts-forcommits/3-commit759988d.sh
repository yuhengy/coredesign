## This is used to test:
#  1. use git repo to compile a nemu-so
#  2. print nemu results and verilator results
set -v

rm ${COREDESIGN_HOME}/verilator/diffLib/riscv64-nemu-interpreter-so
rm -rf ${NEMU_HOME}
git clone https://github.com/OSCPU/nemu.git ${NEMU_HOME}
cd ${NEMU_HOME}
echo "nemu_ref: \$(DIFF_REF_SO)" >> Makefile
make DIFF=nemu ISA=riscv64 nemu_ref
cp ${NEMU_HOME}/build/riscv64-nemu-interpreter-so ${COREDESIGN_HOME}/verilator/diffLib

cd ${COREDESIGN_HOME}
make clean
make runVerilator