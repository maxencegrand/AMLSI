(define (problem is)
(:domain robot)
(:objects
loc0 loc1 - room
d0 - roomdoor
obj0 obj1 obj2 obj3 obj4 - package
)
(:init
(armempty)
(rloc loc0)

(closed d0)
(door loc1 loc0 d0)
(door loc0 loc1 d0) 

(in obj0 loc0) 
(in obj1 loc1) 
(in obj2 loc1) 
(in obj3 loc0) 
(in obj4 loc1) 

(goal_in obj0 loc1) 
(goal_in obj1 loc0) 
(goal_in obj2 loc0) 
(goal_in obj3 loc1) 
(goal_in obj4 loc0) 

)
(:goal (and
(in obj0 loc1) 
(in obj1 loc0) 
(in obj2 loc0) 
(in obj3 loc1) 
(in obj4 loc0) 
))
)