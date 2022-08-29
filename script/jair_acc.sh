rep=experiment/strips
python3 script_experimentation/strips/amlsi_acc.py $1 $rep/AMLSI
python3 script_experimentation/strips/incremental/ablation_acc.py $1 $rep/incremental/ablation
python3 script_experimentation/strips/incremental/size_acc.py $1 $rep/incremental/size
python3 script_experimentation/strips/convergent/amlsi_acc.py $1 $rep/convergent/AMLSI
