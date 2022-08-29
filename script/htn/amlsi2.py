"""
Created on Tue Apr 21 19:13:32 2020
"""

import os, sys, shutil

SCENAR_ARGUMENTS = {"Method":"-onlyMethod",\
                    "ActionModel":"-onlyActionModel",\
                    "Method+ActionModel":"-separate"}

N_RUN=10
RUNNABLE="java -jar build/amlsi.jar"
PROPERTIES="properties_files/config.properties"
#Fonction for one experiment
def run(domain, domain_name, initial_state, rep, scenar, tabu=False, size=45, T=5):
    command = ("%s -t RPNIPC -min %d -max %d -T %d %s -htn -recomposition heuristic -properties %s -d %s -o %s -n %s -i %s -r %d") % \
                (RUNNABLE,\
                 size,\
                 size,\
                 T,\
                 SCENAR_ARGUMENTS[scenar],\
                 PROPERTIES,\
                 rep,\
                 domain,\
                 domain_name,\
                 initial_state,\
                 N_RUN)

    print(command)
    os.system("%s > %s/log.txt" % (command, rep))


#Test all scenari
def test(domain, domain_name, base_dir):
    domain_rep="benchmarks/htn/%s" % domain
    reference="%s/domain.hddl" % domain_rep
    learnt_rep="%s/%s" % (base_dir, domain)

    learnt_rep0 = "%s" % learnt_rep

    for scenar in ["Method+ActionModel"]:
        learnt_repX = "%s/%s" % (learnt_rep0 , scenar)
        # for [size, T] in [[10,10]]:
        for [size, T] in [[5,10],[10,10],[10,20],[20,20],[20,40],[30,40]]:
            learnt_rep1 = "%s/size_%d_%d" % (learnt_repX, size, T)
            os.mkdir(learnt_rep1)
            for is_ in [1,2,3]:
                learnt_rep2 = "%s/IS%d" % (learnt_rep1, is_)
                os.mkdir(learnt_rep2)
                initial_state= "%s/initial_states/initial%d.hddl" % (domain_rep, is_)
                run(reference, domain_name, initial_state, learnt_rep2, scenar, size=size, T=T)

def main():
    print("Begin test")
    test(sys.argv[1], sys.argv[2], sys.argv[3])

if __name__ == "__main__":
    main()
