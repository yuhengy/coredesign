PWD := $(shell pwd)## In principle, this is useless,
                   #  but this make me feel safer when seeing codes that verilator have a makefile in subdirectories,
                   #  though that makefile does not use PWD


#*****************************************************************************
#******************** Here We Start the Command Line Envs ********************
#***************** You Need Overwrite Them from Command Line *****************
#*****************************************************************************
TestName ?= singleLUI
#^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^END^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


#**********************************************************************
#********************** Here We Start the Files ***********************
#* You Need Not Worry About These If You are Using docker-compose.yml *
#**********************************************************************
topModuleName = simTOP

#---------Chisel File Begin-------------
chiselFile = $(shell find $(PWD)/src -name '*.scala')
verilogDir = $(PWD)/build/generated-verilog
verilogFile = $(verilogDir)/$(topModuleName).v
#---------Chisel File End-------------

#---------Nemu Begin-------------
NEMU_SO = $(PWD)/build/riscv64-nemu-interpreter-so
#---------Nemu End-------------

#---------Test File Begin-------------
testSFileDir = $(PWD)/testbench
testSFile = $(wildcard $(testSFileDir)/*.S)
testBuildDir = $(PWD)/build/testbench
testBinFile = $(testSFile:$(testSFileDir)/%.S=$(testBuildDir)/%.bin)
#---------Test File End-------------

#---------Verilator File Begin-------------
verilatorHFile = $(shell find $(PWD)/verilator -name '*.h')
verilatorCppFile = $(shell find $(PWD)/verilator -name '*.cpp')
verilatorDir = $(PWD)/build/generated-cpp
verilatorRunable = $(verilatorDir)/V$(topModuleName)
#---------Verilator File End-------------

#---------Documents File Begin-------------
documentsFile = $(shell find $(PWD)/documents -name '*.gv')
documentsPDF = $(documentsFile:%.gv=%.pdf)
#---------Documents File End-------------
#^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^END^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


#**************************************************************
#***************** Here We Start the Commands *****************
#*************** You Need Not Worry About These ***************
#**************************************************************
.PHONY: getVerilog getTestbench getVerilator runVerilator
.PHONY: cleanTestbench clean
#.PHONY: debugMakefile

#debugMakefile:
#	echo $(verilogDir)

#---------Chisel to verilog Begin-------------
$(verilogFile): $(chiselFile)
	sbt "runMain sim.elaborate -td $(verilogDir) --full-stacktrace"
	#sbt "project sim; run -td $(verilogDir)" #alternative sbt usage

getVerilog: $(verilogFile)
#---------Chisel to verilog End-------------

#---------Nemu-src to  Nemu-os Begin-------------
$(NEMU_SO):
	make -C $(NEMU_HOME) DIFF=nemu ISA=riscv64 ${NEMU_HOME}/build/riscv64-nemu-interpreter-so
	cp ${NEMU_HOME}/build/riscv64-nemu-interpreter-so $(NEMU_SO)
getNemuOS: $(NEMU_SO)
## For this makefile, we assume nemu-os already exists
## To generate it from source code, please refer to test-scripts-forcommits/3-commit759988d.sh
#---------Nemu-src to  Nemu-os End-------------

#---------Testbench .S to .bin Begin-------------
$(testBuildDir)/%.o: $(testSFileDir)/%.S
	mkdir -p $(testBuildDir)
	riscv64-linux-gnu-gcc -c $^ -o $@
$(testBuildDir)/%.bin: $(testBuildDir)/%.o
	riscv64-linux-gnu-objcopy -O binary $^ $@

.PRECIOUS: $(testBuildDir)/%.o  # to avoid makefile rm .o files automatically

getTestbench: $(testBinFile)

cleanTestbench :
	rm -rf $(testBuildDir)
#---------Testbench .S to .bin End-------------

#---------Assemble Verilog-Nemu-Testbench to Runable Begin-------------
$(verilatorRunable): $(verilatorHFile) $(verilatorCppFile) $(verilogFile) $(NEMU_SO)
	verilator --cc \
		--Mdir $(verilatorDir) \
		--exe $(verilatorCppFile) \
		-CFLAGS "-g -DNEMU_SO=\\\"$(NEMU_SO)\\\"" \
		-LDFLAGS "-ldl" \
		$(verilogFile)
	make -C $(verilatorDir) -f V$(topModuleName).mk

getVerilator: $(verilatorRunable)

runVerilator: $(verilatorRunable) $(testBuildDir)/$(TestName).bin
	$(verilatorRunable) $(testBuildDir)/$(TestName).bin
#---------Assemble Verilog-Nemu-Testbench to Runable End-------------

#---------Get Documents PDF Begin-------------
%.pdf: %.gv
	dot -Tpdf $^ -o $@

getDocuments: documentsPDF
#---------et Documents PDF End-------------

clean:
	sbt "clean"
	rm -rf $(PWD)/build
#^^^^^^^^^^^^^^^^^^^^^^^^^^^^^END^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
