(define (problem is)
(:domain robot)
(:objects
loc0 loc1 loc2 - room
d0 d1 d2 d3 - roomdoor
obj0 obj1 obj2 obj3 obj4 obj5 obj6 obj7 obj8 obj9 obj10 obj11 obj12 obj13 obj14 - package
)
(:init
(armempty)
(rloc loc0)

(closed d0)
(door loc2 loc0 d0)
(door loc0 loc2 d0) 
(closed d1)
(door loc2 loc0 d1)
(door loc0 loc2 d1) 
(closed d2)
(door loc2 loc0 d2)
(door loc0 loc2 d2) 
(closed d3)
(door loc0 loc1 d3)
(door loc1 loc0 d3) 

(in obj0 loc0) 
(in obj1 loc0) 
(in obj2 loc2) 
(in obj3 loc1) 
(in obj4 loc0) 
(in obj5 loc1) 
(in obj6 loc2) 
(in obj7 loc1) 
(in obj8 loc2) 
(in obj9 loc1) 
(in obj10 loc1) 
(in obj11 loc1) 
(in obj12 loc2) 
(in obj13 loc0) 
(in obj14 loc0) 

(goal_in obj0 loc2) 
(goal_in obj1 loc2) 
(goal_in obj2 loc1) 
(goal_in obj3 loc0) 
(goal_in obj4 loc2) 
(goal_in obj5 loc2) 
(goal_in obj6 loc1) 
(goal_in obj7 loc0) 
(goal_in obj8 loc1) 
(goal_in obj9 loc2) 
(goal_in obj10 loc2) 
(goal_in obj11 loc2) 
(goal_in obj12 loc1) 
(goal_in obj13 loc1) 
(goal_in obj14 loc1) 

)
(:goal (and
(in obj0 loc2) 
(in obj1 loc2) 
(in obj2 loc1) 
(in obj3 loc0) 
(in obj4 loc2) 
(in obj5 loc2) 
(in obj6 loc1) 
(in obj7 loc0) 
(in obj8 loc1) 
(in obj9 loc2) 
(in obj10 loc2) 
(in obj11 loc2) 
(in obj12 loc1) 
(in obj13 loc1) 
(in obj14 loc1) 
))
)