(define (domain floor-tile)
(:requirements :strips :typing :negative-preconditions)
(:types
robot tile color - object
)
(:predicates
	(robot-has ?x1 - robot ?x2 - color)
	(robot-at ?x1 - robot ?x2 - tile)
	(clear ?x1 - tile)
	(painted ?x1 - tile ?x2 - color)
	(available-color ?x1 - color)
	(up ?x1 - tile ?x2 - tile)
	(down ?x1 - tile ?x2 - tile)
	(right ?x1 - tile ?x2 - tile)
	(left ?x1 - tile ?x2 - tile)
)
(:action change-color
	:parameters (?x1 - robot ?x2 - color ?x3 - color )
	:precondition (and
	(available-color ?x3)
	(available-color ?x2)
	(robot-has ?x1 ?x2)
	(not(robot-has ?x1 ?x3)))
	:effect (and
	(not(robot-has ?x1 ?x2))
	(robot-has ?x1 ?x3))
)
(:action paint-up
	:parameters (?x1 - robot ?x2 - tile ?x3 - tile ?x4 - color )
	:precondition (and
	(up ?x2 ?x3)
	(not(painted ?x2 ?x4))
	(not(clear ?x3))
	(clear ?x2)
	(robot-has ?x1 ?x4)
	(not(painted ?x3 ?x4))
	(not(robot-at ?x1 ?x2))
	(robot-at ?x1 ?x3)
	(down ?x3 ?x2)
	(available-color ?x4))
	:effect (and
	(painted ?x2 ?x4)
	(not(clear ?x2)))
)
(:action paint-down
	:parameters (?x1 - robot ?x2 - tile ?x3 - tile ?x4 - color )
	:precondition (and
	(not(clear ?x3))
	(not(painted ?x2 ?x4))
	(clear ?x2)
	(robot-has ?x1 ?x4)
	(not(painted ?x3 ?x4))
	(up ?x3 ?x2)
	(not(robot-at ?x1 ?x2))
	(robot-at ?x1 ?x3)
	(down ?x2 ?x3)
	(available-color ?x4))
	:effect (and
	(painted ?x2 ?x4)
	(not(clear ?x2)))
)
(:action up
	:parameters (?x1 - robot ?x2 - tile ?x3 - tile )
	:precondition (and
	(clear ?x3)
	(not(clear ?x2))
	(up ?x3 ?x2)
	(robot-at ?x1 ?x2)
	(not(robot-at ?x1 ?x3))
	(down ?x2 ?x3))
	:effect (and
	(not(clear ?x3))
	(clear ?x2)
	(not(robot-at ?x1 ?x2))
	(robot-at ?x1 ?x3))
)
(:action down
	:parameters (?x1 - robot ?x2 - tile ?x3 - tile )
	:precondition (and
	(up ?x2 ?x3)
	(clear ?x3)
	(not(clear ?x2))
	(robot-at ?x1 ?x2)
	(not(robot-at ?x1 ?x3))
	(down ?x3 ?x2))
	:effect (and
	(not(clear ?x3))
	(clear ?x2)
	(not(robot-at ?x1 ?x2))
	(robot-at ?x1 ?x3))
)
(:action right
	:parameters (?x1 - robot ?x2 - tile ?x3 - tile )
	:precondition (and
	(clear ?x3)
	(not(clear ?x2))
	(left ?x2 ?x3)
	(robot-at ?x1 ?x2)
	(not(robot-at ?x1 ?x3))
	(right ?x3 ?x2))
	:effect (and
	(not(clear ?x3))
	(clear ?x2)
	(not(robot-at ?x1 ?x2))
	(robot-at ?x1 ?x3))
)
(:action left
	:parameters (?x1 - robot ?x2 - tile ?x3 - tile )
	:precondition (and
	(left ?x3 ?x2)
	(clear ?x3)
	(not(clear ?x2))
	(right ?x2 ?x3)
	(robot-at ?x1 ?x2)
	(not(robot-at ?x1 ?x3)))
	:effect (and
	(not(clear ?x3))
	(clear ?x2)
	(not(robot-at ?x1 ?x2))
	(robot-at ?x1 ?x3))
)
)
