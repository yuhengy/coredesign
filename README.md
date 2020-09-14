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

### Sep13, 2020 commit-9532afa
This commit is the `instFetch` and `decode` stages. These are enough to formalize the codestyle even though this commit is untestable.
Much code is based on [soder](https://github.com/ucb-bar/riscv-sodor)'s 5-stage core and its structure plot is copied into ./documents/ for convenience.

CodeStyle: 
+ I use individual package for each of five stages. 
+ In each class (in each package), I also divide it into several subclasses with `//------begin------ //^^^^^^end^^^^^^`. These subclasses hold following abstractions:
	+ Subclasses named `${stageName} global status` is global to the whole class, i.e. all other subclass can read or write to it.
	+ Other subclasses have **Two Interface** (`//input` and `//output`) and their **Private Val**.
+ In each class, io.input is global to the whole class; while io.output is connected in a individual subclass for clear. 

### Sep14, 2020 commit-adf221a
This commit uses bundle as data/ctrl-io and add execute stage. Untestable.

As code scale up, we futher highlight the **Coding Principles** behind the **Code Style** listed above. Any other styles can be an alternative once they can achieve these principle.

Coding Principles:
+ Name Space (i.e. Visibility) Principle
	+ Whole Project: Only readonly configurations/constants are visible to the whole project.
	+ One file/class(~200 lines): all global value with in one file need to be **explicitly highlighted** by `include`, `import`, `io`, `notation`. These global values should be a trade-off between as few as possible, and as frequently used as possible. Also, this indicate that we should `include/import` as few as possible in order to be more specific.
	+ One function/subclass with `//------begin------ //^^^^^^end^^^^^^` (~20 lines): all local value need to be **highlighted** explicitly by `paramters`, `return`, `notation`, or implicitly by `val` declaration. This is achieved under the cost of more input/output codes.
	+ I suppose **this complex module interface** is intrinsic in hardware design contrary to software design. Chisel provides a cool method to solve it with `<>`, which provides an implicit connection between interfaces with a reasonable hint from `bundle` name. Further, [here](https://www.chisel-lang.org/api/latest/chisel3/Bundle.html) and [here](https://stackoverflow.com/questions/59049673/how-to-initialize-a-reg-of-bundle-in-chisel) provides an implicit connection between interfaces and regs without unbundling.
+ Other Principles for future

Besides, we formalized the Naming convention here.

Naming Convention:
+ One file, one class. File name is the same to the class name.
+ In most cases, use **littleCaseBeginUpCaseFollow** name for both class name or value name.
+ List all usage of `_`:
	+ Type name, use typeName_t, to avoid conflict in this situation: `typeName_t typeName` (instead of the conflict in `typeName typeName`).
	+ Class name, use className_t.
	+ Data wide, like PCSel_w.

### Sep14, 2020 commit-
This commit finishes memory and writeBack pipe-stages and integrate all five stages into mycoreTOP. Untested, but ready to test in next commit.