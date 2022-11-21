(define (problem is)
(:domain robot)
(:objects
loc0 loc1 loc2 - room
d0 d1 d2 d3 d4 d5 - roomdoor
obj0 obj1 obj2 obj3 obj4 - package
)
(:init
(armempty)
(rloc loc2)

(closed d0)
(door loc0 loc2 d0)
(door loc2 loc0 d0) 
(closed d1)
(door loc2 loc0 d1)
(door loc0 loc2 d1) 
(closed d2)
(door loc2 loc0 d2)
(door loc0 loc2 d2) 
(closed d3)
(door loc2 loc0 d3)
(door loc0 loc2 d3) 
(closed d4)
(door loc0 loc2 d4)
(door loc2 loc0 d4) 
(closed d5)
(door loc1 loc2 d5)
(door loc2 loc1 d5) 

(in obj0 loc1) 
(in obj1 loc2) 
(in obj2 loc2) 
(in obj3 loc0) 
(in obj4 loc1) 

(goal_in obj0 loc0) 
(goal_in obj1 loc1) 
(goal_in obj2 loc0) 
(goal_in obj3 loc1) 
(goal_in obj4 loc2) 

)
(:goal (and
(in obj0 loc0) 
(in obj1 loc1) 
(in obj2 loc0) 
(in obj3 loc1) 
(in obj4 loc2) 
))
)