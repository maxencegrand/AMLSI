# Experimental Setup
## Benchmarks
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
	    
We will show how to launch the experiments done during this thesis. The python scripts present in the script directory launch, for a given domain, all the experiments by varying the different parameters (variant, initial state, number of generated actions). 
## STRIPS
### Ablation study
&rarr; Launching amlsi:

	python3 script/strips/ablation.py <domain> <domain_name> <repository>
For example, the following command launches the ablation study for the Blocksworld domain. The domain's name is blocks and all log files are saved in <repository>/strips/ablation:

	python3 script/strips/ablation.py blocksworld blocks <repository>/strips/ablation

&rarr; Launching accuracy. The following command test problem resolution for all domains learned previously.

	python3 script/strips/ablation_acc.py domain <repository>
	python3 script/strips/ablation_acc.py blocksworld <repository>/strips/ablation
	
	
### LSONIO
&rarr; Launching lsonio:

	python3 script/strips/lsonio.py domain domain_name <repository>
For example, the following command launches LSONIO for the Blocksworld domain. The domain's name is blocks and all log files are saved in <repository>/strips/lsonio:
	python3 script/strips/lsonio.py blocksworld blocks <repository>/strips/lsonio

&rarr; Launching accuracy. The following command test problem resolution for all domains learned previously.

	python3 script/strips/lsonio_acc.py domain <repository>
	python3 script/strips/lsonio_acc.py blocksworld <repository>/strips/lsonio
### Convergent
&rarr; Launching convergent amlsi:

	python3 script/strips/convergent/amlsi.py domain domain_name <repository>
For example, the following command launches the convergent learning for the Blocksworld domain. The domain's name is blocks and all log files are saved in <repository>/strips/convergent:
	python3 script/strips/convergent/amlsi.py blocksworld blocks <repository>/strips/convergent

&rarr; Launching accuracy. The following command test problem resolution for all domains learned previously.

	python3 script/strips/convergent/amlsi_acc.py domain <repository>
	python3 script/strips/convergent/amlsi_acc.py blocksworld <repository>/strips/convergent
## Temporal
&rarr; Launching temporal amlsi:

	python3 script/temporal/amlsi.py domain domain_name <repository>
For example, the following command launches the temporal learning for the Match domain. The domain's name is matchcellar and all log files are saved in <repository>/temporal:
	python3 script/temporal/amlsi.py match matchcellar <repository>/temporal

&rarr; Launching accuracy. The following command test problem resolution for all domains learned previously.

	python3 script/temporal/amlsi_acc.py domain <repository>
	python3 script/temporal/amlsi_acc.py match <repository>/temporal
## HTN
&rarr; Launching hierarchical amlsi:

	python3 script/htn/amlsi.py domain domain_name <repository>
For example, the following command launches the ablation study for the Blocksworld domain. The domain's name is blocks and all log files are saved in <repository>/htn:
	python3 script/htn/amlsi.py blocksworld blocks <repository>/htn

&rarr; Launching accuracy. The following command test problem resolution for all domains learned previously.

	python3 script/htn/amlsi_acc.py domain <repository>
	python3 script/htn/amlsi_acc.py blocksworld <repository>/htn