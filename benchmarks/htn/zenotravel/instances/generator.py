#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os, sys, shutil
from random import randrange
from random import shuffle

class Problem():
    def __init__(self, nbAirport, nbCity, nbPassenger):
        self.nbAirport = nbAirport
        self.nbCity = nbCity
        self.nbPassenger = nbPassenger
        self.allCity = []
        for b in range(1, self.nbCity+1):
            self.allCity.append(b)
        self.allBoard = []
        self.allDepart = []
        self.allInitA = []
        for p in range(self.nbPassenger):
            self.allBoard.append(self.allCity[randrange(len(self.allCity))])
            self.allDepart.append(self.allCity[randrange(len(self.allCity))])
        for p in range(self.nbAirport):
            self.allInitA.append(self.allCity[randrange(len(self.allCity))])

    def hddl(self, id_problem):
        res = "(define (problem p%d)" % id_problem
        res = "%s\n(:domain zenotravel)" % res
        res = "%s\n(:objects\n%s\n)" % (res, self.obj())
        res = "%s\n(:htn :parameters() :ordered-subtasks(and%s\n))" % (res, self.htn())
        res = "%s\n(:init%s\n)" % (res, self.init())
        res = "%s\n(:goal (and%s\n))" % (res, self.goal())
        res = "%s\n)" % (res)
        return res;

    def pddl(self, id_problem):
        res = "(define (problem p%d)" % id_problem
        res = "%s\n(:domain zenotravel)" % res
        res = "%s\n(:objects\n%s\n)" % (res, self.obj())
        res = "%s\n(:init%s\n)" % (res, self.init())
        res = "%s\n(:goal (and%s\n))" % (res, self.goal())
        res = "%s\n)" % (res)
        return res;

    def obj(self):
        res = " f0 f1 f2 - FLevel\n"
        for b in range(1, self.nbAirport+1):
            res = "%s a%d" % (res, b)
        res = "%s - Aircraft\n" % (res)
        for b in range(1, self.nbCity+1):
            res = "%s c%d" % (res, b)
        res = "%s - City\n" % (res)
        for b in range(1, self.nbPassenger+1):
            res = "%s p%d" % (res, b)
        res = "%s - Person" % (res)
        return res

    def init(self):
        res="\n(min f0) (max f2)"
        for f in range(2):
            res = "%s\n(next f%d f%d)" % (res, f, f+1)
            res = "%s\n(not-min f%d)" % (res, f+1)
        for b in range(1, self.nbAirport+1):
            res = "%s\n(fuel-level a%d f2) (at a%d c%d)" % (res, b, b, self.allInitA[b-1])
        for b in range(1, self.nbPassenger+1):
            res = "%s\n(at p%d c%d)" % (res, b, self.allBoard[b-1])
        return res

    def htn(self):
        res=""
        n = 1
        for b in range(1, self.nbPassenger+1):
            res = "%s\n(task%d (transport-person p%d c%d))" % (res, n, b, self.allDepart[b-1])
            n+=1
        for b in range(1, self.nbAirport+1):
            res = "%s\n(task%d (transport-aircraft a%d c%d))" % (res, n, b, self.allInitA[b-1])
            n+=1
        return res

    def goal(self):
        res=""
        for b in range(1, self.nbAirport+1):
            res = "%s\n(at a%d c%d)" % (res, b, self.allInitA[b-1])
        for b in range(1, self.nbPassenger+1):
            res = "%s\n(at p%d c%d)" % (res, b, self.allDepart[b-1])
        return res

def main():
    print("generation")
    nbAirport = randrange(3)+1
    nbCity = randrange(10)+1
    nbPassenger = randrange(30)+1
    problem = Problem(nbAirport, nbCity, nbPassenger)
    if(int(sys.argv[1]) < 10):
        name = "instance-0%d" % int(sys.argv[1])
    else:
        name = "instance-%d" % int(sys.argv[1])
    f = open("%s.pddl" % name, "w")
    f.write(problem.pddl(int(sys.argv[1])))
    f.close()
    f = open("%s.hddl" % name, "w")
    f.write(problem.hddl(int(sys.argv[1])))
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
