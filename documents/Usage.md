Use [Graphviz](http://www.graphviz.org) to plot the graph. More specificly, refer to this [document](http://www.graphviz.org/pdf/dotguide.pdf) on this [page](http://www.graphviz.org/documentation/). For convenience, I also add the same [document](dotguide.pdf) into this repo.

In deed, use
```
dot -Tpdf dependChain-chisel.gv -o dependChain-chisel.pdf
dot -Tpdf dependChain-verilator.gv -o dependChain-verilator.pdf
dot -Tpdf dependChain-verilator-mergeHCPP.gv -o dependChain-verilator-mergeHCPP.pdf
dot -Tpdf dependChain-makefile.gv -o dependChain-makefile.pdf
```
to plot from the `.gv` files