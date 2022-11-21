import numpy as np
import sys

class Graph:
    def __init__(self, locs):
        self.matrix = {}
        self.root = locs[0]
        for loc1 in locs:
            self.matrix[loc1] = {}
            for loc2 in locs:
                self.matrix[loc1][loc2] = False

    def isConnexe(self):
        reachable = {}
        for loc in self.matrix.keys():
            reachable[loc]=False
        closed = []
        opened= []
        opened.append(self.root)
        while(len(opened) > 0):
            loc = opened.pop()
            closed.append(loc)
            for loc2 in self.matrix[loc].keys():
                if (self.matrix[loc][loc2]):
                    reachable[loc2]=True
                    if(not loc2 in closed):
                        opened.append(loc2)
        for loc in reachable.keys():
            if(not reachable[loc]):
                return False
        return True

    def addEdge(self, loc1, loc2):
        self.matrix[loc1][loc2] = True
        self.matrix[loc2][loc1] = True

class Room:
    def __init__(self, id):
        self.id = id

    def __str__(self):
        return "loc%d" % self.id

    def robotLocation(self):
        return "(rloc %s)" % str(self)

class Obj:
    def __init__(self, id, f, t):
        self.id = id
        self.f = f
        self.t = t

    def __str__(self):
        return "obj%d" % self.id

    def getOrigin(self):
        return "(in %s %s)" % (str(self), str(self.f))

    def getGoal(self):
        return "(goal_in %s %s)" % (str(self), str(self.t))

    def getGoalLocation(self):
        return "(in %s %s)" % (str(self), str(self.t))

    def getTask(self):
        return "(achieve-goals %s)" % (str(self))

class Door:
    def __init__(self, id, loc1, loc2):
        self.id = id
        self.loc1 = loc1
        self.loc2 = loc2

    def __str__(self):
        return "d%d" % self.id

    def getInit(self):
        return "(closed %s)\n(door %s %s %s)\n(door %s %s %s)" % \
                        (str(self), str(self.loc1), str(self.loc2), str(self),\
                        str(self.loc2), str(self.loc1), str(self))

class Robot:
    def __init__(self, nbRoom=0, nbObj=0, file_="instance"):
        self.rooms = []
        self.objects = []
        self.doors = []
        self.file_ = file_

        # Create room
        for i in range(nbRoom):
            self.rooms.append(Room(i))

        # Create Graph and doors (to do)
        g = Graph(self.rooms)
        nb_door = 0
        while(not g.isConnexe()):
            loc1 = np.random.randint(nbRoom)
            loc2 = loc1
            while(loc1 == loc2):
                loc2 = np.random.randint(nbRoom)
            g.addEdge(self.rooms[loc1], self.rooms[loc2])
            self.doors.append(Door(nb_door, self.rooms[loc1], self.rooms[loc2]))
            nb_door += 1

        # Create packages
        for i in range(nbObj):
            loc1 = np.random.randint(nbRoom)
            loc2 = loc1
            while(loc1 == loc2):
                loc2 = np.random.randint(nbRoom)
            self.objects.append(Obj(i, self.rooms[loc1], self.rooms[loc2]))

        self.initialLocation = self.rooms[np.random.randint(nbRoom)]

    def preambule(self):
        self.preambule = ""
        for room in self.rooms:
            self.preambule += ("%s " % str(room))
        self.preambule += "- room\n"
        for door in self.doors:
            self.preambule += ("%s " % str(door))
        self.preambule += "- roomdoor\n"
        for obj in self.objects:
            self.preambule += ("%s " % str(obj))
        self.preambule += "- package\n"

    def initial(self):
        self.initial = "(armempty)\n"
        # Robot location
        self.initial += "%s\n\n" % self.initialLocation.robotLocation()
        # Doors
        for door in self.doors:
            self.initial += "%s \n" % door.getInit()
        self.initial += "\n"
        # Object Location
        for obj in self.objects:
            self.initial += "%s \n" % obj.getOrigin()
        self.initial += "\n"
        # Goal Location
        for obj in self.objects:
            self.initial += "%s \n" % obj.getGoal()
        self.initial += "\n"

    def htn(self):
        self.htn = ""
        task_id = 0
        for obj in self.objects:
            task_id += 1
            self.htn += "(task%d %s) \n" % (task_id, obj.getTask())

    def goal(self):
        self.goal = ""
        for obj in self.objects:
            self.goal += "%s \n" % obj.getGoalLocation()

    def generate(self):
        self.preambule()
        self.initial()
        self.htn()
        self.goal()
        self.generatePDDL()
        self.generateHDDL()

    def generatePDDL(self):
        file_content = "(define (problem is)\n(:domain robot)\n(:objects\n"
        file_content += self.preambule
        file_content += ")\n"
        file_content += "(:init\n"
        file_content += self.initial
        file_content += ")\n"
        file_content += "(:goal (and\n"
        file_content += self.goal
        file_content += "))\n)"
        f = open(("%s.pddl" % self.file_), "w")
        f.write(file_content)
        f.close()

    def generateHDDL(self):
        file_content = "(define (problem is)\n(:domain entertainment)\n(:objects\n"
        file_content += self.preambule
        file_content += ")\n"
        file_content += "(:htn :parameters() :ordered-subtasks(and\n"
        file_content += self.htn
        file_content += "))\n"
        file_content += "(:init\n"
        file_content += self.initial
        file_content += "))\n"
        # file_content += "(:goal (and\n"
        # file_content += self.goal
        # file_content += "))\n)"
        f = open(("%s.hddl" % self.file_), "w")
        f.write(file_content)
        f.close()

if __name__ == '__main__':
    robot = Robot(nbRoom=int(sys.argv[1]), nbObj=int(sys.argv[2]), file_=sys.argv[3])
    robot.generate()
