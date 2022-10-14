#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Feb  3 09:41:53 2020

@author: maxence
"""

from math import sqrt

#
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