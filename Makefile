ANTLR4=java -jar /usr/local/lib/antlr-4.5-complete.jar
ANTLRFLAGS=-no-listener -visitor -o $(SRCDIRECTORY)/gen -package com.lyte.gen
SRCDIRECTORY=src/com/lyte
OUTDIRECTORY=out/production/Lyte
JAVAC=javac

all: compile

antlr:
	$(ANTLR4) $(ANTLRFLAGS) Lyte.g4

compile: antlr
	$(JAVAC) -d $(OUTDIRECTORY) -cp $(CLASSPATH):./src $(SRCDIRECTORY)/*.java

clean:
	rm src/com/lyte/gen/*.java
