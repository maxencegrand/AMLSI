#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os, sys, shutil


def getObjects(nb_ball):
    res = "rooma roomb - room"
    res = "%s\n%s" % (res, "right left - gripper")
    balls = ""
    for i in range(nb_ball):
        ball = i+1
        balls = "%sball%d " % (balls, ball)
    res = "%s\n%s- ball" % (res, balls)
    return res

def getHtn(nb_ball):
    nb_task = int(nb_ball/2)
    res=""
    id_ball = 1
    for task_id in range(nb_task):
        res = "%s\n(task_%d (move_two_balls ball%d ball%d roomb))" % (res, task_id, id_ball, (id_ball+1))
        id_ball += 2
    return res

def getInit(nb_ball):
    res="\n%s" % "(at-robby rooma)"
    res="%s\n(free right)" % res
    res="%s\n(free left)" % res
    for i in range(nb_ball):
        res="%s\n(at ball%d rooma)" % (res, (i+1))
    return res

def getGoal(nb_ball):
    res=""
    for i in range(nb_ball):
        res="%s\n(at ball%d roomb)" % (res, (i+1))
    return res

def generate(nb_ball, id_problem):
    res = "(define (problem p%d)" % id_problem
    res = "%s\n(:domain gripper)" % res
    res = "%s\n(:objects\n%s\n)" % (res, getObjects(nb_ball))
    res = "%s\n(:htn :parameters() :ordered-subtasks(and%s\n))" % (res, getHtn(nb_ball))
    res = "%s\n(:init%s\n)" % (res, getInit(nb_ball))
    res = "%s\n(:goal (and%s\n))" % (res, getGoal(nb_ball))
    res = "%s\n)" % (res)
    return res

def main():
    print(generate(int(sys.argv[1])*int(sys.argv[2]),int(sys.argv[2])))

if __name__ == "__main__":
    main()
