(define (problem is)
(:domain robot)
(:objects
loc0 loc1 loc2 loc3 - room
d0 d1 d2 d3 d4 d5 d6 - roomdoor
obj0 obj1 obj2 obj3 obj4 obj5 obj6 obj7 obj8 obj9 obj10 obj11 obj12 obj13 obj14 - package
)
(:init
(armempty)
(rloc loc2)

(closed d0)
(door loc1 loc0 d0)
(door loc0 loc1 d0) 
(closed d1)
(door loc0 loc1 d1)
(door loc1 loc0 d1) 
(closed d2)
(door loc1 loc3 d2)
(door loc3 loc1 d2) 
(closed d3)
(door loc0 loc3 d3)
(door loc3 loc0 d3) 
(closed d4)
(door loc3 loc0 d4)
(door loc0 loc3 d4) 
(closed d5)
(door loc1 loc0 d5)
(door loc0 loc1 d5) 
(closed d6)
(door loc3 loc2 d6)
(door loc2 loc3 d6) 

(in obj0 loc1) 
(in obj1 loc3) 
(in obj2 loc0) 
(in obj3 loc0) 
(in obj4 loc1) 
(in obj5 loc2) 
(in obj6 loc2) 
(in obj7 loc0) 
(in obj8 loc3) 
(in obj9 loc2) 
(in obj10 loc1) 
(in obj11 loc1) 
(in obj12 loc2) 
(in obj13 loc1) 
(in obj14 loc2) 

(goal_in obj0 loc0) 
(goal_in obj1 loc0) 
(goal_in obj2 loc2) 
(goal_in obj3 loc1) 
(goal_in obj4 loc2) 
(goal_in obj5 loc3) 
(goal_in obj6 loc3) 
(goal_in obj7 loc3) 
(goal_in obj8 loc1) 
(goal_in obj9 loc0) 
(goal_in obj10 loc0) 
(goal_in obj11 loc0) 
(goal_in obj12 loc1) 
(goal_in obj13 loc3) 
(goal_in obj14 loc0) 

)
(:goal (and
(in obj0 loc0) 
(in obj1 loc0) 
(in obj2 loc2) 
(in obj3 loc1) 
(in obj4 loc2) 
(in obj5 loc3) 
(in obj6 loc3) 
(in obj7 loc3) 
(in obj8 loc1) 
(in obj9 loc0) 
(in obj10 loc0) 
(in obj11 loc0) 
(in obj12 loc1) 
(in obj13 loc3) 
(in obj14 loc0) 
))
)