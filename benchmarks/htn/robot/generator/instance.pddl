(define (problem is)
(:domain robot)
(:objects
loc0 loc1 loc2 - room
d0 d1 d2 d3 - roomdoor
obj0 obj1 obj2 - package
)
(:init
(armempty)
(rloc loc1)

(closed d0)
(door loc1 loc0 d0)
(door loc0 loc1 d0) 
(closed d1)
(door loc0 loc1 d1)
(door loc1 loc0 d1) 
(closed d2)
(door loc1 loc0 d2)
(door loc0 loc1 d2) 
(closed d3)
(door loc0 loc2 d3)
(door loc2 loc0 d3) 

(in obj0 loc1) 
(in obj1 loc0) 
(in obj2 loc1) 

(goal_in obj0 loc0) 
(goal_in obj1 loc1) 
(goal_in obj2 loc0) 

)
(:goal (and
(in obj0 loc0) 
(in obj1 loc1) 
(in obj2 loc0) 
))
)