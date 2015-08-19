JAVAC=javac
JAVA=java
CFLAGS=
SRC_FOLDER=src
BIN_FOLDER=bin
MAIN_CLASS=URM_Compiler
JAR_NAME=URM_Compiler.jar

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

#make executable jar
.phony: jar
jar: exe
	@echo "Main-Class: URM_Compiler" >> Manifest.txt
	jar -cvfm $(JAR_NAME) Manifest.txt -C $(BIN_FOLDER)/ .
	rm Manifest.txt

#prepare the repository for publishing (i.e. committing)
.phony: publish
publish: jar
	make clobber