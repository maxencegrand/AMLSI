#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os, sys, shutil
from random import randrange
from random import shuffle

class Problem():
    def __init__(self, nbBlocks, nbStacks):
        self.nbBlocks = nbBlocks
        self.allBlocks = []
        self.finalStack = []
        self.initialStack = []
        tmp = []
        for b in range(1, self.nbBlocks+1):
            self.allBlocks.append(b)
            self.finalStack.append(b)
            tmp.append(b)
        shuffle(self.finalStack)
        shuffle(tmp)
        size=0
        idxStacks = []
        idxStacks.append(0)
        while size < nbStacks-1:
            idx = randrange(self.nbBlocks)
            if not idx in idxStacks:
                size += 1
                idxStacks.append(idx)
                idxStacks.sort()
        # idxStacks.append(nbBlocks-1)
        for i in range(len(idxStacks)):
            if (i < (len(idxStacks) - 1)):
                self.initialStack.append(tmp[idxStacks[i]:idxStacks[i+1]])
            else:
                self.initialStack.append(tmp[idxStacks[i]:])
        # print(tmp)
        # print(idxStacks)
        # print(self.initialStack)
        # print(self.finalStack)

    def hddl(self, id_problem):
        res = "(define (problem p%d)" % id_problem
        res = "%s\n(:domain blocks)" % res
        res = "%s\n(:objects\n%s\n)" % (res, self.obj())
        res = "%s\n(:htn :parameters() :ordered-subtasks(and%s\n))" % (res, self.htn())
        res = "%s\n(:init%s\n)" % (res, self.init())
        res = "%s\n(:goal (and%s\n))" % (res, self.goal())
        res = "%s\n)" % (res)
        return res;

    def pddl(self, id_problem):
        res = "(define (problem p%d)" % id_problem
        res = "%s\n(:domain blocks)" % res
        res = "%s\n(:objects\n%s\n)" % (res, self.obj())
        res = "%s\n(:init%s\n)" % (res, self.init())
        res = "%s\n(:goal (and%s\n))" % (res, self.goal())
        res = "%s\n)" % (res)
        return res;

    def obj(self):
        res=""
        for b in self.allBlocks:
            res = "%s b%d" % (res, b)
        res = "%s - block" % (res)
        return res

    def init(self):
        res="\n(handempty)"
        for stack in self.initialStack:
            if len(stack) > 0:
                res = "%s\n(ontable b%d)" % (res, stack[0])
                res = "%s\n(clear b%d)" % (res, stack[len(stack)-1])
                for b in range(1, len(stack)):
                    res = "%s\n(on b%d b%d)" % (res, stack[b], stack[b-1])
        return res

    def htn(self):
        res=""
        n = 1
        for stack in self.initialStack:
            if len(stack) > 0:
                for b in range(len(stack)-1,0,-1):
                    res = "%s\n (task%d (do_on_table b%d))" % (res, n, stack[b])
                    n += 1
        for b in range(1, len(self.finalStack)):
            res = "%s\n (task%d (do_put_on b%d b%d))" % (res, n, self.finalStack[b], self.finalStack[b-1])
            n += 1
        return res

    def goal(self):
        res=""
        for b in range(1, len(self.finalStack)):
            res = "%s\n(on b%d b%d)" % (res, self.finalStack[b], self.finalStack[b-1])
        return res

# def getObjects(nb_child, nb_tray, nb_gluten):
#     res = ""
#     for c in range(nb_child):
#         res = "%schild%d " % (res, (c+1))
#     res = "%s- child\n" % res
#     for c in range(nb_child):
#         res = "%ssand%d " % (res, (c+1))
#     res = "%s- sandwich\n" % res
#     for c in range(nb_child):
#         res = "%sbread%d " % (res, (c+1))
#     res = "%s- bread-portion\n" % res
#     for c in range(nb_child):
#         res = "%scontent%d " % (res, (c+1))
#     res = "%s- content-portion\n" % res
#     for c in range(nb_tray):
#         res = "%stray%d " % (res, (c+1))
#     res = "%s- tray\n" % res
#     for c in range(nb_tray):
#         res = "%stable%d " % (res, (c+1))
#     res = "%skitchen " % res
#     res = "%s- place" % res
#     return res
#
# def getHtn(nb_child):
#     nb_task = nb_child
#     res=""
#     for task_id in range(nb_task):
#         res = "%s\n(task_%d (serve child%d))" % (res, task_id, (task_id+1))
#     return res
#
# def getInit(nb_child, nb_tray, nb_gluten):
#     res=""
#     for t in range(nb_tray):
#         res = "%s\n(at tray%d kitchen)" % (res, (t+1))
#     for c in range(nb_child):
#         res = "%s\n(at_kitchen_bread bread%d)" % (res, (c+1))
#     for c in range(nb_child):
#         res = "%s\n(at_kitchen_content content%d)" % (res, (c+1))
#     for c in range(nb_child):
#         res = "%s\n(not_exist sand%d)" % (res, (c+1))
#     for c in range(nb_child):
#         res = "%s\n(waiting child%d table%d)" % (res, (c+1), (randrange(nb_tray)+1))
#     for g in range(nb_gluten):
#         res = "%s\n(no_gluten_bread bread%d)" % (res, (g+1))
#     for g in range(nb_gluten):
#         res = "%s\n(no_gluten_content content%d)" % (res, (g+1))
#     for g in range(nb_gluten):
#         res = "%s\n(allergic_gluten child%d)" % (res, (g+1))
#     for g in range(nb_child-nb_gluten):
#         res = "%s\n(not_allergic_gluten child%d)" % (res, (g+1+nb_gluten))
#     return res
#
# def getGoal(nb_child, nb_tray, nb_gluten):
#     res=""
#     for c in range(nb_child):
#         res = "%s\n(served child%d)" % (res, (c+1))
#     return res
#
# def generate(nb_child, nb_tray, nb_gluten, id_problem):
#     res = "(define (problem p%d)" % id_problem
#     res = "%s\n(:domain childsnack)" % res
#     res = "%s\n(:objects\n%s\n)" % (res, getObjects(nb_child, nb_tray, nb_gluten))
#     res = "%s\n(:htn :parameters() :ordered-subtasks(and%s\n))" % (res, getHtn(nb_child))
#     res = "%s\n(:init%s\n)" % (res, getInit(nb_child, nb_tray, nb_gluten))
#     res = "%s\n(:goal (and%s\n))" % (res, getGoal(nb_child, nb_tray, nb_gluten))
#     res = "%s\n)" % (res)
#     return res

def main():
    print("generation")
    problem = Problem(int(sys.argv[1]),int(sys.argv[2]))
    if(int(sys.argv[3]) < 10):
        name = "instance-0%d" % int(sys.argv[3])
    else:
        name = "instance-%d" % int(sys.argv[3])
    f = open("%s.pddl" % name, "w")
    f.write(problem.pddl(int(sys.argv[3])))
    f.close()
    f = open("%s.hddl" % name, "w")
    f.write(problem.hddl(int(sys.argv[3])))
    f.close()
    # nb_child = 3 * int(sys.argv[1])
    # if nb_child > 14:
    #     nb_child=14
    # nb_tray = int(nb_child / 3)
    # if(nb_tray < 3):
    #     nb_tray = 3
    # nb_gluten = randrange(nb_child)+1
    # print(generate(nb_child, nb_tray, nb_gluten, int(sys.argv[1])))

if __name__ == "__main__":
    main()
