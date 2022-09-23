#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os, sys, shutil

#Constants
PROG="./fast_downward/fast-downward.py"
ARG="--search-time-limit 120 --search-memory-limit 1200"
ARG2="--evaluator \"hff=ff()\" --search \"lazy_greedy([hff], preferred=[hff])\" > /dev/null 2> /dev/null"
#Try to plan
def plan(domain, problem):

    command = ("%s --plan-file plan.txt %s %s %s %s") % \
        (PROG,\
         ARG,\
         domain,\
         problem,\
         ARG2)
    code = os.system(command)
    return code

#Test for one domain
def test_domain(reference, domain_to_test, domain, rep_plan, ref_max):
    for problem in range(1,21):

            # command = ("%s %s benchmarks/strips/%s/instances/instance-%d.pddl ") % (PROG,domain_to_test,domain,problem)
            #print(command)
        pfile = ("benchmarks/strips/%s/instances/instance-%d.pddl" % (domain,problem))
        code_return = plan(domain_to_test, pfile)
        if(code_return == 0):
            code_return = os.system("cat plan.txt > %s/plan_%d 2> /dev/null " % (rep_plan, problem))
            if(code_return == 0):
                continue
            else:
                os.system("echo \"NO SOLUTION\" > %s/plan_%d " % (rep_plan, problem))
        else:
            os.system("echo \"NO SOLUTION\" > %s/plan_%d " % (rep_plan, problem))
            # return


#Test all domains
def test(domain, base_dir):
    domain_rep="benchmarks/strips/%s" % domain
    reference="%s/domain.pddl" % domain_rep

    # for scenar in [8,9,10,11,12,13,14,15]:
    for scenar in [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]:
        rep_plan = "%s/%s/Criterion_%d/plans" % (base_dir, domain, scenar)
        os.system("rm -rf %s" % rep_plan)
        os.mkdir(rep_plan)
        for initial in [1,2,3]:
            rep_plan1 = "%s/IS%d" % (rep_plan, initial)
            os.mkdir(rep_plan1)
            for run in [0,1,2,3,4,5,6,7,8,9]:
                rep_plan2 = "%s/RUN%d" % (rep_plan1, run)
                os.mkdir(rep_plan2)
                for partial in [100, 80, 60, 40, 20]:
                    rep_plan3 = "%s/OBS%d" % (rep_plan2, partial)
                    os.mkdir(rep_plan3)
                    for noise in [0, 1, 5, 20]:
                        rep_plan4 = "%s/NOISE%d" % (rep_plan3, noise)
                        os.mkdir(rep_plan4)
                        domain_to_test=("%s/%s/Criterion_%s/IS%d/domain.%d.%d.%d.pddl " % (base_dir, domain, scenar, initial, partial, noise, run))
                        print(domain_to_test)
                        test_domain(reference, domain_to_test, domain, rep_plan4, ("%s/ref_max" % domain_rep))

def main():
    test(sys.argv[1], sys.argv[2])

if __name__ == "__main__":
    main()
