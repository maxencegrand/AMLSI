#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Apr 21 19:13:32 2020
"""
import os, sys, shutil

#Constants
SCENAR_ARGUMENTS = {"2OP":"",\
                    "3OP":"-lgp"}
GRAMMAR_TYPE = {"WITH_PC":"-t RPNIPC", "WITHOUT_PC":"-t RPNI"}

N_RUN=10
RUNNABLE="java -jar build/amlsi.jar"
PROPERTIES="properties_files/config.properties"
#Fonction for one experiment
def run(domain, domain_name, initial_state, rep, scenar, size, T):
    command = ("%s %s %s %s -properties %s -min %d -max %d  -T %d -d %s -o %s -n %s -i %s -r %d") % \
                (RUNNABLE,\
                 "-temporal",\
                 SCENAR_ARGUMENTS[scenar],
                 "-t RPNIPC",\
                 PROPERTIES,\
                 size,\
                 size,\
                 T,\
                 rep,\
                 domain,\
                 domain_name,\
                 initial_state,\
                 N_RUN)
    print(command)
    os.system("%s > %s/log.txt" % (command, rep))


#Test all scenari
def test(domain, domain_name, base_dir):
    domain_rep="benchmarks/temporal/%s" % domain
    reference="%s/domain.pddl" % domain_rep
    learnt_rep="%s/%s" % (base_dir, domain)

    if(os.path.isdir(learnt_rep)):
        shutil.rmtree(learnt_rep)
    os.mkdir(learnt_rep)

    rep = "%s" % (learnt_rep)
    for scenar in ["2OP", "3OP"]:
        rep_scenar = "%s/%s" % (rep, scenar)
        os.mkdir(rep_scenar)
        #for [size, T] in [[5,10]]:
        for [size, T] in [[5,10],[10,10],[10,20],[20,20],[20,40],[30,40]]:
            # rep2 = "%s/%s" % (rep_scenar , size)
            rep2 = "%s/Size_%d_%d" % (rep_scenar , size, T)
            if(os.path.isdir(rep2)):
                shutil.rmtree(rep2)
            os.mkdir(rep2)
            for i in [1,2,3]:
                rep_is = "%s/IS%d" % (rep2, i)
                os.mkdir(rep_is)
                initial_state= "%s/initial_states/initial%d.pddl" % (domain_rep, i)
                run(reference, domain_name, initial_state, rep_is, scenar, size, T)

def main():
    print("Begin test")
    test(sys.argv[1], sys.argv[2], sys.argv[3])

if __name__ == "__main__":
    main()
