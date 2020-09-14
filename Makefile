PWD := $(shell pwd)

topModuleName = simTOP

#---------Src File Begin-------------
scalaFile = $(shell find $(PWD)/src -name '*.scala')
verilatorHFile = $(shell find $(PWD)/verilator -name '*.h')
verilatorCppFile = $(shell find $(PWD)/verilator -name '*.cpp')
#---------Src File End-------------

#---------Lib Begin-------------
NEMU_SO = /ProjectRiscv/coredesign/verilator/diffLib/riscv64-nemu-interpreter-so
#---------Lib End-------------

#---------Build File Begin-------------
verilogDir = $(PWD)/build/generated-verilog
verilogFile = $(verilogDir)/$(topModuleName).v
verilatorDir = $(PWD)/build/generated-cpp
verilatorRunable = $(verilatorDir)/V$(topModuleName)
#---------Build File End-------------


.PHONY: verilog verilator clean

$(verilogFile): $(scalaFile)
	sbt "runMain sim.elaborate -td $(verilogDir) --full-stacktrace"
	#sbt "project sim; run -td $(verilogDir)" #alternative sbt usage

verilog: $(verilogFile)

$(verilatorRunable): $(verilatorHFile) $(verilatorCppFile) $(verilogFile)
	verilator --cc \
		--Mdir $(verilatorDir) \
		--exe $(verilatorCppFile) \
		-CFLAGS "-g -DNEMU_SO=\\\"$(NEMU_SO)\\\"" \
		-LDFLAGS "-ldl" \
		$(verilogFile)
	make -C $(verilatorDir) -f V$(topModuleName).mk

verilator: $(verilatorRunable)

runVerilator: $(verilatorRunable)
	$(verilatorRunable)

clean:
	sbt "clean"
	rm -r build
