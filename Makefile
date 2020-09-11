verilog:
	sbt "project sim; run -td build/generated-src"

clean:
	sbt "clean"
	rm -r build