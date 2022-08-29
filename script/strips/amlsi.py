#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Apr 21 19:13:32 2020
"""

import os, sys, shutil

#Constants
SCENAR_ARGUMENTS = {"Base":"-withoutTabu -t RPNI -withoutTabu",\
                    "Base+PC":"-withoutTabu -t RPNIPC -withoutTabu",\
                    "Base+PC+Tabu":"-t RPNIPC",\
                    "LSONIO":"-lsonio"}

N_RUN=5
RUNNABLE="java -jar build/amlsi.jar -min 10 -max 30 -T 30"
PROPERTIES="properties_files/config.properties"
#Fonction for one experiment
def run(domain, domain_name, initial_state, rep):
    command = ("%s -t RPNIPC -properties %s -d %s -o %s -n %s -i %s -r %d") % \
                (RUNNABLE,\
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
    domain_rep="benchmarks/strips/%s" % domain
    reference="%s/domain.pddl" % domain_rep
    rep="%s/%s" % (base_dir, domain)

    if(os.path.isdir(rep)):
        shutil.rmtree(rep)
    os.mkdir(rep)

    for i in [1,2,3]:
        rep_is = "%s/IS%d" % (rep, i)
        os.mkdir(rep_is)
        initial_state= "%s/initial_states/initial%d.pddl" % (domain_rep, i)
        run(reference, domain_name, initial_state, rep_is)

def main():
    test(sys.argv[1], sys.argv[2], sys.argv[3])

if __name__ == "__main__":
    main()
