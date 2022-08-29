"""
Created on Tue Apr 21 19:13:32 2020
"""

import os, sys, shutil


N_RUN=5
RUNNABLE="java -jar build/amlsi.jar"
PROPERTIES="properties_files/config.properties"
#Fonction for one experiment
def run(domain, domain_name, size, initial_state, rep, tabu=False):
    command = ("%s -t RPNIPC -min %d -max %d -T %d -htn -onlyMethod -recomposition heuristic -properties %s -d %s -o %s -n %s -i %s -r %d") % \
                (RUNNABLE,\
                 size[1],\
                 size[1],\
                 size[0],\
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

    if(os.path.isdir(learnt_rep)):
        shutil.rmtree(learnt_rep)
    os.mkdir(learnt_rep)

    learnt_rep0 = "%s" % learnt_rep

    for size in [[1,5],[2,5],[3,5],[5,5],[5,10],[10,10],[15,10],[20,15], [30,20]]:
        learnt_rep1 = "%s/size_%d_%d" % (learnt_rep0, size[0], size[1])
        os.mkdir(learnt_rep1)
        for is_ in [1,2,3]:
            learnt_rep2 = "%s/IS%d" % (learnt_rep1, is_)
            os.mkdir(learnt_rep2)
            initial_state= "%s/initial_states/initial%d.hddl" % (domain_rep, is_)
            run(reference, domain_name, size, initial_state, learnt_rep2)


def main():
    print("Begin test")
    test(sys.argv[1], sys.argv[2], sys.argv[3])

if __name__ == "__main__":
    main()
