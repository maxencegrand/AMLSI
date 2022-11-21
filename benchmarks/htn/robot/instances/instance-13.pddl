(define (problem is)
(:domain robot)
(:objects
loc0 loc1 loc2 - room
d0 d1 - roomdoor
obj0 obj1 obj2 obj3 obj4 obj5 obj6 obj7 obj8 obj9 - package
)
(:init
(armempty)
(rloc loc2)

(closed d0)
(door loc2 loc0 d0)
(door loc0 loc2 d0) 
(closed d1)
(door loc1 loc0 d1)
(door loc0 loc1 d1) 

(in obj0 loc2) 
(in obj1 loc1) 
(in obj2 loc2) 
(in obj3 loc0) 
(in obj4 loc0) 
(in obj5 loc2) 
(in obj6 loc2) 
(in obj7 loc0) 
(in obj8 loc1) 
(in obj9 loc2) 

(goal_in obj0 loc1) 
(goal_in obj1 loc2) 
(goal_in obj2 loc0) 
(goal_in obj3 loc2) 
(goal_in obj4 loc1) 
(goal_in obj5 loc0) 
(goal_in obj6 loc1) 
(goal_in obj7 loc1) 
(goal_in obj8 loc2) 
(goal_in obj9 loc0) 

)
(:goal (and
(in obj0 loc1) 
(in obj1 loc2) 
(in obj2 loc0) 
(in obj3 loc2) 
(in obj4 loc1) 
(in obj5 loc0) 
(in obj6 loc1) 
(in obj7 loc1) 
(in obj8 loc2) 
(in obj9 loc0) 
))
)