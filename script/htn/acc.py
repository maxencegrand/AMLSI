"""
Created on Tue Apr 21 19:13:32 2020
"""

import os, sys, shutil


N_RUN=5
RUNNABLE="java -jar build/amlsi.jar"
PROPERTIES="properties_files/config.properties"
#Fonction for one experiment

#Test all scenari
def test(domain, base_dir):
    domain_rep="benchmarks/htn/%s" % domain
    instance="%s/instances/" % domain_rep
    learnt_rep="%s/%s" % (base_dir, domain)
    learnt_rep0 = "%s" % learnt_rep

    for scenar in ["ActionModel", "Method", "Method+ActionModel"]:
        learnt_repX = "%s/%s" % (learnt_rep0 , scenar)
        for size,T in [[5,10],[10,10],[10,20],[20,20],[20,40],[30,40]]:
        #for size in [[30,20]]:
            learnt_rep1 = "%s/size_%d_%d" % (learnt_repX, size, T)
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
                            domain = "%s/IS%d/domain.%d.%d.%d.hddl" % (learnt_rep1, is_, fluent, noise, run)
                            command = "java -jar build/amlsi.jar -htn -planner -o %s -i %s -d %s > /dev/null 2> /dev/null" % (domain, instance, rep3)
                            # print(command)
                            os.system("%s" % (command))


def main():
    print("Begin test")
    test(sys.argv[1], sys.argv[2])

if __name__ == "__main__":
    main()
