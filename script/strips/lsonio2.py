#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Apr 21 19:13:32 2020
"""

import os, sys, shutil

#Constants
N_RUN=1
RUNNABLE="java -jar build/amlsi.jar"
PROPERTIES="properties_files/config.properties"
#Fonction for one experiment
def run(domain, domain_name, initial_state, rep, size=5, T=5):
    command = ("%s -lsonio -min %d -max %d -T %d -properties %s -d %s -o %s -n %s -i %s -r %d") % \
                (RUNNABLE,\
                 size,
                 size,
                 T,
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

    #os.mkdir(rep)
    for [size, T] in [[10,20], [20,20],[20,40],[30,40]]:
        rep2 = "%s/Size_%d_%d" % (rep , size, T)
        if(os.path.isdir(rep2)):
            shutil.rmtree(rep2)
        os.mkdir(rep2)
        for i in [1,2,3]:
            rep_is = "%s/IS%d" % (rep2, i)
            os.mkdir(rep_is)
            initial_state= "%s/initial_states/initial%d.pddl" % (domain_rep, i)
            run(reference, domain_name, initial_state, rep_is, size, T)

def main():
    test(sys.argv[1], sys.argv[2], sys.argv[3])

if __name__ == "__main__":
    main()
