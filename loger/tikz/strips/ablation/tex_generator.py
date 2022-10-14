import csv
from math import sqrt

IDX_METRIC = {"Size":0, "Obs":1, "Noise":2, "IS":3, "FScore":5,"Distance":4,"Accuracy":6,"Time":7}
IDX_SCENARIO = {"Scenario_1":0,"Scenario_2":1,"Scenario_3":2,"Scenario_4":3}
domain_name = {"blocksworld":"Blocksworld", "gripper":"Gripper", "peg":"Peg", "spanner":"Spanner", "sokoban":"Sokoban", "zenotravel":"Zenotravel", "logistics":"Logistics", "NegElevator":"Elevator", "NegVisitAll":"Visit All", "floortile":"Floortile"}
LEG_SCENARIO = {"Scenario_1":"Complete observation and no noise","Scenario_2":"Partial observation and no noise","Scenario_3":"Complete observation and high level of noise","Scenario_4":"Partial observation and high level of noise"}

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
"blocksworld":{"Distance":40, "Accuracy":100, "FScore":100, "Time":50},\
"gripper":{"Distance":50, "Accuracy":100, "FScore":100, "Time":50},\
"hanoi":{"Distance":40, "Accuracy":100, "FScore":100, "Time":500},\
"n-puzzle":{"Distance":50, "Accuracy":100, "FScore":100, "Time":500},\
"peg":{"Distance":30, "Accuracy":100, "FScore":100, "Time":500},\
"parking":{"Distance":50, "Accuracy":100, "FScore":100, "Time":500},\
"zenotravel":{"Distance":30, "Accuracy":100, "FScore":100, "Time":500},\
"NegVisitAll":{"Distance":30, "Accuracy":100, "FScore":100, "Time":500},\
"NegElevator":{"Distance":20, "Accuracy":100, "FScore":100, "Time":50},\
"spanner":{"Distance":30, "Accuracy":100, "FScore":100, "Time":20},\
"logistics":{"Distance":40, "Accuracy":100, "FScore":100, "Time":500},\
"floortile":{"Distance":20, "Accuracy":100, "FScore":100, "Time":500},\
"sokoban":{"Distance":30, "Accuracy":100, "FScore":100, "Time":500}
}

label = {"Distance":"$E_{\\sigma}$", "Accuracy":"Accuracy", "FScore":"FScore", "Time":"Time"}
NOISE_ = [0,1,5,20]
OBS_ = [100,80,60,40,20]
fluent = [100,80,60,40,20]
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

def generate_tex_group(data, domain, metric):
    res = ""
    res = "%s\n%s" % (res, "\\documentclass[border=1pt]{standalone}")
    res = "%s\n%s" % (res, "\\usepackage{pgfplots}\n\\usepgfplotslibrary{groupplots}\n\\usetikzlibrary{matrix,positioning}")
    res = "%s\n%s" % (res, "\\pgfplotsset{/pgfplots/group/.cd, horizontal sep=2cm, vertical sep=2cm}")
    res = "%s\n%s" % (res, "\\begin{document}")
    res = "%s\n%s" % (res, "\\begin{tikzpicture}")
    res = "%s\n%s" % (res, "\\begin{groupplot}[group style={group name=my plots, group size=4 by 5,}]")

    for f in (100,80,60,40,20):
        for noise in [0,1,5,20]:
            if f == 100:
                res = "%s\n%s%d%s%d%s%s%s" % (res, "\\nextgroupplot[title=\\LARGE Noise: ",noise, "\\%,ymin=0,ymax=",ymax[domain][metric],", xlabel={Size},ylabel={", metric, " $(\\%)$}]")
            else:
                res = "%s\n%s%d%s%s%s" % (res, "\\nextgroupplot[ymin=0,ymax=",ymax[domain][metric],", xlabel={Size},ylabel={", metric, " $(\\%)$ }]")
            res = "%s\n\t%s" % (res, "\\addplot[color=blue,mark=o] coordinates {")
            for size in data[domain].keys():
                res = "%s\n\t(%d,%f)" % (res, size, data[domain][size][f][noise]["Base"][metric]["mean"])
            res = "%s\n%s%s" % (res, "};","\\label{plots:Base}")
            res = "%s\n\t%s" % (res, "\\addplot[color=red,mark=square] coordinates {")
            for size in data[domain].keys():
                res = "%s\n\t(%d,%f)" % (res, size, data[domain][size][f][noise]["Base+PS"][metric]["mean"])
            res = "%s\n%s%s" % (res, "};","\\label{plots:Base+PS}")
            res = "%s\n\t%s" % (res, "\\addplot[color=green,mark=triangle] coordinates {")
            for size in data[domain].keys():
                res = "%s\n\t(%d,%f)" % (res, size, data[domain][size][f][noise]["Base+PS+Tabu"][metric]["mean"])
            res = "%s\n%s%s" % (res, "};","\\label{plots:Base+PS+Tabu}")
    res =  "%s\n%s" % (res, "\\end{groupplot}\n")
    for i in (0,1,2,3,4):
        res =  "%s\n%s%d%s" % (res, "\\node[below = 1.2cm of my plots c1r",(i+1),".south] (tmp1) {};\n")
        res =  "%s\n%s%d%s" % (res, "\\node[below = 1.2cm of my plots c4r",(i+1),".south] (tmp2) {};")
        res =  "%s\n%s%d%s" % (res, "\\node (b) at ($(tmp1)!0.5!(tmp2)$) {\LARGE Observability: ",OBS_[i],"\\%};\n")
    res =  "%s\n%s" % (res, "\\node (tmp) at ($(tmp1)!0.5!(tmp2)$) {};")
    res =  "%s\n%s\n%s" % (res, "\\matrix[matrix of nodes,anchor=south,draw,inner sep=0.2em,draw]\nat ([yshift=-8.5ex]tmp)",\
                "{\\ref{plots:Base}&\\LARGE Base&[45pt]\\ref{plots:Base+PS}&\LARGE Base + PS &[45pt]\\ref{plots:Base+PS+Tabu}&\LARGE Base + PS + Tabu\\\\};")
    res =  "%s\n%s%s" % (res, "\\end{tikzpicture}\n", "\\end{document}\n")
    return res

data_all_domains = {}
for d in domain:
    data = {}
    for algo in ["Base", "Base+PS", "Base+PS+Tabu"]:
        file_name = "../../../csv/strips/%s_%s.csv" % (d, algo)
        with open(file_name, newline='') as csvfile:
            spamreader = csv.reader(csvfile, delimiter=' ', quotechar='|')
            size = -1
            dist = []
            fscore = []
            acc = []
            time = []
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
                        if(not algo in data[size][obs][noise]):
                            data[size][obs][noise][algo]={}
                        data[size][obs][noise][algo]["Distance"]={"mean":mean(dist), "std":std(dist)}
                        data[size][obs][noise][algo]["FScore"]={"mean":mean(fscore), "std":std(fscore)}
                        data[size][obs][noise][algo]["Accuracy"]={"mean":mean(acc), "std":std(acc)}
                        data[size][obs][noise][algo]["Time"]={"mean":mean(time), "std":std(time)}
                    size = float(row[IDX_METRIC["Size"]])
                    obs = float(row[IDX_METRIC["Obs"]])
                    noise = float(row[IDX_METRIC["Noise"]])
                    dist = []
                    fscore = []
                    acc = []
                    time = []
                dist.append(float(row[IDX_METRIC["Distance"]]))
                acc.append(float(row[IDX_METRIC["Accuracy"]]))
                fscore.append(float(row[IDX_METRIC["FScore"]]))
                time.append(float(row[IDX_METRIC["Time"]]))
            if(not size in data):
                data[size] = {}
            if(not obs in data[size]):
                data[size][obs]={}
            if(not noise in data[size][obs]):
                data[size][obs][noise]={}
            if(not algo in data[size][obs][noise]):
                data[size][obs][noise][algo]={}
            data[size][obs][noise][algo]["Distance"]={"mean":mean(dist), "std":std(dist)}
            data[size][obs][noise][algo]["FScore"]={"mean":mean(fscore), "std":std(fscore)}
            data[size][obs][noise][algo]["Accuracy"]={"mean":mean(acc), "std":std(acc)}
            data[size][obs][noise][algo]["Time"]={"mean":mean(time), "std":std(time)}
    data_all_domains[d] = data

for d in domain:
    for met in ["Distance","FScore","Time", "Accuracy"]:
        str_tex = generate_tex_group(data_all_domains, d, met)
        file_name = "%s_%s.tex" % (met, d)
        file_ = open(file_name , "w")
        file_.write(str_tex)
        file_.close()
