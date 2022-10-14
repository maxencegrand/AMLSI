import csv
from math import sqrt

IDX_METRIC = {"Size":0, "Obs":1, "Noise":2, "IS":3, "FScore":4,"Accuracy":5,"IPC":6,"Time":7}
IDX_SCENARIO = {"Scenario_1":0,"Scenario_2":1,"Scenario_3":2,"Scenario_4":3}
LEG_SCENARIO = {"Scenario_1":"Complete observation and no noise","Scenario_2":"Partial observation and no noise","Scenario_3":"Complete observation and high level of noise","Scenario_4":"Partial observation and high level of noise"}

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

ymax = {\
"blocksworld":{"Distance":40, "Accuracy":100, "FScore":100, "Time":50, "IPC":20},\
"gripper":{"Distance":50, "Accuracy":100, "FScore":100, "Time":50, "IPC":20},\
"zenotravel":{"Distance":30, "Accuracy":100, "FScore":100, "Time":500, "IPC":20},\
"transport":{"Distance":20, "Accuracy":100, "FScore":100, "Time":500, "IPC":21},\
"childsnack":{"Distance":30, "Accuracy":100, "FScore":100, "Time":500, "IPC":20},\
"allDomains":{"Distance":30, "Accuracy":100, "FScore":100, "Time":500, "IPC":20}
}


label = {"Distance":"$E_{\\sigma}$", "Accuracy":"Accuracy", "FScore":"FScore", "Time":"Time", "IPC":"IPC"}
NOISE_ = [0,1,5,20]
fluent = [20,40,60,80,100]

def generate_tex_simple(data, domain, metric):
    res = ""
    res = "%s\n%s" % (res, "\\documentclass[border=1pt]{standalone}")
    res = "%s\n%s" % (res, "\\usepackage{pgfplots}\n\\usepgfplotslibrary{groupplots}\n\\usetikzlibrary{matrix,positioning}")
    res = "%s\n%s" % (res, "\\pgfplotsset{/pgfplots/group/.cd, horizontal sep=2cm, vertical sep=2cm}")
    res = "%s\n%s" % (res, "\\begin{document}")
    res = "%s\n%s" % (res, "\\begin{tikzpicture}")
    res = "%s\n%s" % (res, "\\begin{groupplot}[group style={group name=my plots, group size=4 by 5,}]")
    for f in (20,40,60,80,100):
        for noise in [0,1,5,20]:
            if f == 20:
                # print(ymax.keys())
                # print(ymax[domain])
                res = "%s\n%s%d%s%d%s%s%s" % (res, "\\nextgroupplot[title=\\LARGE Noise: ",noise, "\\%,ymin=0,ymax=",ymax[domain][metric],", xlabel={Size},ylabel={", metric, " $(\\%)$}]")
            else:
                res = "%s\n%s%d%s%s%s" % (res, "\\nextgroupplot[ymin=0,ymax=",ymax[domain][metric],", xlabel={Size},ylabel={", metric, " $(\\%)$ }]")
            res = "%s\n\t%s" % (res, "\\addplot[color=blue,mark=o] coordinates {")
            for size in data[domain].keys():
                res = "%s\n\t(%d,%f)" % (res, size, data[domain][size][f][noise]["Method"][metric]["mean"])
            res = "%s\n%s%s" % (res, "};","\\label{plots:Base}")
            res = "%s\n\t%s" % (res, "\\addplot[color=red,mark=square] coordinates {")
            for size in data[domain].keys():
                res = "%s\n\t(%d,%f)" % (res, size, data[domain][size][f][noise]["ActionModel"][metric]["mean"])
            res = "%s\n%s%s" % (res, "};","\\label{plots:Base+PS}")
            res = "%s\n\t%s" % (res, "\\addplot[color=green,mark=triangle] coordinates {")
            for size in data[domain].keys():
                res = "%s\n\t(%d,%f)" % (res, size, data[domain][size][f][noise]["Method+ActionModel"][metric]["mean"])
            res = "%s\n%s%s" % (res, "};","\\label{plots:Base+PS+Tabu}")
    res =  "%s\n%s" % (res, "\\end{groupplot}\n")
    for i in (0,1,2,3,4):
        res =  "%s\n%s%d%s" % (res, "\\node[below = 1.2cm of my plots c1r",(i+1),".south] (tmp1) {};\n")
        res =  "%s\n%s%d%s" % (res, "\\node[below = 1.2cm of my plots c4r",(i+1),".south] (tmp2) {};")
        res =  "%s\n%s%d%s" % (res, "\\node (b) at ($(tmp1)!0.5!(tmp2)$) {\LARGE Observability: ",fluent[i],"\\%};\n")
    res =  "%s\n%s" % (res, "\\node (tmp) at ($(tmp1)!0.5!(tmp2)$) {};")
    res =  "%s\n%s\n%s" % (res, "\\matrix[matrix of nodes,anchor=south,draw,inner sep=0.2em,draw]\nat ([yshift=-8.5ex]tmp)",\
                "{\\ref{plots:Base}&\\LARGE Only Methods&[45pt]\\ref{plots:Base+PS}&\LARGE Only Action Model &[45pt]\\ref{plots:Base+PS+Tabu}&\LARGE Action Model + Methods\\\\};")
    res =  "%s\n%s%s" % (res, "\\end{tikzpicture}\n", "\\end{document}\n")
    return res

def generate_tex_group(data, domains, metric):
    res = ""
    res = "%s\n%s" % (res, "\\documentclass[border=1pt]{standalone}")
    res = "%s\n%s" % (res, "\\usepackage{pgfplots}\n\\usepgfplotslibrary{groupplots}\n\\usetikzlibrary{matrix,positioning}")
    res = "%s\n%s" % (res, "\\pgfplotsset{/pgfplots/group/.cd, horizontal sep=2cm, vertical sep=2cm}")
    res = "%s\n%s" % (res, "\\begin{document}")
    res = "%s\n%s" % (res, "\\begin{tikzpicture}")
    res = "%s\n%s" % (res, "\\begin{groupplot}[group style={group name=my plots, group size=4 by 5,}]")
    first=True
    for d in domains:
        sc=1
        for noise in [0,20]:
            for f in (100,20):
                if first:
                    res = "%s\n%s%d%s%d%s%s%s" % (res, "\\nextgroupplot[title=\\LARGE Scenario: ",sc, ",ymin=0,ymax=",ymax[d][metric],", xlabel={Size},ylabel={", metric, " $(\\%)$}]")
                else:
                    res = "%s\n%s%d%s%s%s" % (res, "\\nextgroupplot[ymin=0,ymax=",ymax[d][metric],", xlabel={Size},ylabel={", metric, " $(\\%)$ }]")
                sc+=1
                res = "%s\n\t%s" % (res, "\\addplot[color=blue,mark=o] coordinates {")
                for size in data[d].keys():
                    res = "%s\n\t(%d,%f)" % (res, size, data[d][size][f][noise]["Method"][metric]["mean"])
                res = "%s\n%s%s" % (res, "};","\\label{plots:Base}")
                res = "%s\n\t%s" % (res, "\\addplot[color=red,mark=square] coordinates {")
                for size in data[d].keys():
                    res = "%s\n\t(%d,%f)" % (res, size, data[d][size][f][noise]["ActionModel"][metric]["mean"])
                res = "%s\n%s%s" % (res, "};","\\label{plots:Base+PS}")
                res = "%s\n\t%s" % (res, "\\addplot[color=green,mark=triangle] coordinates {")
                for size in data[d].keys():
                    res = "%s\n\t(%d,%f)" % (res, size, data[d][size][f][noise]["Method+ActionModel"][metric]["mean"])
                res = "%s\n%s%s" % (res, "};","\\label{plots:Base+PS+Tabu}")
        first=False
    res =  "%s\n%s" % (res, "\\end{groupplot}\n")
    for i in (0,1,2,3,4):
        res =  "%s\n%s%d%s" % (res, "\\node[below = 1.2cm of my plots c1r",(i+1),".south] (tmp1) {};\n")
        res =  "%s\n%s%d%s" % (res, "\\node[below = 1.2cm of my plots c4r",(i+1),".south] (tmp2) {};")
        res =  "%s\n%s%s%s" % (res, "\\node (b) at ($(tmp1)!0.5!(tmp2)$) {\LARGE ",domain_name[domain[i]],"};\n")
    res =  "%s\n%s" % (res, "\\node (tmp) at ($(tmp1)!0.5!(tmp2)$) {};")
    res =  "%s\n%s\n%s" % (res, "\\matrix[matrix of nodes,anchor=south,draw,inner sep=0.2em,draw]\nat ([yshift=-8.5ex]tmp)",\
                "{\\ref{plots:Base}&\\LARGE Only Methods&[45pt]\\ref{plots:Base+PS}&\LARGE Only Action Model &[45pt]\\ref{plots:Base+PS+Tabu}&\LARGE Action Model + Methods\\\\};")
    res =  "%s\n%s%s" % (res, "\\end{tikzpicture}\n", "\\end{document}\n")
    return res

domain = [\
"blocksworld",\
"gripper",\
"transport",\
"zenotravel",\
"childsnack"]

data_all_domains = {}
label = {"FScore":"FScore", "Accuracy":"Accuracy", "Correction":"Correction", "Precision":"Precision", "Time":"Time"}
domain_name = {"blocksworld":"Blocksworld", "gripper":"Gripper", "zenotravel":"Zenotravel", "transport":"Transport", "childsnack":"Childsnack", "allDomains":'allDomains'}
for d in domain:
    data = {}
    for algo in ["ActionModel", "Method", "Method+ActionModel"]:
        file_name = "../../csv/htn/%s_%s.csv" % (d, algo)
        with open(file_name, newline='') as csvfile:
            spamreader = csv.reader(csvfile, delimiter=' ', quotechar='|')
            size = -1
            dist = []
            fscore = []
            acc = []
            time = []
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
                        if(not algo in data[size][obs][noise]):
                            data[size][obs][noise][algo]={}
                        data[size][obs][noise][algo]["FScore"]={"mean":mean(fscore), "std":std(fscore)}
                        data[size][obs][noise][algo]["Accuracy"]={"mean":mean(acc), "std":std(acc)}
                        data[size][obs][noise][algo]["Time"]={"mean":mean(time), "std":std(time)}
                        data[size][obs][noise][algo]["IPC"]={"mean":mean(ipc), "std":std(ipc)}
                    size = float(row[IDX_METRIC["Size"]])
                    obs = float(row[IDX_METRIC["Obs"]])
                    noise = float(row[IDX_METRIC["Noise"]])
                    dist = []
                    fscore = []
                    acc = []
                    time = []
                acc.append(float(row[IDX_METRIC["Accuracy"]]))
                fscore.append(float(row[IDX_METRIC["FScore"]]))
                time.append(float(row[IDX_METRIC["Time"]]))
                ipc.append(float(row[IDX_METRIC["IPC"]]))
            if(not size in data):
                data[size] = {}
            if(not obs in data[size]):
                data[size][obs]={}
            if(not noise in data[size][obs]):
                data[size][obs][noise]={}
            if(not algo in data[size][obs][noise]):
                data[size][obs][noise][algo]={}
            data[size][obs][noise][algo]["FScore"]={"mean":mean(fscore), "std":std(fscore)}
            data[size][obs][noise][algo]["Accuracy"]={"mean":mean(acc), "std":std(acc)}
            data[size][obs][noise][algo]["Time"]={"mean":mean(time), "std":std(time)}
            data[size][obs][noise][algo]["IPC"]={"mean":mean(ipc), "std":std(ipc)}
    data_all_domains[d]=data

#Overall results
print(data_all_domains.keys())
for d in data_all_domains.keys():
    for met in ["FScore", "Accuracy", "IPC"]:
        str_tex = generate_tex_simple(data_all_domains, d, met)
        file_name = "%s_%s.tex" % (met, d)
        file_ = open(file_name , "w")
        file_.write(str_tex)
        file_.close()
