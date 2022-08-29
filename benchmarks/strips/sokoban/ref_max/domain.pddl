(define (domain sokoban-typed)
(:requirements :strips :typing :negative-preconditions)
(:types
box loc dir - object
)
(:predicates
	(clear ?x1 - loc)
	(at-robot ?x1 - loc)
	(at ?x1 - box ?x2 - loc)
	(adjacent ?x1 - loc ?x2 - loc ?x3 - dir)
)
(:action move
	:parameters (?x1 - loc ?x2 - loc ?x3 - dir )
	:precondition (and
	(not(at-robot ?x2))
	(clear ?x1)
	(at-robot ?x1)
	(clear ?x2)
	(adjacent ?x1 ?x2 ?x3))
	:effect (and
	(at-robot ?x2)
	(not(at-robot ?x1)))
)
(:action push
	:parameters (?x1 - loc ?x2 - loc ?x3 - loc ?x4 - dir ?x5 - box )
	:precondition (and
	(not(at-robot ?x2))
	(clear ?x1)
	(at-robot ?x1)
	(clear ?x3)
	(not(clear ?x2))
	(adjacent ?x1 ?x2 ?x4)
	(adjacent ?x2 ?x3 ?x4)
	(not(at ?x5 ?x1))
	(not(at-robot ?x3))
	(not(at ?x5 ?x3))
	(at ?x5 ?x2))
	:effect (and
	(at-robot ?x2)
	(not(at-robot ?x1))
	(not(clear ?x3))
	(clear ?x2)
	(at ?x5 ?x3)
	(not(at ?x5 ?x2)))
)
)
