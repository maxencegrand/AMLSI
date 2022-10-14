# Results extraction and visualization

Once all the experiments have been done, we can generate the .csv files compiling the results and generate the graphs. The following command generates the .csv files and saves them in the loger/csv folder:

	python3 loger/extractCSV.py
	
For a question of memory space, the log files (~50Gb) are not present on this git repository. Nevertheless the generated .csv files have been kept on this git repository. Finally, graph used in the thesis manuscript are generated as follows and saves in the loger/pdf folder:

	python3 loger/generateGraph.py