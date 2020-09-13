### Sep10, 2020 commit-82d9be1
This commit use `BoringUtils`  learnt from example [here](https://www.chisel-lang.org/api/latest/chisel3/util/experimental/BoringUtils$.html)

### Sep10, 2020 commit-743ce95
Set environment on [hubdocker](https://hub.docker.com/repository/docker/yuhengy/coredesign)

### Sep11, 2020 commit-9866e13
This commit learn verilator from this [tutorial](https://www.youtube.com/watch?v=HAQfD35U6-M) and this [document](https://www.veripool.org/projects/verilator/wiki/Documentation). And try a better Makefile refering to [NutShell](https://github.com/OSCPU/NutShell)

### Sep11, 2020 commit-ddcd040
This commit adds dependence chain graphs of files to clearly show the structure. This can be helpful for both understanding the code efficiently and keeping the big picture in mind throughout the project. The key insight from these graphs is that, they show the functionality of files/objects, i.e. **why we have these files**, which is benefited from a consice include/import, i.e. **staying away from redundant include/import, and of course, avoiding any declaration in .cpp**. 

### Sep12, 2020 commit-759988d
This commit adds a nemuResult class to abstract the trace from nemu-so. Also add a ram to load image file and send it to nemu-so. In future, this ram will also send data to verilator.
`riscv64-nemu-interpreter-so` is build through follow commands:
```shell
git clone https://github.com/OSCPU/nemu.git ${NEMU_HOME}
cd ${NEMU_HOME}
echo "nemu_ref: \$(DIFF_REF_SO)" >> Makefile
make DIFF=nemu ISA=riscv64 nemu_ref
cp ${NEMU_HOME}/build/riscv64-nemu-interpreter-so ${COREDESIGN_HOME}/verilator/diffLib
```
Learn `dlfcn.h` from `NutShell/src/test/csrc/difftest.cpp` and this [example](https://www.jianshu.com/p/72cc08405a5a).

### Sep12, 2020 commit-fa9843d
This commit use an alternative method to use sbt. This is better becuase old method must create new subproject whenever create a new dir.

### Sep13, 2020 commit-f793da0
This commit tests three functions:
+ `ram.cpp` feeds chisel data successfully.
+ Chisel write to regfile successfully.
+ Chisel boringUtils the regfile to `verilatorResult.cpp` successfully.

The test method is that we let the `mycorTOP.scala` keep reading the memory and writing it into regfile.
Note Scala does not support 0x80000000, and need use 0x80000000**L** instead.

### Sep13, 2020 commit-
This commit is the `instFetch` and `decode` stages. These are enough to formalize the codestyle even though this commit is untestable.
Much code is based on [soder](https://github.com/ucb-bar/riscv-sodor)'s 5-stage core and its structure plot is copied into ./documents/ for convenience.

CodeStyle: 
+ I use individual package for each of five stages. 
+ In each class (in each package), I also divide it into several subclasses with `//------begin------ //^^^^^^end^^^^^^`. These subclasses hold following abstractions:
	+ Subclasses named `${stageName} global status` is global to the whole class, i.e. all other subclass can read or write to it.
	+ Other subclasses have **Two Interface** (`//input` and `//output`) and their **Private Val**.
+ In each class, io.input is global to the whole class; while io.output is connected in a individual subclass for clear. 
