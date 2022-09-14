# AMLSI : Action Model Learning with State machine Interaction

## Description

AMLSI is an action model learner using interactions with state machine. The key idea of the AMLSI approach is to interact with the environment in which the autonomous agent will have to solve planning problems to learn the action model: AMSLI tests different actions, observes how the environment evolves when these actions are executed and learns the action model from its observations. This approach is divided into two phases:

* **Querying Phase**: AMLSI queries the environment with feasible and unfeasible action sequences and perceives observations. These observation can be partial and noisy. AMLSI tests both feasible and unfeasible action sequences because infeasible action sequences allow to minimize the amount of (feasible) actions to be executed in the environment and thus minimize the cost of the training dataset acquisition. Also, the action sequences will be generated randomly to avoid the overfitting issue. This phase takes as input the set of actions that the autonomous agent can execute and the set of observable propositions describing the environment. This querying phase will allow AMLSI to build a training dataset that will then be used as input of the learning phase.
* **Learning Phase**: It is during this phase that AMLSI learns the action model. To learn the action model, AMLSI will rely on Regular Grammar Induction. Indeed, planning problems are related to state machines and these state machines are equivalent to regular grammars. Moreover, Regular Grammar Induction is a well-defined problem, and many algorithms has been proposed to solve it like RPNI.




## Dependency

Java JDK 8 must be installed on the computer that will run AMLSI

## Library

All libraries are in the ./lib directory:

* Java library for AI Planning: [PDDL4J](https://github.com/pellierd/pddl4j) 
* PDDL Generator used during the generation step: [PddlGenerator](https://github.com/maxencegrand/PddlGenerator)  
## Compilation

	make build

## Usage

### Name
AMLSI - Action Model Learning with State machine Interaction

### Synopsis
	java -jar build/amlsi.jar [options]

### Main Classes
* main.RUN  : Experiment for the AMLSI algorithm

### Option
* - [withoutRefinement, withoutTabu, incremental, convergent, temporal, htn, onlyMethod]: The tested scenario
* -n, -name [string]: The planning domain's name.
* -t, -type [RPNI | RPNIR]: the regular grammar induction algorithm used.
* -d, -directory [directory]: The directory where al logs and models are saved.
* -o, -domain [file]: The file declaring the planning domain to encapsulate the blackbox.
* -i, -initial [file]: The file declaring initial state.
* -r, -run [integer]: The number of runs for the experimentation.
* -properties [file]: The properties file.
* - [min, max] [integer]: The min (resp. max) size of the generated example.
* -T: The number of iteration during the generation step

## Documentations
	make javadoc
The javadoc is generated in the repository "./build/javadoc".

## Experimental Setup
### Benchmarks
All domains tested in the experiment are present in the "./benchmarcks" folder. For example, for the STRIPS blocksworld domain, the folder and subfolders are organized as follows :

	benchmarks/strips/blocksworld
	│   domain.pddl #The planning domain
	└───initial_states #Folder with all initial states declarations
	│   │   initial1.pddl #The first initial state
	│   │   initial2.pddl #The second initial state
	│   │   initial3.pddl #The third initial state
	└───instances #Folder with all planning problems declarations
	    │   instance-1.pddl #The first planning problem
	    │   instance-2.pddl #The second planning problem
	    │   ...
	    │   instance-20.pddl #The 20th planning problem
### STRIPS
#### Ablation study
Launching amlsi:

	python3 script/strips/ablation.py domain domain_name repertory
	python3 script/strips/ablation.py blocksworld blocks ./log/strips/ablation

Launching accuracy:

	python3 script/strips/ablation_acc.py domain repertory
	python3 script/strips/ablation_acc.py blocksworld ./log/strips/ablation
#### LSONIO
Launching lsonio:

	python3 script/strips/lsonio.py domain domain_name repertory
	python3 script/strips/lsonio.py blocksworld blocks ./log/strips/lsonio

Launching accuracy:

	python3 script/strips/lsonio_acc.py domain repertory
	python3 script/strips/lsonio_acc.py blocksworld ./log/strips/lsonio
#### Convergent
Launching amlsi:

	python3 script/strips/convergent/amlsi.py domain domain_name repertory
	python3 script/strips/convergent/amlsi.py blocksworld blocks ./log/strips/convergent

Launching accuracy:

	python3 script/strips/convergent/amlsi_acc.py domain repertory
	python3 script/strips/convergent/amlsi_acc.py blocksworld ./log/strips/convergent
### Temporal
Launching amlsi:

	python3 script/temporal/amlsi.py domain domain_name repertory
	python3 script/temporal/amlsi.py match matchcellar ./log/temporal

Launching accuracy:

	python3 script/temporal/amlsi_acc.py domain repertory
	python3 script/temporal/amlsi_acc.py match ./log/temporal
### HTN
Launching amlsi:

	python3 script/htn/amlsi.py domain domain_name repertory
	python3 script/htn/amlsi.py blocksworld blocks ./log/htn

Launching accuracy:

	python3 script/htn/amlsi_acc.py domain repertory
	python3 script/htn/amlsi_acc.py blocksworld ./log/htn