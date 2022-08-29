import numpy as np

class Elevator:
    def __init__(self, nbFloor=2, nbPassenger=1, file_="instance.pddl"):
        self.nbFloor=nbFloor
        self.nbPassenger=nbPassenger
        self.file_=file_

    def objects(self):
        self.objects = ""
        for i in range(self.nbFloor):
            self.objects += ("floor_%d - floor\n" % (i))
        for i in range(self.nbPassenger):
            self.objects += ("passenger_%d - passenger\n" % (i))

    def above(self):
        self.above = ""
        for i in range(self.nbFloor-1):
            self.above += ("\t(above floor_%d floor_%d)\n" % (i, (i+1)))
        self.above += ("\t(lift-at floor_%d)\n" % np.random.randint(low=0, high=self.nbFloor))

    def originAndDestin(self):
        self.origin = ""
        self.destin = ""
        for i in range(self.nbPassenger):
            origin = np.random.randint(low=0, high=self.nbFloor)
            destin = origin
            while origin == destin:
                destin = np.random.randint(low=0, high=self.nbFloor)
            self.origin += ("\t(origin passenger_%d floor_%d)\n" % (i, origin))
            self.destin += ("\t(destin passenger_%d floor_%d)\n" % (i, destin))
            

    def generate(self):
        #print()
        
        self.objects()
        self.above()
        self.originAndDestin()
        
        file_content = "(define (problem is)\n(:domain miconic)\n(:objects\n"
        file_content += self.objects
        file_content += ")\n"
        file_content += "(:init\n"
        file_content += self.above
        file_content += self.origin
        file_content += self.destin
        file_content += ")\n"
        file_content += "(:goal ())\n)"
        f = open(self.file_, "w")
        f.write(file_content)
        f.close()

for i in range(1,6):
    e = Elevator(nbFloor=i+1, nbPassenger=i, file_=("../initial_states/scale%d.pddl" % i))
    e.generate()
