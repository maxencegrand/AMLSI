"""
Created on Tue Apr 21 19:13:32 2020
"""

import os, sys, shutil


PROG="./fast_downward/fast-downward.py"
ARG="--search-time-limit 120 --search-memory-limit 1200"
ARG2="--evaluator \"hff=ff()\" --search \"lazy_greedy([hff], preferred=[hff])\" > /dev/null 2> /dev/null"
#Fonction for one experiment
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
def test_domain(domain_to_test, domain, rep_plan):
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


#Test all scenari
def test(domain, base_dir):
    domain_rep="benchmarks/strips/%s" % domain
    instance="%s/instances/" % domain_rep
    learnt_rep="%s/%s" % (base_dir, domain)
    learnt_rep0 = "%s" % learnt_rep

    for scenar in ["Base", "Base+PS", "Base+PS+Tabu"]:
        learnt_repX = "%s/%s" % (learnt_rep0, scenar)
        for [size, T] in [[5,10],[10,10],[10,20],[20,20],[20,40],[30,40]]:
            learnt_rep1 = "%s/Size_%d_%d" % (learnt_repX, size, T)
            for is_ in [1,2,3]:
                learnt_rep2 = "%s/IS%d/plans" % (learnt_rep1, is_)
                if(os.path.isdir(learnt_rep2)):
                    shutil.rmtree(learnt_rep2)
                os.mkdir(learnt_rep2)
                for noise in (0,1,5,20):
                    rep1 = "%s/NOISE%d" % (learnt_rep2, noise)
                    os.mkdir(rep1)
                    for fluent in (100,80,60,40,20):
                        rep2 = "%s/OBS%d" % (rep1, fluent)
                        os.mkdir(rep2)
                        # for run in [4]:
                        for run in (0,1,2,3,4,5,6,7,8,9):
                            rep3 = "%s/REP%d" % (rep2, run)
                            os.mkdir(rep3)
                            to_test = "%s/IS%d/domain.%d.%d.%d.pddl" % (learnt_rep1, is_, fluent, noise, run)
                            # print("%s %s" % (to_test, rep3))
                            test_domain(to_test, domain, rep3)


def main():
    test(sys.argv[1], sys.argv[2])
    print("Done %s" % sys.argv[1])

if __name__ == "__main__":
    main()
