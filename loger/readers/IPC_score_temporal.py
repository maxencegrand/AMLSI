#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
@author: Grand Maxence
"""
import os, sys, shutil

def test_plan(file_plan, problem, domain):
    if(os.path.getsize(file_plan) == 0):
        return [0,0]
    f = open(file_plan, 'r')
    for line in f:
        if(line == "NO SOLUTION" or line==""):
            return [0,0]
        else:
            break

    command = "./validate -v -t 0.0001 %s %s %s > /dev/null 2> /dev/null" % (domain, problem, file_plan)
    print(command)
    if(os.system(command) == 0):
        return [1,1]
    else:
        return [1,0]

def accuracy_domain(domain, rep_problem, rep_plans, n):
    s = 0
    a = 0
    for i in range(n):
        nn = i+1
        plan_file = "%s/plan_%d" % (rep_plans, nn)
        problem_file = "%s/instance-%d.pddl" % (rep_problem, nn)
        score_ = test_plan(plan_file, problem_file, domain)
        s += score_[0]
        a += score_[1]
    return [s/n*100, a/n*100]

def cost(file_plan):
    if(os.path.getsize(file_plan) == 0):
        return 0
    f = open(file_plan, 'r')
    first=True
    max_value=0
    for line in f:
        line = line[0:len(line)-1]
        if(first):
            if(line == "NO SOLUTION" or line==""):
                return 0
            first=False
        else:
            if(line != ""):
                start = float(line.split(":")[0])
                line=line.split(":")[1]
                tab=line.split(" ")
                line=tab[len(tab)-1]
                duration=float(line[1:len(line)-1])
                value=start+duration
                if(max_value < value):
                    max_value=value
    return max_value

def score(plan_ref, plan):
    c = cost(plan_ref)
    cBis = cost(plan)
    if(cBis == 0):
        return 0
    return c / cBis

def score_domain(rep_ref, rep, n=20):
    score_ = 0
    for i in range(n):
        nn = i+1
        plan_file_ref = "%s/plan_%d" % (rep_ref, nn)
        plan_file = "%s/plan_%d" % (rep, nn)
        score_ += score(plan_file_ref, plan_file)
    return score_


# print(accuracy_domain("pddl/temporal/peg/domain.pddl", "pddl/temporal/peg/instances", "/home/maxence/Documents/amlsi/temporal2/peg/AMLSI/plans/IS3/RUN3/OBS100", 20))
print(accuracy_domain("pddl/temporal/match/domain.pddl", "pddl/temporal/match/instances", "/home/maxence/Documents/amlsi/temporal2/match/AMLSI/plans/IS3/RUN0/OBS100", 20))
