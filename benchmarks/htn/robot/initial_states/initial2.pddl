(define (problem is)
(:domain robot)
(:objects
loc0 loc1 loc2 - room
d0 d1 d2 - roomdoor
obj0 obj1 - package
)
(:init
(armempty)
(rloc loc0)

(closed d0)
(door loc2 loc0 d0)
(door loc0 loc2 d0) 
(closed d1)
(door loc0 loc2 d1)
(door loc2 loc0 d1) 
(closed d2)
(door loc1 loc0 d2)
(door loc0 loc1 d2) 

(in obj0 loc1) 
(in obj1 loc2) 

(goal_in obj0 loc2) 
(goal_in obj1 loc1) 

)
(:goal (and
(in obj0 loc2) 
(in obj1 loc1) 
))
)