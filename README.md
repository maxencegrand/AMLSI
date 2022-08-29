# AMLSI : Action Model Learning with State machine Interaction

## Description

AMLSI is an action model learner using interactions with state machine.

## Compilation

	make compile

## Usage

### Name
AMLSI - Action Model Learning with State machine Interaction

### Synopsis
	java -jar build/amlsi.jar [options]

### Main Classes
* main.RUN  : Experiment for the AMLSI algorithm

### Option
* - [withoutRefinement, withoutTabu, incremental, convergent, temporal] : The tested scenario
* -n, -name [string] : The planning domain's name.
* -t, -type [RPNI | RPNIR] : the regular grammar induction algorithm used.
* -d, -directory [directory] : The directory where al logs and models are saved.
* -o, -domain [file]: The file declaring the planning domain to encapsulate the blackbox.
* -i, -initial [file] : The file declaring initial state.
* -r, -run [integer] : The number of runs for the experimentation.
* -properties [file] : The properties file.

## Documentations
	make javadoc
The javadoc is generated in the repository "./javadoc".

## Experimental Setup - ICAPS 2021

### Domains used for the experiment
All domains tested in the experiment are present in the "./pddl" folder. For example, for the Gripper domain, the folder and subfolders are organized as follows :

```
pddl/gripper
│   domain.pddl #The planning domain
└───initial_states #Folder with all initial states declarations
│   │   initial1.pddl #The first initial state
│   │   initial2.pddl #The second initial state
│   │   initial3.pddl #The third initial state
└───problem #Folder with all planning problems declarations
    │   problems.txt #A text file with links to all planning problems
    │   p01.pddl #The first planning problem
    │   p02.pddl #The second planning problem
    │   ...
    │   p20.pddl #The 20th planning problem
```

### Scripts for experiments
* ./script_experimentation/icaps_2021 domain domain_name save_directory : Test all scenarii for the domaine "domain" and save results in the directory "save_directory".
* ./script_experimentation/icaps_2021_accuracy domain save_directory : Compute the accuracy.

 ### Example of experiments command
To experiment the AMLSI algorithm for the domain Gripper with five runs on the first initial state, you must execute the following commands :

	make compile
	./script_experimentation/icaps_2021 gripper gripper-typed icaps_2021
	./script_experimentation/icaps_2021_accuracy gripper icaps_2021

### Log files

All log files for the experiment are present in the repository experiment. Log fles for the domain Gripper are in the repository "experiment/gripper_amlsi" and is organized as follows
```
icaps_2021/gripper
└───-GEN #The BASE scenario
└──────IS1 #The first initial state
│   │   │   log.txt #The log file containing all metrics results and benchmark specifications
│   │   │   amlsi_rpni.${f}.${n}.${r}.pddl #The planning domain learnt where ${f} is the level of observability, ${n} is the level of noise and ${r} is he current run
└──────IS2 #The second initial state
│   │   │   log.txt
│   │   │   amlsi_rpni.${f}.${n}.${r}.pddl
└──────IS3 #The third initial state
│   │   │   log.txt
│   │   │   amlsi_rpni.${f}.${n}.${r}.pddl
└──────planning_results.txt #File containing accuracy results
└─── Refine #The BASE + Refine scenario
└──────IS1 #The first initial state
│   │   │   log.txt #The log file containing all metrics results and benchmark specifications
│   │   │   amlsi_rpni.${f}.${n}.${r}.pddl #The planning domain learnt where ${f} is the level of observability, ${n} is the level of noise and ${r} is he current run
└──────IS2 #The second initial state
│   │   │   log.txt
│   │   │   amlsi_rpni.${f}.${n}.${r}.pddl
└──────IS3 #The third initial state
│   │   │   log.txt
│   │   │   amlsi_rpni.${f}.${n}.${r}.pddl
└──────planning_results.txt #File containing accuracy results
└─── COMPLETE #The AMLSI scenario
└──────IS1 #The first initial state
│   │   │   log.txt #The log file containing all metrics results and benchmark specifications
│   │   │   amlsi_rpnir.${f}.${n}.${r}.pddl #The planning domain learnt where ${f} is the level of observability, ${n} is the level of noise and ${r} is he current run
└──────IS2 #The second initial state
│   │   │   log.txt
│   │   │   amlsi_rpnir.${f}.${n}.${r}.pddl
└──────IS3 #The third initial state
│   │   │   log.txt
│   │   │   amlsi_rpnir.${f}.${n}.${r}.pddl
└──────planning_results.txt #File containing accuracy results
└───TABU #The Tabu search alone scenario
└──────IS1 #The first initial state
│   │   │   log.txt #The log file containing all metrics results and benchmark specifications
│   │   │   amlsi_rpnir.${f}.${n}.${r}.pddl #The planning domain learnt where ${f} is the level of observability, ${n} is the level of noise and ${r} is he current run
└──────IS2 #The second initial state
│   │   │   log.txt
│   │   │   amlsi_rpnir.${f}.${n}.${r}.pddl
└──────IS3 #The third initial state
│   │   │   log.txt
│   │   │   amlsi_rpnir.${f}.${n}.${r}.pddl
└──────planning_results.txt #File containing accuracy results
└───LSONIO #The LSONIO alone scenario
└──────IS1 #The first initial state
│   │   │   log.txt #The log file containing all metrics results and benchmark specifications
│   │   │   amlsi_rpnir.${f}.${n}.${r}.pddl #The planning domain learnt where ${f} is the level of observability, ${n} is the level of noise and ${r} is he current run
└──────IS2 #The second initial state
│   │   │   log.txt
│   │   │   amlsi_rpnir.${f}.${n}.${r}.pddl
└──────IS3 #The third initial state
│   │   │   log.txt
│   │   │   amlsi_rpnir.${f}.${n}.${r}.pddl
└──────planning_results.txt #File containing accuracy results
```
## Experimental Setup - ICAPS KEPS 2021

### Domains used for the experiment
All domains tested in the experiment are present in the "./pddl/temporal" folder. For example, for the Gripper domain, the folder and subfolders are organized as follows :

```
pddl/temporal/peg
│   domain.pddl #The planning domain
└───initial_states #Folder with all initial states declarations
│   │   initial1.pddl #The first initial state
│   │   initial2.pddl #The second initial state
│   │   initial3.pddl #The third initial state
└───problem #Folder with all planning problems declarations
    │   problems.txt #A text file with links to all planning problems
    │   p01.pddl #The first planning problem
    │   p02.pddl #The second planning problem
    │   ...
    │   p20.pddl #The 20th planning problem
```

### Scripts for experiments
* ./script_experimentation/temporal domain domain_name save_directory : Test all scenarii for the domaine "domain" and save results in the directory "save_directory".
* ./script_experimentation/temporal_accuracy domain save_directory : Compute the accuracy.
* ./script_experimentation/temporal_reference domain : Compute the accuracy with the ground truth domains.

All results are summerized in the jupyter notebook : log_reader/temporal.ipynb

### Example of experiments command
To experiment the AMLSI algorithm for the domain peg with five runs on the first initial state, you must execute the following commands :

	make compile
	./script_experimentation/temporal peg icaps_keps_2021
	./script_experimentation/temporal_accuracy peg icaps_keps_2021
	./script_experimentation/temporal_reference peg