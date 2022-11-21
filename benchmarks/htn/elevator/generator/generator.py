import numpy as np
import sys

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

    def aboveDirect(self):
        self.aboveDirect = ""
        for i in range(self.nbFloor-1):
            self.aboveDirect += ("\t(above-direct floor_%d floor_%d)\n" % (i, (i+1)))
        self.aboveDirect += ("\t(lift-at floor_%d)\n" % np.random.randint(low=0, high=self.nbFloor))

    def above(self):
        self.above = ""
        for i in range(self.nbFloor-1):
            for j in range(i+1, self.nbFloor):
                self.above += ("\t(above floor_%d floor_%d)\n" % (i, (j)))

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

    def goal(self):
        self.goal = ""
        for i in range(self.nbPassenger):
            self.goal += ("\t(served passenger_%d)\n" % i)

    def generate(self):
        self.objects()
        self.aboveDirect()
        self.above()
        self.originAndDestin()
        self.goal()
        self.generatePDDL()
        self.generateHDDL()

    def htn(self):
        self.htn = ""
        for i in range(self.nbPassenger):
            self.htn += ("\t(task%d (deliver-person passenger_%d))\n" % ((i+1),i))

    def generatePDDL(self):
        file_content = "(define (problem is)\n(:domain miconic)\n(:objects\n"
        file_content += self.objects
        file_content += ")\n"
        file_content += "(:init\n"
        file_content += self.aboveDirect
        file_content += self.above
        file_content += self.origin
        file_content += self.destin
        file_content += ")\n"
        file_content += "(:goal (and\n"
        file_content += self.goal
        file_content += "))\n)"
        f = open(("%s.pddl" % self.file_), "w")
        f.write(file_content)
        f.close()

    def generateHDDL(self):
        self.htn()
        file_content = "(define (problem is)\n(:domain miconic)\n(:objects\n"
        file_content += self.objects
        file_content += ")\n"
        file_content += "(:htn :parameters() :ordered-subtasks(and\n"
        file_content += self.htn
        file_content += "))\n"
        file_content += "(:init\n"
        file_content += self.aboveDirect
        file_content += self.above
        file_content += self.origin
        file_content += self.destin
        file_content += ")\n"
        file_content += "(:goal (and\n"
        file_content += self.goal
        file_content += "))\n)"
        f = open(("%s.hddl" % self.file_), "w")
        f.write(file_content)
        f.close()


if __name__ == '__main__':
    nbPassenger = int(sys.argv[1])
    nbFloor = int(sys.argv[2])
    filename = sys.argv[3]
    elevator = Elevator(nbFloor=nbFloor, nbPassenger=nbPassenger, file_=filename)
    elevator.generate()
