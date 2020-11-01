PWD := $(shell pwd)## In principle, this is useless,
                   #  but this make me feel safer when seeing codes that verilator have a makefile in subdirectories,
                   #  though that makefile does not use PWD


#*****************************************************************************
#******************** Here We Start the Command Line Envs ********************
#***************** You Need Overwrite Them from Command Line *****************
#*****************************************************************************
#testName_all += myTest/singleLUI

#*********************************
# AMCPUTest
#*********************************
#testName_all += AMCPUTest/add-longlong-riscv64-nutshell#done
#testName_all += AMCPUTest/add-riscv64-nutshell#done
#testName_all += AMCPUTest/bit-riscv64-nutshell#done
#testName_all += AMCPUTest/bubble-sort-riscv64-nutshell#done
#testName_all += AMCPUTest/dummy-riscv64-nutshell#done
#testName_all += AMCPUTest/fib-riscv64-nutshell#done
#testName_all += AMCPUTest/if-else-riscv64-nutshell#done
#testName_all += AMCPUTest/load-store-riscv64-nutshell#done
#testName_all += AMCPUTest/max-riscv64-nutshell#done
#testName_all += AMCPUTest/min3-riscv64-nutshell#done
#testName_all += AMCPUTest/mov-c-riscv64-nutshell#done
#testName_all += AMCPUTest/movsx-riscv64-nutshell#done
#testName_all += AMCPUTest/pascal-riscv64-nutshell#done
#testName_all += AMCPUTest/quick-sort-riscv64-nutshell#done
#testName_all += AMCPUTest/select-sort-riscv64-nutshell#done
#testName_all += AMCPUTest/shift-riscv64-nutshell#done
#testName_all += AMCPUTest/string-riscv64-nutshell#done
#testName_all += AMCPUTest/sub-longlong-riscv64-nutshell#done
#testName_all += AMCPUTest/sum-riscv64-nutshell#done
#testName_all += AMCPUTest/switch-riscv64-nutshell#done
#testName_all += AMCPUTest/to-lower-case-riscv64-nutshell#done
#testName_all += AMCPUTest/unalign-riscv64-nutshell#done

#testName_all += AMCPUTest/div-riscv64-nutshell
#testName_all += AMCPUTest/fact-riscv64-nutshell
#testName_all += AMCPUTest/goldbach-riscv64-nutshell
#testName_all += AMCPUTest/hello-str-riscv64-nutshell
#testName_all += AMCPUTest/leap-year-riscv64-nutshell
#testName_all += AMCPUTest/matrix-mul-riscv64-nutshell
#testName_all += AMCPUTest/mul-longlong-riscv64-nutshell
#testName_all += AMCPUTest/prime-riscv64-nutshell
#testName_all += AMCPUTest/recursion-riscv64-nutshell
#testName_all += AMCPUTest/shuixianhua-riscv64-nutshell
#testName_all += AMCPUTest/wanshu-riscv64-nutshell


#*********************************
# officialTest
#*********************************
#testName_all += officialTest/rv64ui-p-add#done
#testName_all += officialTest/rv64ui-p-addi#done
#testName_all += officialTest/rv64ui-p-addiw#done
#testName_all += officialTest/rv64ui-p-addw#done
#testName_all += officialTest/rv64ui-p-and#done
#testName_all += officialTest/rv64ui-p-andi#done
#testName_all += officialTest/rv64ui-p-auipc#done
#testName_all += officialTest/rv64ui-p-beq#done
#testName_all += officialTest/rv64ui-p-bge#done
#testName_all += officialTest/rv64ui-p-bgeu#done
#testName_all += officialTest/rv64ui-p-blt#done
#testName_all += officialTest/rv64ui-p-bne#done
#testName_all += officialTest/rv64ui-p-jal#done
#testName_all += officialTest/rv64ui-p-jalr#done
#testName_all += officialTest/rv64ui-p-lb#done
#testName_all += officialTest/rv64ui-p-lbu#done
#testName_all += officialTest/rv64ui-p-ld#done
#testName_all += officialTest/rv64ui-p-lh#done
#testName_all += officialTest/rv64ui-p-lhu#done
#testName_all += officialTest/rv64ui-p-lui#done
#testName_all += officialTest/rv64ui-p-lw#done
#testName_all += officialTest/rv64ui-p-lwu#done
#testName_all += officialTest/rv64ui-p-or#done
#testName_all += officialTest/rv64ui-p-ori#done
#testName_all += officialTest/rv64ui-p-sb#done
#testName_all += officialTest/rv64ui-p-sd#done
#testName_all += officialTest/rv64ui-p-sh#done
#testName_all += officialTest/rv64ui-p-simple#done
#testName_all += officialTest/rv64ui-p-sll#done
#testName_all += officialTest/rv64ui-p-slli#done
#testName_all += officialTest/rv64ui-p-slliw#done
#testName_all += officialTest/rv64ui-p-sllw#done
#testName_all += officialTest/rv64ui-p-slt#done
#testName_all += officialTest/rv64ui-p-slti#done
#testName_all += officialTest/rv64ui-p-sltiu#done
#testName_all += officialTest/rv64ui-p-sltu#done
#testName_all += officialTest/rv64ui-p-sra#done
#testName_all += officialTest/rv64ui-p-srai#done
#testName_all += officialTest/rv64ui-p-sraiw#done
#testName_all += officialTest/rv64ui-p-sraw#done
#testName_all += officialTest/rv64ui-p-srl#done
#testName_all += officialTest/rv64ui-p-srli#done
#testName_all += officialTest/rv64ui-p-srliw#done
#testName_all += officialTest/rv64ui-p-srlw#done
#testName_all += officialTest/rv64ui-p-sub#done
#testName_all += officialTest/rv64ui-p-subw#done
#testName_all += officialTest/rv64ui-p-sw#done
#testName_all += officialTest/rv64ui-p-xor#done
#testName_all += officialTest/rv64ui-p-xori#done

#testName_all += officialTest/rv64ui-p-fence_i


#*********************************
# rtthread
#*********************************
testName_all += rtthread/rtthread
#^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^END^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


#**********************************************************************
#********************** Here We Start the Files ***********************
#* You Need Not Worry About These If You are Using docker-compose.yml *
#**********************************************************************
topModuleName = verilatorTOP

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

#----------AM cputests File Begin-------------
AMCPUTestBuildDir = $(PWD)/build/testbench/AMCPUTest
#AMCPUTestDumpFile := $(shell find $(officialTestBuildDir) -name '*.txt')
#AMCPUTestObjFile := $(officialTestDumpFile:%.txt=%.elf)
#AMCPUTestBinFile := $(officialTestObjFile:%.elf=%.bin)
#----------AM cputests File End-------------

#----------Official riscv-tests File Begin-------------
officialTestBuildDir = $(PWD)/build/testbench/officialTest
officialTestDumpFile := $(shell find $(officialTestBuildDir) -name '*.dump')
officialTestObjFile := $(officialTestDumpFile:%.dump=%)
officialTestBinFile := $(officialTestObjFile:%=%.bin)
#----------Official riscv-tests File End-------------

#----------RT-Thread File Begin-------------
rtthreadBuildDir = $(PWD)/build/testbench/rtthread
rtthreadDumpFile := $(shell find $(rtthreadBuildDir) -name '*.txt')
rtthreadObjFile := $(rtthreadDumpFile:%.txt=%.elf)
rtthreadBinFile := $(rtthreadObjFile:%.elf=%.bin)
#----------RT-Thread File End-------------

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

#---------Get AM cputests Begin-------------
## TODO: for now, this should be done manully
AMCPUTESTS_HOME = $(PWD)/../nexus-am
getFromAMRepo:
	make -C $(AMCPUTESTS_HOME)/tests/cputest ARCH=riscv64-nutshell
	mkdir -p $(AMCPUTestBuildDir)
	cp $(AMCPUTESTS_HOME)/tests/cputest/build/*-riscv64-nutshell* $(AMCPUTestBuildDir)

cleanAMCPUTestbench:
	rm -rf $(AMCPUTestBuildDir)
#---------Get AM cputests End-------------

#---------Get Official riscv-tests Begin-------------
## TODO: for now, this should be done manully
RISCVTESTS_HOME = $(PWD)/../riscv-tests
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

#---------Get RT-Thread Begin-------------
## TODO: for now, this should be done manully
TRTHREAD_HOME = $(PWD)/../RT-Thread
getFromRtthreadRepo:
	make -C $(TRTHREAD_HOME)/bsp/riscv64 nutshell
	mkdir -p $(rtthreadBuildDir)
	cp $(TRTHREAD_HOME)/bsp/riscv64/build/code.txt $(rtthreadBuildDir)/rtthread.txt
	cp $(TRTHREAD_HOME)/bsp/riscv64/rtthread.elf $(rtthreadBuildDir)
	cp $(TRTHREAD_HOME)/bsp/riscv64/rtthread.bin $(rtthreadBuildDir)

cleanRtthread:
	rm -rf $(rtthreadBuildDir)
#---------Get RT-Thread End-------------

#---------Assemble Verilog-Nemu-Testbench to Runable Begin-------------
$(verilatorRunable): $(verilatorHFile) $(verilatorCppFile) $(verilogFile) $(NEMU_SO)
	verilator --cc \
		--Mdir $(verilatorDir) \
		--trace \
		--exe $(verilatorCppFile) \
		-CFLAGS "-g -DNEMU_SO=\\\"$(NEMU_SO)\\\"" \
		-LDFLAGS "-ldl" \
		$(verilogFile)
	make -C $(verilatorDir) -f V$(topModuleName).mk

getVerilator: $(verilatorRunable)

runVerilator: $(verilatorRunable) ${foreach testName, $(testName_all), $(PWD)/build/testbench/$(testName).bin}
	#TODO: this should break immediately when meet error
	for testName in `echo $(testName_all) | cut -d' ' -f 1-`; do\
		echo "---------TEST ON $${testName}.bin---------";\
		$(verilatorRunable) $(PWD)/build/testbench/$${testName}.bin;\
	done

cleanVerilator:
	rm -rf $(verilatorDir)
#---------Assemble Verilog-Nemu-Testbench to Runable End-------------

#---------Get Documents PDF Begin-------------
%.pdf: %.gv
	dot -Tpdf $^ -o $@

doc: $(documentsPDF)
#---------et Documents PDF End-------------
#^^^^^^^^^^^^^^^^^^^^^^^^^^^^^END^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


#**************************************************************
#***************** Here We Start the Commands *****************
#*************** This is about fpga verification **************
#**************************************************************

pynqProjectDir = /home/xilinx/jupyter_notebooks/project_coredesign
vivadoBitfileName = coredesign.bit

#---------Chisel to verilog FOR FPGA Begin-------------
getVerilogFPGA: $(chiselFile)
	sbt "runMain sim.elaborateFPGA -td $(verilogDir) --full-stacktrace"
#---------Chisel to verilog FOR FPGA End-------------

#---------Run FPGA Simulation Begin-------------
sendToPynqAndRun: ${foreach testName, $(testName_all), $(PWD)/build/testbench/$(testName).bin}
	tar -zcf ../coredesign.tar.gz ../coredesign
	scp ../coredesign.tar.gz myPynq:$(pynqProjectDir)/coredesign.tar.gz
	ssh myPynq "cd $(pynqProjectDir) && tar -zxf coredesign.tar.gz"
	ssh myPynq " cd $(pynqProjectDir)/coredesign/fpga &&\
		python3 runTest.py ../../$(vivadoBitfileName) ${foreach testName, $(testName_all), ../build/testbench/$(testName).bin}\
		"
#---------Run FPGA Simulation End-------------
#^^^^^^^^^^^^^^^^^^^^^^^^^^^^^END^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

clean:
	sbt "clean"
	rm -rf $(PWD)/build
