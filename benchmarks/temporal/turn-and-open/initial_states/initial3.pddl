(define (problem turnandopen-2-8-10)
(:domain turnandopen-strips)
(:objects robot1 - robot
grip - gripper
room1 room2 - room
door1  - door
ball1 ball2 - obj)
(:init
(closed door1)
(connected room1 room2 door1)
(connected room2 room1 door1)
(at-robby robot1 room1)
(free robot1 grip)
(at ball1 room2)
(at ball2 room2)
)
(:goal
(and
)
)
)
