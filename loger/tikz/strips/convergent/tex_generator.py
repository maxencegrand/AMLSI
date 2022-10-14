import csv
from math import sqrt

IDX_METRIC = {"Size":0, "Obs":1, "Noise":2, "IS":3, "FScore":5,"Distance":4,"Accuracy":6,"IPC":7, "Time":8, "Iterations":9}
IDX_SCENARIO = {"Scenario_1":0,"Scenario_2":1,"Scenario_3":2,"Scenario_4":3}
print("Generate TIKZ file")

def mean(l):
    if len(l) == 0:
        return 0

    ll = []
    for x in l:
        ll.append(float(x))
    N=0
    sum_=0
    for x in ll:
        N+=1
        sum_+=x
    return float(sum_/N) if sum_ > 0 else 0

def std(l):
    ll = []
    N=0
    for x in l:
        ll.append(float(x))
        N+=1
    mean_ = mean(l)
    sum_=0
    for x in ll:
        sum_+=((x - mean_)*(x - mean_))
    return float(sqrt((1/N)*sum_))

colors={20:"blue", 40:"green", 60:"red", 80:"purple", 100:"black"}
mark={20:"o", 40:"pentagon", 60:"triangle", 80:"diamond", 100:"square"}
def generate_tex_group(data, domain, metric):
    res = ""
    res = "%s\n%s" % (res, "\\documentclass[border=1pt]{standalone}")
    res = "%s\n%s" % (res, "\\usepackage{pgfplots}\n\\usepgfplotslibrary{groupplots}\n\\usetikzlibrary{matrix,positioning}")
    res = "%s\n%s" % (res, "\\pgfplotsset{/pgfplots/group/.cd, horizontal sep=2cm, vertical sep=2cm}")
    res = "%s\n%s" % (res, "\\begin{document}")
    res = "%s\n%s" % (res, "\\begin{tikzpicture}")
    res = "%s\n%s" % (res, "\\begin{groupplot}[group style={group name=my plots, group size=4 by 5,}]")

    for noise in [0,1,5,20]:
        res = "%s\n%s%d%s%d%s%s%s" % (res, "\\nextgroupplot[title=\\LARGE Noise: ",noise, "\\%,ymin=0,ymax=",ymax[domain][metric],", xlabel={T},ylabel={", metric, " $(\\%)$}]")
        for f in [20,40,60,80,100]:
            res = "%s\n\t%s%s%s%s%s" % (res, "\\addplot[color=",colors[f],",mark=",mark[f],"] coordinates {")
            for size in data[domain].keys():
                res = "%s\n\t(%d,%f)" % (res, size, data[domain][size][f][noise][metric]["mean"])
            res = "%s\n%s%s%d%s" % (res, "};","\\label{plots:",f,"}")

    res =  "%s\n%s" % (res, "\\end{groupplot}\n")
    for i in [0]:
        res =  "%s\n%s%d%s" % (res, "\\node[below = 1.2cm of my plots c1r",(i+1),".south] (tmp1) {};\n")
        res =  "%s\n%s%d%s" % (res, "\\node[below = 1.2cm of my plots c4r",(i+1),".south] (tmp2) {};")
        # res =  "%s\n%s%d%s" % (res, "\\node (b) at ($(tmp1)!0.5!(tmp2)$) {\LARGE Observability: ",OBS_[i],"\\%};\n")
    res =  "%s\n%s" % (res, "\\node (tmp) at ($(tmp1)!0.5!(tmp2)$) {};")
    res =  "%s\n%s\n%s" % (res, "\\matrix[matrix of nodes,anchor=south,draw,inner sep=0.2em,draw]\nat ([yshift=-8.5ex]tmp)",\
                "{\\Large Observability:&[45pt]\\ref{plots:20}&\\LARGE 20\%&[45pt]\\ref{plots:40}&\LARGE 40\% &[45pt]\\ref{plots:60}&\LARGE 60\% &[45pt]\\ref{plots:80}&\LARGE 80\% &[45pt]\\ref{plots:100}&\LARGE 40\%\\\\};")
    res =  "%s\n%s%s" % (res, "\\end{tikzpicture}\n", "\\end{document}\n")
    return res

domain = [\
"blocksworld",\
"gripper",\
"hanoi",\
"n-puzzle",\
"peg",\
"parking",\
"zenotravel",\
"NegVisitAll",\
"NegElevator",\
"spanner",\
"logistics",\
"floortile",\
"sokoban"
]

ymax = {\
"blocksworld":{"Distance":40, "Accuracy":100, "FScore":100, "Time":50, "Iterations":50, "IPC":25},\
"gripper":{"Distance":50, "Accuracy":100, "FScore":100, "Time":50, "Iterations":50, "IPC":25},\
"hanoi":{"Distance":40, "Accuracy":100, "FScore":100, "Time":500, "Iterations":50, "IPC":25},\
"n-puzzle":{"Distance":50, "Accuracy":100, "FScore":100, "Time":500, "Iterations":50, "IPC":25},\
"peg":{"Distance":30, "Accuracy":100, "FScore":100, "Time":500, "Iterations":50, "IPC":25},\
"parking":{"Distance":50, "Accuracy":100, "FScore":100, "Time":500, "Iterations":50, "IPC":25},\
"zenotravel":{"Distance":30, "Accuracy":100, "FScore":100, "Time":500, "Iterations":50, "IPC":25},\
"NegVisitAll":{"Distance":30, "Accuracy":100, "FScore":100, "Time":500, "Iterations":50, "IPC":25},\
"NegElevator":{"Distance":20, "Accuracy":100, "FScore":100, "Time":50, "Iterations":50, "IPC":25},\
"spanner":{"Distance":30, "Accuracy":100, "FScore":100, "Time":20, "Iterations":50, "IPC":25},\
"logistics":{"Distance":40, "Accuracy":100, "FScore":100, "Time":500, "Iterations":50, "IPC":25},\
"floortile":{"Distance":20, "Accuracy":100, "FScore":100, "Time":500, "Iterations":50, "IPC":25},\
"sokoban":{"Distance":30, "Accuracy":100, "FScore":100, "Time":500, "Iterations":50, "IPC":25},\
"allDomains":{"Distance":5, "Accuracy":100, "FScore":100, "Time":500, "Iterations":40, "IPC":25}
}


label = {"Distance":"$E_{\\sigma}$", "Accuracy":"Accuracy", "FScore":"FScore", "Time":"Time", "Iterations":"Iterations"}
NOISE_ = [0,1,5,20]
OBS_ = [100,80,60,40,20]
fluent = [100,80,60,40,20]
print("Generate TIKZ file")

data_all_domains = {}
for d in domain:
    data = {}
    file_name = "../../../csv/strips/%s.csv" % (d)
    with open(file_name, newline='') as csvfile:
        spamreader = csv.reader(csvfile, delimiter=' ', quotechar='|')
        size = -1
        dist = []
        fscore = []
        acc = []
        time = []
        iter = []
        ipc = []
        for row in spamreader:
            initial = float(row[IDX_METRIC["IS"]])
            if(initial == 1):
                if(size != -1):
                    if(not size in data):
                        data[size] = {}
                    if(not obs in data[size]):
                        data[size][obs]={}
                    if(not noise in data[size][obs]):
                        data[size][obs][noise]={}
                    data[size][obs][noise]["Distance"]={"mean":mean(dist), "std":std(dist)}
                    data[size][obs][noise]["FScore"]={"mean":mean(fscore), "std":std(fscore)}
                    data[size][obs][noise]["Accuracy"]={"mean":mean(acc), "std":std(acc)}
                    data[size][obs][noise]["Time"]={"mean":mean(time), "std":std(time)}
                    data[size][obs][noise]["Iterations"]={"mean":mean(iter), "std":std(iter)}
                    data[size][obs][noise]["IPC"]={"mean":mean(ipc), "std":std(ipc)}
                        # print("%s %s %f %f %f" % (d, algo, size, obs, noise))
                size = float(row[IDX_METRIC["Size"]])
                obs = float(row[IDX_METRIC["Obs"]])
                noise = float(row[IDX_METRIC["Noise"]])
                dist = []
                fscore = []
                acc = []
                time = []
                iter = []
                ipc = []
            dist.append(float(row[IDX_METRIC["Distance"]]))
            acc.append(float(row[IDX_METRIC["Accuracy"]]))
            fscore.append(float(row[IDX_METRIC["FScore"]]))
            time.append(float(row[IDX_METRIC["Time"]]))
            iter.append(float(row[IDX_METRIC["Iterations"]]))
            ipc.append(float(row[IDX_METRIC["IPC"]]))
        if(not size in data):
            data[size] = {}
        if(not obs in data[size]):
            data[size][obs]={}
        if(not noise in data[size][obs]):
            data[size][obs][noise]={}
        data[size][obs][noise]["Distance"]={"mean":mean(dist), "std":std(dist)}
        data[size][obs][noise]["FScore"]={"mean":mean(fscore), "std":std(fscore)}
        data[size][obs][noise]["Accuracy"]={"mean":mean(acc), "std":std(acc)}
        data[size][obs][noise]["Time"]={"mean":mean(time), "std":std(time)}
        data[size][obs][noise]["Iterations"]={"mean":mean(iter), "std":std(iter)}
        data[size][obs][noise]["IPC"]={"mean":mean(ipc), "std":std(ipc)}
    data_all_domains[d] = data

for d in domain:
    # for met in ["Distance"]:
    for met in ["Distance","FScore","IPC", "Accuracy"]:
        # print(size)
        str_tex = generate_tex_group(data_all_domains, d, met)
        file_name = "%s_%s.tex" % (met, d)
        file_ = open(file_name , "w")
        file_.write(str_tex)
        file_.close()
