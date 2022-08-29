JAVAC=javac
JAR=jar
FLAGS=-Xlint:unchecked
SRC_PATH=src/main/java/
CLASSPATH=./lib/pddl4j-4.0.jar:./lib/PddlGenerator.jar
SRC=${wildcard ${SRC_PATH}fr/uga/amlsi/baseline/*.java}  \
    ${wildcard ${SRC_PATH}fr/uga/amlsi/baseline/lsonio/*.java}  \
    ${wildcard ${SRC_PATH}fr/uga/amlsi/exceptions/*.java} \
    ${wildcard ${SRC_PATH}fr/uga/amlsi/fsm/*.java} \
    ${wildcard ${SRC_PATH}fr/uga/amlsi/learning/simulator/*.java} \
    ${wildcard ${SRC_PATH}fr/uga/amlsi/learning/simulator/hierarchical/*.java} \
    ${wildcard ${SRC_PATH}fr/uga/amlsi/learning/*.java} \
    ${wildcard ${SRC_PATH}fr/uga/amlsi/learning/hierarchical/*.java} \
    ${wildcard ${SRC_PATH}fr/uga/amlsi/learning/preprocess/*.java} \
    ${wildcard ${SRC_PATH}fr/uga/amlsi/temporal/*.java} \
    ${wildcard ${SRC_PATH}fr/uga/amlsi/main/*.java} \
    ${wildcard ${SRC_PATH}fr/uga/amlsi/learning/*.java} \
    ${wildcard ${SRC_PATH}fr/uga/amlsi/main/*.java} \
    ${wildcard ${SRC_PATH}fr/uga/amlsi/main/experiment/*.java}

PACKAGES=fr.uga.amlsi.main fr.uga.amlsi.main.experiment fr.uga.amlsi.baseline fr.uga.amlsi.baseline.lsonio fr.uga.amlsi.fsm fr.uga.amlsi.learning fr.uga.amlsi.simulator fr.uga.amlsi.temporal
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
