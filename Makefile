JAVAC=javac
JAVA=java
CFLAGS=
SRC_FOLDER=src
BIN_FOLDER=bin
MAIN_CLASS=URM_Compiler

SOURCES=$(shell find $(SRC_FOLDER) -type f -iname '*.java')


.phony: exe
exe: $(SOURCES)
	$(JAVAC) $(CFLAGS) -d $(BIN_FOLDER)/ $^

#run without recompiling (make exe must be run once first)
.phony: run
run:
	$(JAVA) -cp `pwd`/$(BIN_FOLDER)/ $(MAIN_CLASS)

#force compilation
.phony: .run
.run: exe run
	
.phony: clobber
clobber:
	rm $(BIN_FOLDER)/*.class
