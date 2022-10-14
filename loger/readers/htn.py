#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Feb 19 10:06:42 2020

@author: maxence
"""

#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
@author: Grand Maxence
"""

from tabulate import tabulate
import matplotlib.pyplot as plt
import numpy as np
import matplotlib.gridspec as gridspec
from math import sqrt
import csv
import os, sys, shutil

T=30

def count_length(plan):
    count = 0
    with open(plan, 'r') as fp:
        for line in fp:
            count += 1
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

def read_results(domain, method="amlsi",directory="./"):
    reader= Reader(domain, method, directory)
    print("Overall results")
    reader.print_domain_results()
    print("\nStandard deviation between configurations")
    reader.print_conf_std()
    print("\nResults for each initial states")
    reader.print_domain_results_split()
    print("\nStandard deviation between initial states")
    reader.print_std_split()
    print("\nAutomaton results")
    reader.print_automaton_results()
    print("\nBenchmark info")
    reader.print_benchmark_info()


def test_plan(file_plan, file_plan_ref, problem, domain):
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
    # print(command)
    ret = os.system(command)
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
        plan_file = "%s/plan%d.txt" % (rep_plans, nn)
        plan_file_ref = "%s/plans/plan%d.txt" % (rep_problem, nn)
        if (nn < 10):
            problem_file = "%s/instance-0%d.pddl" % (rep_problem, nn)
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
        for [size, T] in [[5,10],[10,10],[10,20],[20,20],[20,40],[30,40]]:
        # for [size, T] in [[30,40]]:
            met = "%s/size_%d_%d" % (method, size, T)
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
                    res[f][n][i]["Time"] = res[f][n][i]["Time Methods"]+res[f][n][i]["Time Automaton"]
            for f in (20,40,60,80,100):
                for n in (0,1,5,20):
                    for i in [1,2,3]:
                        line=[]
                        line.append(size*T)
                        line.append(f)
                        line.append(n)
                        line.append(i)
                        for m in ["Fscore", "Accuracy", "IPC","Time"]:
                            line.append(res[f][n][i][m])
                        spamwriter.writerow(line)

class Reader:
    def __init__(self, domain, method="amlsi",directory="./"):

        self.domain = domain
        self.method = method
        self.directory = directory
        self.fluent = {-1:20, 20:40, 40:60, 60:80, 80:100, 100:20}
        self.noise = {-1:0, 0:1, 1:5, 5:20, 20:0}
        self.metrics_domain = ["Recall",
                               "Correction",
                               "Accuracy",
                               "Fscore",
                               "Time Automaton",
                               "Time Methods",
                               "Time",
                               "IPC"]

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
    def read_log_domain(self):
        #Read log files
        for initial in [1,2,3]:
            file_ = ("%s/%s/%s/IS%d/log.txt" %
                     (self.directory, self.domain, self.method, initial))
            raws = extract_line(file_)
            p = False
            fluent = -1
            noise= -1
            it=1
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
                        it=0
                    elif line[:20] == "Time Domain Learning":
                        # print(fluent)
                        # print(noise)
                        self.data_domain["Time Methods"][fluent][noise][initial].append(float(line[23:]))
                        continue
                    elif line[:14] == "Time Automaton":
                        self.data_domain["Time Automaton"][fluent][noise][initial].append(float(line[17:]))
                        continue
                    elif line[:6] == "FScore":
                        self.data_domain["Fscore"][fluent][noise][initial].append(float(line[9:])*100)
                        continue
                    else:
                        continue

    def read_acc(self):
        for initial in [1,2,3]:
            for run in [0,1,2,3,4,5,6,7,8,9]:
                for obs in [20,40,60,80,100]:
                    for noise in [0,1,5,20]:
                        file_ = ("%s/%s/%s/IS%d/plans/NOISE%d/OBS%d/REP%d" %
                                 (self.directory, self.domain, self.method, initial, noise, obs, run))
                        domain_file = ("./benchmarks/htn/%s/domain.pddl" % self.domain)
                        rep_instances = ("./benchmarks/htn/%s/instances" % self.domain)
                        accSolve = accuracy_domain(domain_file, rep_instances, file_, 20)
                        # self.data_domain["solve"][obs][noise].append(accSolve[0])
                        self.data_domain["Accuracy"][obs][noise][initial].append(accSolve[1])
                        self.data_domain["IPC"][obs][noise][initial].append(accSolve[2])
