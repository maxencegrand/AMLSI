# Results extraction and visualization

Once all the experiments have been done, we can generate the .csv files compiling the results and generate the graphs. The following command generates the .csv files and saves them in the loger/csv folder:

	python3 loger/extractCSV.py
	
For a question of memory space, the log files (~50Gb) are not present on this git repository. Nevertheless the generated .csv files have been kept on this git repository. Finally, graph used in the thesis manuscript are generated as follows and saves in the loger/graph folder:

## STRIPS
### Ablation Study
	cd loger/tikz/strips/ablation
	python3 tex_generator.py 
	make
	
<object data="./loger/graph/strips/ablation/Distance_blocksworld.pdf" type="application/pdf" width="700px" height="700px">
    <embed src="./loger/graph/strips/ablation/Distance_blocksworld.pdf">
        <p>This browser does not support PDFs. Please download the PDF to view it: <a href="./loger/graph/strips/ablation/Distance_blocksworld.pdf">Download PDF</a>.</p>
    </embed>
</object>
