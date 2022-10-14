import matplotlib.pyplot as plt
from tabulate import tabulate
import numpy as np
import matplotlib.gridspec as gridspec
import os, sys, shutil
import csv

def nextIteration(it):
    if(it==50):
        return 1
    else:
        return it+1

def test_plan(file_plan, problem, domain):
    if(os.path.getsize(file_plan) == 0):
        return [0,0]
    f = open(file_plan, 'r')
    for line in f:
        if(line[0:len(line)-1] == "NO SOLUTION" or line==""):
            return [0,0]
        else:
            break

    command = "../validate -v -t 0.0001 %s %s %s > /dev/null 2> /dev/null" % (domain, problem, file_plan)
    ret =os.system(command)
    if(ret == 0):
        return [1,1]
    else:
        return [1,0]

def accuracy_domain(domain, rep_problem, rep_plans, n):
    s = 0
    a = 0
    instances = []
    for i in range(n):
        nn = i+1
        plan_file = "%s/plan_%d" % (rep_plans, nn)
        problem_file = "%s/instance-%d.pddl" % (rep_problem, nn)
        score_ = test_plan(plan_file, problem_file, domain)
        s += score_[0]
        a += score_[1]
    return [s/n*100, a/n*100]


def extract_line(file_):
    raws = []
    f = open(file_, "r")
    for line in f:
        if not line == "\n":
            raws.append(line[:-1])
    return raws

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

def plot(subp, x, y, title, m="x-",c="b"):
    leg, = subp.plot(x, y, m, c=c)
    subp.set_xlabel("Delta")
    subp.set_ylabel("Score(%)")
    subp.title.set_text(title)
    return leg

class Reader:
    def __init__(self, domain, method="amlsi_convergent2",directory="./"):
        self.domain = domain
        self.method = method
        self.directory = directory
        self.fluent = {-1:25, 25:100, 100:25}
        self.noise = {-1:0, 0:20,20:0}
        self.iterations = []
        for i in range(50):
            self.iterations.append(i+1)
        self.metrics_domain = ["fscore",
                               "fscore2",
                               "precondition",
                               "effect",
                               "distance",
                               "solve",
                               "accuracy",
                               "ipc",
                               "iteration",
                               "time"]

        self.metrics_automaton = ["fscore",
                                  "recall",
                                  "precision",
                                  "states",
                                  "observations",
                                  "transitions",
                                  "compression"]

        self.benchmark_info = ["I+","I-","i+","i-",
                               "E+","E-","e+","e-"]

        self.data_benchmark = {}
        for info in self.benchmark_info:
            self.data_benchmark[info] = []
        self.clear()

    def clear(self):
        self.data_automaton = {}
        for metric in self.metrics_automaton:
            self.data_automaton[metric] = []
        self.data_domain = {}
        for metric in self.metrics_domain:
            self.data_domain[metric] = {}
            for it in self.iterations:
                self.data_domain[metric][it]={}
                for f in {25,100}:
                    self.data_domain[metric][it][f] = {}
                    for n in {0, 20}:
                        self.data_domain[metric][it][f][n] = []

    def read_log_domain_planning(self, Delta=10):
        for initial in [1,2,3]:
            for run in [0,1,2,3,4]:
                for obs in [25,100]:
                    for noise in [0,20]:
                        file_ = ("%s/%s/%s/plans/IS%d/RUN%d/OBS%d/NOISE%d" %
                                 (self.directory, self.domain, self.method, initial, run, obs, noise))
                        domain_file = ("../benchmarks/strips/%s/domain.pddl" % self.domain)
                        rep_instances = ("../benchmarks/strips/%s/instances" % self.domain)
                        accSolve = accuracy_domain(domain_file, rep_instances, file_, 20)
                        self.data_domain["solve"][obs][noise].append(accSolve[0])
                        self.data_domain["accuracy"][obs][noise].append(accSolve[1])



    def read_log_domain(self, Delta=10):
        #Read log files
        for initial in [1,2,3]:
            file_ = ("%s/%s/%s/IS%d/log.txt" % \
                     (self.directory, self.domain,self.method,initial))
            raws = extract_line(file_)
            p = False
            fluent = -1
            noise= -1
            it=0
            for line in raws:
                if line == "############################################":
                    p = True
                if p:
                    # print("%d %d %d" % (it,fluent,noise))
                    if line == "############################################":
                        noise = -1
                        continue
                    elif line[:4] == "### ":
                        fluent = self.fluent[fluent]
                        noise = -1
                    elif line[:4] == "*** ":
                        noise = self.noise[noise]
                    elif line[:9] == "Iteration":
                        it = nextIteration(it)
                        continue
                    elif line[:6] == "FSCORE":
                        self.data_domain["fscore"][it][fluent][noise].append(line[9:])
                        continue
                    elif line[:4] == "Time":
                        self.data_domain["time"][it][fluent][noise].append(line[7:])
                        continue
                    elif line[:20] == "Syntactical distance":
                        self.data_domain["distance"][it][fluent][noise].append(line[23:])
                        continue
                    else:
                        continue

    def read_log_automaton(self):
        for initial in [1,2,3]:
            file_ = ("%s%s/%s/IS%d/log.txt" %
                     (self.directory, self.domain, self.method,initial))
            raws = extract_line(file_)
            for line in raws:
                #print(self.data_automaton)
                if line[:16] == "#Observed states":
                    self.data_automaton["observations"].append(line[18:])
                elif line[:7] == "#States":
                    self.data_automaton["states"].append(line[9:])
                elif line[:12] == "#Transitions":
                    self.data_automaton["transitions"].append(line[14:])
                elif line[:17] == "Compression level":
                    self.data_automaton["compression"].append(line[19:])
                elif line[:6] == "Recall":
                    self.data_automaton["recall"].append(line[9:])
                elif line[:9] == "Precision":
                    self.data_automaton["precision"].append(line[12:])
                elif line[:16] == "Fscore automaton":
                    self.data_automaton["fscore"].append(line[17:])
                else:
                    continue
    def read_log_benchmark(self):
        for initial in [1,2,3]:
            file_ = ("%s%s/%s/IS%d/log.txt" %
                     (self.directory, self.domain, self.method,initial))
            raws = extract_line(file_)
            for line in raws:
                #print(self.data_automaton)
                if line[:7] == "I+ size":
                    self.data_benchmark["I+"].append(line[9:])
                elif line[:7] == "I- size":
                    self.data_benchmark["I-"].append(line[9:])
                elif line[:12] == "x+ mean size":
                    self.data_benchmark["i+"].append(line[14:])
                elif line[:12] == "x- mean size":
                    self.data_benchmark["i-"].append(line[14:])
                elif line[:7] == "E+ size":
                    self.data_benchmark["E+"].append(line[9:])
                elif line[:7] == "E- size":
                    self.data_benchmark["E-"].append(line[9:])
                elif line[:12] == "e+ mean size":
                    self.data_benchmark["e+"].append(line[14:])
                elif line[:12] == "e- mean size":
                    self.data_benchmark["e-"].append(line[14:])
                else:
                    continue

    def print_benchmark_info(self):
        self.read_log_benchmark()
        res = []
        res_ = []
        for info in self.benchmark_info:
            res_.append(mean(self.data_benchmark[info]))

        res.append(res_)
        print(tabulate(res, headers=self.benchmark_info))

    def print_automaton_results(self):
        self.read_log_automaton()
        heads = ["fscore","R","P","#States","#Obs","#Transitions","Compression level"]
        res = []
        res_ = []
        for metrics in self.metrics_automaton:
            if(metrics == "states" or metrics == "observations" or metrics == "transitions" or metrics == "compression"):
                res_.append(mean(self.data_automaton[metrics]))
            else:
                res_.append(mean(self.data_automaton[metrics])*100)
        res.append(res_)
        print(tabulate(res, headers=heads))

    def print_domain_results(self, Plan=True):
        self.clear()
        self.read_log_domain()
        if(Plan):
            self.read_log_domain_planning()

def main():
    domain=sys.argv[1]
    rep=sys.argv[2]
    readers=[]
    readers.append(Reader(domain, method="Size1", directory=rep))
    readers.append(Reader(domain, method="Size2", directory=rep))
    readers.append(Reader(domain, method="Size5", directory=rep))
    readers.append(Reader(domain, method="Size10", directory=rep))
    for reader in readers:
        reader.print_domain_results(Plan=False)
    # print(reader.data_domain["distance"])
    f = open(sys.argv[3], 'w')
    writer = csv.writer(f)
    for it in reader.iterations:
        row = []
        for reader in readers:
            for metric in ["distance", "fscore", "time", "accuracy"]:
                for noise in [0,20]:
                    for fluent in [100,25]:
                        if metric == "accuracy" or metric == "time":
                            row.append(mean(reader.data_domain[metric][it][fluent][noise]))
                        else:
                            row.append(mean(reader.data_domain[metric][it][fluent][noise])*100)
        writer.writerow(row)
    f.close()

if __name__ == "__main__":
    main()
