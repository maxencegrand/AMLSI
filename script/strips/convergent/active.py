#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Apr 21 19:13:32 2020
"""

import os, sys, shutil

#Constants
SCENAR_ARGUMENTS = {"min":"-strategy min",\
		            "max":"-strategy max"}

N_RUN=5
RUNNABLE="java -jar build/amlsi.jar -min 10 -max 30 -active -T 100"
PROPERTIES="properties_files/config.properties"
#Fonction for one experiment
def run(domain, domain_name, initial_state, scenar, rep, strat):
    command = ("%s -Delta %d -t RPNIPC -properties %s -d %s -o %s -n %s -i %s -r %d %s") % \
                (RUNNABLE,\
                 int(scenar),\
                 PROPERTIES,\
                 rep,\
                 domain,\
                 domain_name,\
                 initial_state,\
                 N_RUN,
                 SCENAR_ARGUMENTS[strat])
    # print(scenar)
    # command = ("%s -Delta 1 -t RPNIR -properties %s -d %s -o %s -n %s -i %s -r %d") % \
    #             (RUNNABLE,\
    #              PROPERTIES,\
    #              rep,\
    #              domain,\
    #              domain_name,\
    #              initial_state,\
    #              N_RUN)
    print(command)
    os.system("%s > %s/log.txt" % (command, rep))


#Test all scenari
def test(domain, domain_name, base_dir):
    domain_rep="benchmarks/strips/%s" % domain
    reference="%s/domain.pddl" % domain_rep
    learnt_rep="%s/%s" % (base_dir, domain)

    if(os.path.isdir(learnt_rep)):
        shutil.rmtree(learnt_rep)
    os.mkdir(learnt_rep)

    for strat in ["min", "max"]:
        rep_conv = "%s/%s" % (learnt_rep, strat)
        os.mkdir(rep_conv)
        for delta in [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]:
            rep = "%s/Delta%d" % (rep_conv , delta)
            os.mkdir(rep)
            for i in [1,2,3]:
                rep_is = "%s/IS%d" % (rep, i)
                os.mkdir(rep_is)
                initial_state= "%s/initial_states/initial%d.pddl" % (domain_rep, i)
                run(reference, domain_name, initial_state, delta, rep_is,strat)

def main():
    print("Begin test")
    test(sys.argv[1], sys.argv[2], sys.argv[3])

if __name__ == "__main__":
    main()
