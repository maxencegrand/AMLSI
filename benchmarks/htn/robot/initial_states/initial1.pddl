(define (problem is)
(:domain robot)
(:objects
loc0 loc1 loc2 - room
d0 d1 - roomdoor
obj0 - package
)
(:init
(armempty)
(rloc loc1)

(closed d0)
(door loc0 loc2 d0)
(door loc2 loc0 d0) 
(closed d1)
(door loc2 loc1 d1)
(door loc1 loc2 d1) 

(in obj0 loc1) 

(goal_in obj0 loc0) 

)
(:goal (and
(in obj0 loc0) 
))
)