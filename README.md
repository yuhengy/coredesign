This commit use `BoringUtils`  learnt from example [here](https://www.chisel-lang.org/api/latest/chisel3/util/experimental/BoringUtils$.html)

Set environment on [hubdocker](https://hub.docker.com/repository/docker/yuhengy/coredesign)

This commit learn verilator from this [tutorial](https://www.youtube.com/watch?v=HAQfD35U6-M) and this [document](https://www.veripool.org/projects/verilator/wiki/Documentation). And try a better Makefile refering to [NutShell](https://github.com/OSCPU/NutShell)

This commit adds dependence chain graphs of files to clearly show the structure. This can be helpful for both understanding the code efficiently and keeping the big picture in mind throughout the project. The key insight from these graphs is that, they show the functionality of files/objects, i.e. **why we have these files**, which is benefited from a consice include/import, i.e. **staying away from redundant include/import, and of course, avoiding any declaration in .cpp**. 

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