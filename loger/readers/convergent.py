from tabulate import tabulate
import matplotlib.pyplot as plt
import numpy as np
import matplotlib.gridspec as gridspec
from math import sqrt
import csv
import os, sys, shutil

T=30

def count_length(plan):
    with open(plan, 'r') as fp:
        for count, line in enumerate(fp):
            pass
    return count

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

def mean2(l):
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

#
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

def extract_line(file_):
    raws = []
    f = open(file_, "r")
    for line in f:
        if not line == "\n":
            raws.append(line[:-1])
    return raws

def test_plan(file_plan, file_plan_ref, problem, domain):
    # print(file_plan)
    if(not os.path.isfile(file_plan)):
        # print(file_plan)
        return [0,0,0]
    if(os.path.getsize(file_plan) == 0):
        return [0,0,0]
    f = open(file_plan, 'r')
    for line in f:
        if(line[0:len(line)-1] == "NO SOLUTION"):

            return [0,0,0]
        else:
            break

    command = "./validate -v -t 0.0001 %s %s %s > /dev/null 2> /dev/null" % (domain, problem, file_plan)
    ret =os.system(command)
    # print(command)
    # print(ret)
    if(ret == 0):
        return [1,1,float(count_length(file_plan_ref)/count_length(file_plan))]
    else:
        # print(command)
        return [1,0,0]

def accuracy_domain(domain, rep_problem, rep_plans, n):
    s = 0
    a = 0
    ipc=0
    instances = []

    # file_problems = ("%s/problems.txt" % rep_problem)
    # with open(file_problems) as problems:
    #     for problem in problems:
    #         problem = problem[:len(problem)-1]
    #         problem = ("./%s" % problem)
    #         instances.append(problem)
    for i in range(n):
        nn = i+1
        plan_file = "%s/plan_%d" % (rep_plans, nn)
        plan_file_ref = "%s/plans/plan_%d" % (rep_problem, nn)
        if (nn < 10):
            problem_file = "%s/instance-%d.pddl" % (rep_problem, nn)
        else:
            problem_file = "%s/instance-%d.pddl" % (rep_problem, nn)
        score_ = test_plan(plan_file, plan_file_ref, problem_file, domain)
        s += score_[0]
        a += score_[1]
        ipc += score_[2]
    # print(ipc)
    return [s/n*100, a/n*100, ipc]

def getCSV(domain, method, rep="./", filename="file.csv", accuracy=False):
    with open(filename, 'w', newline='') as csvfile:
        spamwriter = csv.writer(csvfile, delimiter=' ', quotechar='|', quoting=csv.QUOTE_MINIMAL)
        # for [size, T] in [[5,2],[5,10],[10,10],[20,20],[30,30]]:
        for c in [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]:
        # for [size, T] in [[30,40]]:
            met = "%s/Criterion_%d" % (method, c)
            reader= Reader(domain, directory=rep, method=met)
            reader.read_log_domain()
            if accuracy:
                reader.read_acc()
            res = {}
            # print(reader.data_domain)
            for f in (20,40,60,80,100):
                res[f]={}
                for n in (0,1,5,20):
                    res[f][n]={}
                    for i in [1,2,3]:
                        res[f][n][i]={}
                        for m in reader.metrics_domain:
                            res[f][n][i][m] = mean(reader.data_domain[m][f][n][i])
                        for m in reader.metrics_dfa:
                            res[f][n][i][m] = mean(reader.data_dfa[m][i])
            for f in (20,40,60,80,100):
                for n in (0,1,5,20):
                    for i in [1,2,3]:
                        line=[]
                        line.append(c)
                        line.append(f)
                        line.append(n)
                        line.append(i)
                        for m in ["Distance", "FScore", "Accuracy", "IPC",  "Time", "iteration"]:
                            line.append(res[f][n][i][m])
                        spamwriter.writerow(line)

class Reader:
    def __init__(self, domain, method="amlsi",directory="./"):

        self.domain = domain
        self.method = method
        self.directory = directory
        self.fluent = {-1:20, 20:40, 40:60, 60:80, 80:100, 100:20}
        self.noise = {-1:0, 0:1, 1:5, 5:20, 20:0}
        self.metrics_domain = ["Distance",
                               "FScore",
                               "Accuracy",
                               "Solve",
                               "Time",
                               "iteration",
                               "IPC"]

        self.metrics_dfa = ["Observations",
                            "FScore DFA",
                            "States",
                            "Transitions",
                            "Time DFA"]

        self.benchmark_info = ["I+","I-","i+","i-",
                               "E+","E-","e+","e-"]

        self.data_domain = {}
        for metric in self.metrics_domain:
            self.data_domain[metric] = []

        self.data_benchmark = {}
        for info in self.benchmark_info:
            self.data_benchmark[info] = []

        self.data_domain = {}
        for metric in self.metrics_domain:
            self.data_domain[metric] = {}
            for f in {20,40,60,80,100}:
                self.data_domain[metric][f] = {}
                for n in {0,1,5,20}:
                    self.data_domain[metric][f][n] = {}
                    for i in [1,2,3]:
                        self.data_domain[metric][f][n][i] = []

        self.data_dfa = {}
        for metric in self.metrics_dfa:
            self.data_dfa[metric] = {}
            for i in [1,2,3]:
                self.data_dfa[metric][i] = []

    def read_log_domain(self):
        #Read log files
        # print("************** %s ***************" % self.method)
        for initial in [1,2,3]:
            file_ = ("%s/%s/%s/IS%d/log.txt" % \
                     (self.directory, self.domain,self.method,initial))
            raws = extract_line(file_)
            p = False
            fluent = -1
            noise= -1
            for line in raws:
                if line == "############################################":
                    p = True
                if p:
                    if line == "############################################":
                        noise = -1
                        continue
                    elif line[:4] == "### ":
                        fluent = self.fluent[fluent]
                        noise = -1
                    elif line[:4] == "*** ":
                        noise = self.noise[noise]
                    elif line[:6] == "FSCORE":
                        self.data_domain["FScore"][fluent][noise][initial].append(100*float(line[9:]))
                        continue
                    elif line[:4] == "Time":
                        self.data_domain["Time"][fluent][noise][initial].append(float(line[7:]))
                        continue
                    elif line[:9] == "Iteration":
                        self.data_domain["iteration"][fluent][noise][initial].append(float(line[9:]))
                        continue
                    elif line[:20] == "Syntactical distance":
                        self.data_domain["Distance"][fluent][noise][initial].append(100*float(line[23:]))
                        continue
                    elif line[:16] == "#Observed states":
                        self.data_dfa["Observations"][initial].append(float(line[19:]))
                        continue
                    elif line[:7] == "#States":
                        self.data_dfa["States"][initial].append(float(line[10:]))
                        continue
                    elif line[:12] == "#Transitions":
                        self.data_dfa["Transitions"][initial].append(float(line[15:]))
                        continue
                    elif line[:16] == "FScore Automaton":
                        self.data_dfa["FScore DFA"][initial].append(100*float(line[18:]))
                        continue
                    else:
                        continue

    def read_acc(self):
        for initial in [1,2,3]:
            for run in [0,1,2,3,4,5,6,7,8,9]:
            # for run in [0,1,2,3,4,5,6,7,8,9]:
                for obs in [20,40,60,80,100]:
                    for noise in [0,1,5,20]:
                        file_ = ("%s/%s/%s/plans/IS%d/RUN%d/OBS%d/NOISE%d" % (self.directory, self.domain, self.method, initial, run, obs, noise))
                        domain_file = ("./benchmarks/strips/%s/domain.pddl" % self.domain)
                        rep_instances = ("./benchmarks/strips/%s/instances" % self.domain)
                        accSolve = accuracy_domain(domain_file, rep_instances, file_, 20)
                        self.data_domain["Solve"][obs][noise][initial].append(accSolve[0])
                        self.data_domain["Accuracy"][obs][noise][initial].append(accSolve[1])
                        self.data_domain["IPC"][obs][noise][initial].append(accSolve[2])
