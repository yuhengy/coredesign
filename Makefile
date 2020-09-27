PWD := $(shell pwd)## In principle, this is useless,
                   #  but this make me feel safer when seeing codes that verilator have a makefile in subdirectories,
                   #  though that makefile does not use PWD


#*****************************************************************************
#******************** Here We Start the Command Line Envs ********************
#***************** You Need Overwrite Them from Command Line *****************
#*****************************************************************************
testName ?= myTest/singleLUI
#testName ?= rv64ui-p-lui
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

#----------MyTest File Begin-------------
myTestSFileDir = $(PWD)/testbench/myTest
myTestSFile = $(wildcard $(myTestSFileDir)/*.S)
myTestBuildDir = $(PWD)/build/testbench/myTest
myTestBinFile = $(myTestSFile:$(myTestSFileDir)/%.S=$(myTestBuildDir)/%.bin)
#----------MyTest File End-------------

#----------Official riscv-tests File Begin-------------
officialTestBuildDir = $(PWD)/build/testbench/officialTest
officialTestDumpFile := $(shell find $(officialTestBuildDir) -name '*.dump')
officialTestObjFile := $(officialTestDumpFile:%.dump=%)
officialTestBinFile := $(officialTestObjFile:%=%.bin)
#----------Official riscv-tests File End-------------

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
#	echo $(officialTestBinFile)

#---------Chisel to verilog Begin-------------
$(verilogFile): $(chiselFile)
	sbt "runMain sim.elaborate -td $(verilogDir) --full-stacktrace"
	#sbt "project sim; run -td $(verilogDir)" #alternative sbt usage

getVerilog: $(verilogFile)

cleanVerilog:
	rm -rf $(verilogDir)
#---------Chisel to verilog End-------------

#---------Nemu-src to  Nemu-os Begin-------------
$(NEMU_SO):
	make -C $(NEMU_HOME) DIFF=nemu ISA=riscv64 ${NEMU_HOME}/build/riscv64-nemu-interpreter-so
	cp ${NEMU_HOME}/build/riscv64-nemu-interpreter-so $(NEMU_SO)
getNemuOS: $(NEMU_SO)
#---------Nemu-src to  Nemu-os End-------------

#---------Testbench-MyTest .S to .bin Begin-------------
$(myTestBuildDir)/%.o: $(myTestSFileDir)/%.S
	mkdir -p $(myTestBuildDir)
	riscv64-linux-gnu-gcc -c $^ -o $@
	cp $^ $(myTestBuildDir)/
$(myTestBuildDir)/%.bin: $(myTestBuildDir)/%.o
	riscv64-linux-gnu-objcopy -O binary $^ $@

.PRECIOUS: $(myTestBuildDir)/%.o  # to avoid makefile rm .o files automatically

getMyTestbench: $(myTestBinFile)

cleanMyTestbench :
	rm -rf $(myTestBuildDir)
#---------Testbench-MyTest .S to .bin End-------------

#---------Get Official riscv-tests Begin-------------
## TODO: for now, this should be done manully
RISCVTESTS_HOME = /coredesign-env/riscv-tests
getFromOfficialRepo:
	cd $(RISCVTESTS_HOME) && autoconf && ./configure --prefix=$(RISCVTESTS_HOME)/install
	make -C $(RISCVTESTS_HOME) install
	mkdir -p $(officialTestBuildDir)
	cp $(RISCVTESTS_HOME)/install/share/riscv-tests/isa/rv64ui*-p-* $(officialTestBuildDir)

$(officialTestBuildDir)/%.bin: $(officialTestBuildDir)/%
	riscv64-unknown-elf-objcopy -O binary $^ $@

getOfficialTestbench: $(officialTestBinFile)

cleanOfficialTestbench:
	rm -rf $(officialTestBuildDir)
#---------Get Official riscv-tests End-------------

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

runVerilator: $(verilatorRunable) $(PWD)/build/testbench/$(testName).bin
	$(verilatorRunable) $(PWD)/build/testbench/$(testName).bin

cleanVerilator:
	rm -rf $(verilatorDir)
#---------Assemble Verilog-Nemu-Testbench to Runable End-------------

#---------Get Documents PDF Begin-------------
%.pdf: %.gv
	dot -Tpdf $^ -o $@

getDocuments: $(documentsPDF)
#---------et Documents PDF End-------------

clean:
	sbt "clean"
	rm -rf $(PWD)/build
#^^^^^^^^^^^^^^^^^^^^^^^^^^^^^END^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

#cd riscv-tests
#git submodule update --init --recursive
#autoconf
#./configure --prefix=/coredesign-env/coredesign/build/testbench
#make install
