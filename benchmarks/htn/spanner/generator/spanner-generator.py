#!/usr/bin/env python
import sys, random
import datetime

#*****************#
# Functions declarations
#*****************#
def get_objects():
   str_objects="\n"

   # -- man
   str_objects=str_objects+"     bob - man\n"

   # -- spanner
   for i in range(1, num_spanners+1):
      str_objects=str_objects+" spanner"+str(i)
   str_objects=str_objects+" - spanner\n    "

   # -- nuts
   for i in range(1,num_nuts+1):
      str_objects=str_objects+" nut"+str(i)
   str_objects=str_objects+" - nut\n    "

   # -- locations
   for i in range(1,num_locations+1):
      str_objects=str_objects+" location"+str(i)
   str_objects=str_objects+" - location\n    "

   str_objects=str_objects+" shed gate - location\n    "

   return(str_objects)
   


#*****************#
def get_init():
   str_init="\n"
   str_init=str_init+"    (at bob shed)\n"

   for i in range(1,num_spanners+1):      
      str_init=str_init+"    (at spanner"+str(i)+" location"+ str(random.randint(1,num_locations))+")\n"
      str_init=str_init+"    (useable spanner"+str(i)+")\n"

   for i in range(1,num_nuts+1):
      str_init=str_init+"    (loose nut"+str(i)+")\n"	
      str_init=str_init+"    (at nut"+str(i)+" gate)\n"

   str_init=str_init+"    (link shed location1)\n"
   str_init=str_init+"    (link location"+str(num_locations)+" gate)\n"

   for i in range(1,num_locations):
      str_init=str_init+"    (link location"+str(i)+" location"+str(i+1)+")\n"
      
   return(str_init)

#*****************#
def get_goals():
   str_goal=""
   str_goal=str_goal+"\n  (and\n"

   for i in range(1,num_nuts+1):
      str_goal=str_goal+ "   (tightened nut"+str(i)+")\n"
            
   str_goal=str_goal+")"
   return(str_goal)
#*****************#

#*****************#
# MAIN
#*****************#
# Reading the command line arguments
try:
   num_spanners = int(sys.argv[1])
   num_nuts =  int(sys.argv[2])
   num_locations = int(sys.argv[3])
   name = sys.argv[4]
   print name
except:
   print "Usage: " +sys.argv[0] + " <num_spanners> <num_nuts> <num_locations>"
   print "  num_spanners (min 1)"
   print "  num_nuts (min 1 and <= num_spanners)"
   print "  num_locations (min 1)"

   sys.exit(1)

seed = datetime.datetime.now().strftime( "%s")
random.seed( seed )
filename= sys.argv[5]
with open( filename, "w") as output:
   output.write("(define (problem "+name+")")
   output.write(" (:domain spanner)")
   output.write(" (:objects "+ get_objects()+")")
   output.write(" (:init " + get_init()+")")
   output.write(" (:goal"+ get_goals()+"))")

print "wrote domain to:" +  filename
sys.exit(0)
