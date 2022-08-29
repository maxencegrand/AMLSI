#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os, sys, shutil
from random import randrange


def getObjects(nb_child, nb_tray, nb_gluten):
    res = ""
    for c in range(nb_child):
        res = "%schild%d " % (res, (c+1))
    res = "%s- child\n" % res
    for c in range(nb_child):
        res = "%ssand%d " % (res, (c+1))
    res = "%s- sandwich\n" % res
    for c in range(nb_child):
        res = "%sbread%d " % (res, (c+1))
    res = "%s- bread-portion\n" % res
    for c in range(nb_child):
        res = "%scontent%d " % (res, (c+1))
    res = "%s- content-portion\n" % res
    for c in range(nb_tray):
        res = "%stray%d " % (res, (c+1))
    res = "%s- tray\n" % res
    for c in range(nb_tray):
        res = "%stable%d " % (res, (c+1))
    res = "%skitchen " % res
    res = "%s- place" % res
    return res

def getHtn(nb_child):
    nb_task = nb_child
    res=""
    for task_id in range(nb_task):
        res = "%s\n(task_%d (serve child%d))" % (res, task_id, (task_id+1))
    return res

def getInit(nb_child, nb_tray, nb_gluten):
    res=""
    for t in range(nb_tray):
        res = "%s\n(at tray%d kitchen)" % (res, (t+1))
    for c in range(nb_child):
        res = "%s\n(at_kitchen_bread bread%d)" % (res, (c+1))
    for c in range(nb_child):
        res = "%s\n(at_kitchen_content content%d)" % (res, (c+1))
    for c in range(nb_child):
        res = "%s\n(not_exist sand%d)" % (res, (c+1))
    for c in range(nb_child):
        res = "%s\n(waiting child%d table%d)" % (res, (c+1), (randrange(nb_tray)+1))
    for g in range(nb_gluten):
        res = "%s\n(no_gluten_bread bread%d)" % (res, (g+1))
    for g in range(nb_gluten):
        res = "%s\n(no_gluten_content content%d)" % (res, (g+1))
    for g in range(nb_gluten):
        res = "%s\n(allergic_gluten child%d)" % (res, (g+1))
    for g in range(nb_child-nb_gluten):
        res = "%s\n(not_allergic_gluten child%d)" % (res, (g+1+nb_gluten))
    return res

def getGoal(nb_child, nb_tray, nb_gluten):
    res=""
    for c in range(nb_child):
        res = "%s\n(served child%d)" % (res, (c+1))
    return res

def generate(nb_child, nb_tray, nb_gluten, id_problem):
    res = "(define (problem p%d)" % id_problem
    res = "%s\n(:domain childsnack)" % res
    res = "%s\n(:objects\n%s\n)" % (res, getObjects(nb_child, nb_tray, nb_gluten))
    res = "%s\n(:htn :parameters() :ordered-subtasks(and%s\n))" % (res, getHtn(nb_child))
    res = "%s\n(:init%s\n)" % (res, getInit(nb_child, nb_tray, nb_gluten))
    res = "%s\n(:goal (and%s\n))" % (res, getGoal(nb_child, nb_tray, nb_gluten))
    res = "%s\n)" % (res)
    return res

def main():
    nb_child = randrange(20)+1
    nb_tray = 1
    nb_gluten = randrange(nb_child)+1
    print(generate(nb_child, nb_tray, nb_gluten, int(sys.argv[1])))

if __name__ == "__main__":
    main()
