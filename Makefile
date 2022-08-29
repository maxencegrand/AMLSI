JAVAC=javac
JAR=jar
FLAGS=-Xlint:unchecked
SRC_PATH=src/main/java/
CLASSPATH=./lib/pddl4j-4.0.jar
SRC=${wildcard ${SRC_PATH}baseline/*.java}  \
    ${wildcard ${SRC_PATH}baseline/lsonio/*.java}  \
    ${wildcard ${SRC_PATH}exceptions/*.java} \
    ${wildcard ${SRC_PATH}fsm/*.java} \
    ${wildcard ${SRC_PATH}simulator/*.java} \
    ${wildcard ${SRC_PATH}simulator/hierarchical/*.java} \
    ${wildcard ${SRC_PATH}learning/*.java} \
    ${wildcard ${SRC_PATH}learning/hierarchical/*.java} \
    ${wildcard ${SRC_PATH}learning/preprocess/*.java} \
    ${wildcard ${SRC_PATH}temporal/*.java} \
    ${wildcard ${SRC_PATH}main/*.java} \
    ${wildcard ${SRC_PATH}learning/*.java} \
    ${wildcard ${SRC_PATH}main/*.java} \
    ${wildcard ${SRC_PATH}main/experiment/*.java}

PACKAGES=main main.experiment baseline baseline.lsonio fsm learning simulator temporal
#PACKAGES=main main.experiment baseline baseline.lsonio fsm learning learning.preprocess simulator
CLASSES=${SRC:src/main/java/%.java=%.class}

install: build javadoc

all: build

init: clean
	@mkdir build
	@mkdir build/classes

compile: init
	@echo "Compile"
	@$(JAVAC) -O -Xpkginfo:always -d build/classes -classpath $(CLASSPATH) -sourcepath $(SRC_PATH) ${SRC}

build: compile
	@echo "Build"
	@touch MANIFEST.MF
	@echo "Main-Class: main.Run" > MANIFEST.MF
	@echo "Class-Path: ${CLASSPATH} classes" >> MANIFEST.MF
	@mkdir ./build/lib
	@cp -r ./lib/*.jar ./build/lib
	@${JAR} cvfm build/amlsi.jar MANIFEST.MF -C build/classes .

javadoc: build
	mkdir build/javadoc
	javadoc -cp ${SRC_PATH}:${CLASSPATH} -d build/javadoc ${PACKAGES}

clean:
	@echo "Clean"
	@rm -rf build/classes
	@rm -rf build
	@rm -f MANIFEST.MF
