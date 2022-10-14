# AMLSI : Action Model Learning with State machine Interaction

## Description

AMLSI is an action model learner using interactions with state machine. The key idea of the AMLSI approach is to interact with the environment in which the autonomous agent will have to solve planning problems to learn the action model: AMSLI tests different actions, observes how the environment evolves when these actions are executed and learns the action model from its observations. This approach is divided into two phases:

* **Querying Phase**: AMLSI queries the environment with feasible and unfeasible action sequences and perceives observations. These observation can be partial and noisy. AMLSI tests both feasible and unfeasible action sequences because infeasible action sequences allow to minimize the amount of (feasible) actions to be executed in the environment and thus minimize the cost of the training dataset acquisition. Also, the action sequences will be generated randomly to avoid the overfitting issue. This phase takes as input the set of actions that the autonomous agent can execute and the set of observable propositions describing the environment. This querying phase will allow AMLSI to build a training dataset that will then be used as input of the learning phase.
* **Learning Phase**: It is during this phase that AMLSI learns the action model. To learn the action model, AMLSI will rely on Regular Grammar Induction. Indeed, planning problems are related to state machines and these state machines are equivalent to regular grammars. Moreover, Regular Grammar Induction is a well-defined problem, and many algorithms has been proposed to solve it like RPNI.




## Dependency

Java JDK 8 must be installed to run AMLSI
python 3 must be installed to launches experiments and extracts results
pdfTeX 3.14159265-2.6-1.40.20 (TeX Live 2019/Debian) must be installed to generate results graph
## Compilation

	make build

## Usage

### Name
AMLSI - Action Model Learning with State machine Interaction

### Synopsis
	java -jar build/amlsi.jar [options]

### Main Classes
* main.RUN  : Experiment for the AMLSI algorithm

### Options
* - [withoutRefinement, withoutTabu, incremental, convergent, temporal, htn, onlyMethod]: The tested scenario
* -n, -name [string]: The planning domain's name.
* -t, -type [RPNI | RPNIPC]: the regular grammar induction algorithm used.
* -d, -directory [directory]: The directory where al logs and models are saved.
* -o, -domain [file]: The file declaring the planning domain to encapsulate the blackbox.
* -i, -initial [file]: The file declaring the initial state.
* -r, -run [integer]: The number of runs for the experimentation.
* -properties [file]: The properties file containing value of observability and noise and seeds used for observations generation.
* - [min, max] [integer]: The min (resp. max) size of the generated example.
* -T: The number of iteration during the generation step, i.e. the number of positive examples

Here is a commented example of AMLSI execution. The following command launches classical AMLSI (STRIPS learning) with all refinement steps. DFA Learning step is performed using pairewaise sequences. The learned domain is blocks with the first initial state. Learned domains are saved in the folder ".log". 20 positive examples with 20 actions are generated during the generation step. The properties file used is properties_files/config.properties (the observability varies between 20% and 100%, the noise level varies between 0% and 20%).

	@user:~/amlsi$ java -jar build/amlsi.jar -t RPNIPC -n blocks -o benchmarks/strips/blocksworld/domain.pddl -i benchmarks/strips/blocksworld/initial_states/initial1.pddl -d ./log -min 20 -max 20 -T 20 -properties properties_files/config.properties -r 1 
	# actions 18 //The number of actions
	# predicate 16 //The number of propositions
	Initial state : benchmarks/strips/blocksworld/initial_states/initial1.pddl
	benchmarks/strips/blocksworld/domain.pddl

	RPNIPC run : 0 //The run id (0 - 9) and the RPNI variant used (with pairewise sequences)
	//The training set
	I+ size : 20 //The number of positive examples
	I- size : 2071 //The number of negative examples
	x+ mean size : 20.0 //The mean size of positive examples
	x- mean size : 10.885079 //The mean size of negative examples
	//The testing set
	E+ size : 100
	E- size : 27079
	e+ mean size : 50.53
	e- mean size : 34.376564
	#Observed states : 400.0 //Number of observations
	#States : 23 // Number of state sin the learned DFA
	#Transitions : 47 // NUmber of transitions
	Compression level : 17.391304 //  #Observations / #States
	Time Automaton : 3.118 //Time to learn the DFA + metrics evaluating the learned DFA
	Recall = 0.32
	Precision = 0.2962963
	Fscore automaton 0.30769232
	############################################
	### Fluent = 20.0% ### // Number of visible propositions

	*** Noise = 0.0% *** // Noise level
	STRIPS Generation
	STRIPS Refinment
	Time Domain : 0.952 //Time to learn the domain
	Syntactical distance : 0.0 //Syntactical + Semantical (FScore) metrics
	FSCORE : 1.0

	*** Noise = 1.0% ***
	STRIPS Generation
	STRIPS Refinment
	Time Domain : 1.422
	Syntactical distance : 0.0
	FSCORE : 1.0

	*** Noise = 5.0% ***
	STRIPS Generation
	STRIPS Refinment
	Time Domain : 30.136
	Syntactical distance : 0.057870373
	FSCORE : 0.6666667

	*** Noise = 20.0% ***
	STRIPS Generation
	STRIPS Refinment
	Time Domain : 8.862
	Syntactical distance : 0.0
	FSCORE : 1.0
	############################################
	### Fluent = 40.0% ### // Number of visible propositions

	*** Noise = 0.0% *** // Noise level
	[...]
	
Learned domains are generated in ./log. The file names are generated as follows:

	domain.<obs>.<noise>.<run>.pddl
for example domain.40.5.0.pddl is the domain learned at run with 40% of observability and 5% of noise in the observations. 

	user:~/amlsi$ cat ./log/domain.40.5.0.pddl 
	(define (domain blocks)
	(:requirements :strips :typing :negative-preconditions)
	(:types
	block - object
	)
	(:predicates
		(clear ?x1 - block)
		(ontable ?x1 - block)
		(handempty)
		(holding ?x1 - block)
		(on ?x1 - block ?x2 - block)
	)
	(:action pick-up
		:parameters (?x1 - block )
		:precondition (and
		(clear ?x1)
		(ontable ?x1)
		(not(holding ?x1))
		(handempty))
		:effect (and
		(not(clear ?x1))
		(not(ontable ?x1))
		(holding ?x1)
		(not(handempty)))
	)
	[...]

## Documentations
	make javadoc
The javadoc is generated in the folder "./build/javadoc".

Procedure to launch all experiments are given [here](./experiments.md).

Procedure to extract results from log files and generate graphs are given [here](./results.md).
