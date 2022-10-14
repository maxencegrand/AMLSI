# Results extraction and visualization

Once all the experiments have been done, we can generate the .csv files compiling the results and generate the graphs. The following command generates the .csv files and saves them in the loger/csv folder:

	python3 loger/extractCSV.py
	
For a question of memory space, the log files (~50Gb) are not present on this git repository. Nevertheless the generated .csv files have been kept on this git repository. Finally, graph used in the thesis manuscript are generated as follows and saves in the loger/graph folder:

## STRIPS
### Ablation Study
	cd loger/tikz/strips/ablation
	python3 tex_generator.py 
	make
 
 
 <p>Accuracy distance for Blocksworld:  <a href="./loger/graph/strips/ablation/Accuracy_blocksworld.pdf">Download PDF</a>.</p>

### Convergent
	cd loger/tikz/strips/convergent
	python3 tex_generator.py 
	make

 <p>Accuracy distance for Blocksworld:  <a href="./loger/graph/strips/convergent/Accuracy_blocksworld.pdf">Download PDF</a>.</p>
 
 
## Temporal


	cd loger/tikz/temporal
	python3 tex_generator.py 
	make

 <p>Accuracy distance for Peg:  <a href="./loger/graph/strips/ablation/Accuracy_peg.pdf">Download PDF</a>.</p>
 
## HTN

 
	cd loger/tikz/htn
	python3 tex_generator.py 
	make

 <p>Accuracy distance for Blocksworld:  <a href="./loger/graph/strips/ablation/Accuracy_blocksworld.pdf">Download PDF</a>.</p>