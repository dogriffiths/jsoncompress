#
# Directories beneath the main directory
#
SRC:=src
TEST:=test
BUILD:=build
TESTBUILD:=testbuild
LIB:=lib
TESTLIB:=testlib

# Find all the files
SRCFILES:= $(shell find $(SRC) -name '*.java')
TESTFILES:= $(shell find $(TEST) -name '*Test.java')
CLASSFILES:= $(patsubst $(SRC)/%.java,$(BUILD)/%.class, $(SRCFILES))
TESTCLASSFILES:= $(patsubst $(TEST)/%.java,$(BUILD)/%.class, $(TESTFILES))

# Generate classpaths
CP:=$(SRC):$(shell ls -C $(LIB)/*.jar | tr '\t' ':'|sed 's/::/:/g')
TCP:=$(TESTBUILD):$(BUILD):$(shell ls -C $(TESTLIB)/*.jar | tr '\t' ':'|sed 's/::/:/g'):$(CP)

# Compiler commands
JC=javac -sourcepath $(SRC) -classpath $(CP) -d $(BUILD) $(filter %.java,$?)
JTC=javac -sourcepath $(TEST) -classpath $(TCP) -d $(TESTBUILD) $(filter %.java,$?)

.SUFFIXES : .java .class
.PHONY : clean all test

all: $(CLASSFILES) $(TESTCLASSFILES)

$(BUILD):
	mkdir $@

$(TESTBUILD):
	mkdir $@

clean:
	rm -rf $(BUILD)/* && rm -rf $(TESTBUILD)/*

$(CLASSFILES): $(SRCFILES) $(BUILD)
	$(JC) $(patsubst $(BUILD)/%.class,$(SRC)/%.java, $*.class)

$(TESTCLASSFILES): $(TESTFILES) $(CLASSFILES) $(TESTBUILD)
	$(JTC) $(patsubst $(BUILD)/%.class,$(TEST)/%.java, $*.class)

classes: $(CLASSFILES)

test: $(CLASSFILES) $(TESTCLASSFILES)
	java -classpath $(TCP) org.junit.runner.JUnitCore \
		$(patsubst $(TESTBUILD).%.class,%, $(shell find $(TESTBUILD) -name '*Test.class' | sed 's/\//./g' ))
