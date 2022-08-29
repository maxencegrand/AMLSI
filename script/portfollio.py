#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os, sys, shutil

PROG="./planner/bin/plan.py"
VAL="./VAL/validate -t 0.0001 -v"

def plan(domain, problem, type):
    command = ("%s %s %s %s --time 600 --validate --no-iterated > /dev/null 2> /dev/null") % (PROG, type, domain,problem)
    code = os.system(command)
    print(command)
    #print(type)
    #print(code)
    #sys.exit(1)
    return code

def search(domain, problem):
    ## Sequential search
    if(plan(domain, problem, "seq") == -1):
        return True
    else:
        if(plan(domain, problem, "she") == 0):
            return True
        else:
            if(plan(domain, problem, "tempo-1") == -1):
                return True
            else:
                if(plan(domain, problem, "tempo-2") == -1):
                    return True
                else:
                    if(plan(domain, problem, "tempo-3") == -1):
                        return True
                    else:
                        if(plan(domain, problem, "stp-1") == 0):
                            return True
                        else:
                            if(plan(domain, problem, "stp-2") == 0):
                                return True
                            else:
                                if(plan(domain, problem,  "stp-3") == 0):
                                    return True
                                else:
                                    return False

def main():
    if(search(sys.argv[1], sys.argv[2])):
        sys.exit(0)
    else:
        sys.exit(1)

if __name__ == "__main__":
    main()
