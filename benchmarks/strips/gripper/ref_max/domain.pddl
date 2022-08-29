(define (domain gripper-typed)
(:requirements :strips :typing :negative-preconditions)
(:types
ball room gripper - object
)
(:predicates
	(at-robby ?x1 - room)
	(at ?x1 - ball ?x2 - room)
	(free ?x1 - gripper)
	(carry ?x1 - ball ?x2 - gripper)
)
(:action move
	:parameters (?x1 - room ?x2 - room )
	:precondition (and
	(at-robby ?x1)
	(not(at-robby ?x2)))
	:effect (and
	(not(at-robby ?x1))
	(at-robby ?x2))
)
(:action pick
	:parameters (?x1 - ball ?x2 - room ?x3 - gripper )
	:precondition (and
	(at-robby ?x2)
	(not(carry ?x1 ?x3))
	(at ?x1 ?x2)
	(free ?x3))
	:effect (and
	(carry ?x1 ?x3)
	(not(at ?x1 ?x2))
	(not(free ?x3)))
)
(:action drop
	:parameters (?x1 - ball ?x2 - room ?x3 - gripper )
	:precondition (and
	(at-robby ?x2)
	(carry ?x1 ?x3)
	(not(at ?x1 ?x2))
	(not(free ?x3)))
	:effect (and
	(not(carry ?x1 ?x3))
	(at ?x1 ?x2)
	(free ?x3))
)
)
