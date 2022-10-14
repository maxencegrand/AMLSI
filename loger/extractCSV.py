import readers.ablation as ablation
import readers.convergent as convergent
import readers.lsonio as lsonio
import readers.temporal as temp
import readers.htn as htn

print("Extract CSV")

domain_strips = [ \
"blocksworld",
"gripper",
"hanoi",
"n-puzzle",
"zenotravel",
"peg",
"parking",
"sokoban",
"NegElevator",
"NegVisitAll",
"floortile",
"logistics",
"spanner"
]

domain_temporal = [ \
"zenotravel",
"peg",
"sokoban",
"match",
"turn-and-open"
]

domain_htn = [ \
"blocksworld",
 "gripper",
"zenotravel",
"childsnack",
"transport"
]

print("__STRIPS -- Ablation Study")
rep="experiment/strips/ablation/"
for domain in domain_strips:
    print("____%s"%domain)
    for met in ["Base", "Base+PS", "Base+PS+Tabu"]:
        csv= "loger/csv/strips/%s_%s.csv" % (domain,met)
        ablation.getCSV(domain, met, rep, csv, accuracy=True)
print("__STRIPS -- LSONIO")
rep="experiment/strips/lsonio/"
for domain in domain_strips:
   print("____%s"%domain)
   csv= "loger/csv/strips/%s_lsonio.csv" % (domain)
   lsonio.getCSV(domain, ".", rep, csv, accuracy=True)
print("__STRIPS -- Convergent")
rep="experiment/strips/convergent/"
 for domain in domain_strips:
    print("____%s"%domain)
    csv= "loger/csv/strips/%s.csv" % (domain)
    convergent.getCSV(domain, ".", rep, csv, accuracy=True)
print("__Temporal")
rep="experiment/temporal/"
for domain in domain_temporal:
   print("____%s"%domain)
   for met in ["2OP", "3OP"]:
       csv= "loger/csv/temporal/%s_%s.csv" % (domain,met)
       temp.getCSV(domain, met, rep, csv, accuracy=True)
print("__HTN")
rep="experiment/htn/"
for domain in domain_htn:
    print("____%s"%domain)
    for met in ["ActionModel", "Method", "Method+ActionModel"]:
        csv= "loger/csv/htn/%s_%s.csv" % (domain,met)
        htn.getCSV(domain, met, rep, csv, accuracy=True)
