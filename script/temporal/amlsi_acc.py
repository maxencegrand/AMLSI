#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os, sys, shutil

#Constants
PROG="./planner/bin/plan.py"

#Try to plan
def plan(domain, problem, type="seq"):
    command = ("%s %s %s %s --time 600 --validate --no-iterated > /dev/null 2> /dev/null") % (PROG, type, domain,problem)
    # print("test")
    # print(type)
    code = os.system(command)
    # print(command)
    return code
    # return 0

#Test for one domain
def test_domain(reference, domain_to_test, domain, rep_plan, type="seq"):
    for problem in range(1,21):

        # command = ("%s %s pddl/temporal/%s/instances/instance-%d.pddl ") % (PROG,domain_to_test,domain,problem)
        #print(command)
        pfile = ("benchmarks/temporal/%s/instances/instance-%d.pddl" % (domain,problem))
        code_return = plan(domain_to_test, pfile, type)
        # code_return = 0
        if(code_return == 0):
            code_return = os.system("cat tmp_sas_plan > %s/plan_%d 2> /dev/null " % (rep_plan, problem))
            if(code_return == 0):
                continue
            else:
                os.system("echo \"NO SOLUTION\" > %s/plan_%d " % (rep_plan, problem))
        else:
            os.system("echo \"NO SOLUTION\" > %s/plan_%d " % (rep_plan, problem))
        # return


#Test all domains
def test(domain, base_dir, type="seq"):
    domain_rep="benchmarks/temporal/%s" % domain
    reference="%s/domain.pddl" % domain_rep
    learnt_rep="%s/%s" % (base_dir, domain)
    learnt_rep0 = "%s" % learnt_rep
    for scenar in ["2OP", "3OP"]:
        rep_plan = "%s/%s/%s/plans" % (base_dir, domain, scenar)
        os.system("rm -rf %s" % rep_plan)
        os.mkdir(rep_plan)
        learnt_repX = "%s/%s" % (learnt_rep0, scenar)
        for [size, T] in [[5,10],[10,10],[10,20],[20,20],[20,40],[30,40]]:
            learnt_rep1 = "%s/Size_%d_%d" % (learnt_repX, size, T)
            for is_ in [1,2,3]:
                rep_plan1 = "%s/IS%d/plans" % (learnt_rep1, is_)
                if(os.path.isdir(rep_plan1)):
                    shutil.rmtree(rep_plan1)
                # print(rep_plan1)
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
                            to_test = "%s/IS%d/domain.%d.%d.%d.pddl" % (learnt_rep1, is_, partial, noise, run)
                            # print(domain_to_test)
                            test_domain(reference, to_test, domain, rep_plan4, type)
                            # return

def main():
    test(sys.argv[1], sys.argv[2], sys.argv[3])

if __name__ == "__main__":
    main()
