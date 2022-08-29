(define (domain grid-visitAll)
(:requirements :strips :typing :negative-preconditions)
(:types
place - object
)
(:predicates
	(at-robot ?x1 - place)
	(visited ?x1 - place)
	(connected ?x1 - place ?x2 - place)
)
(:action move
	:parameters (?x1 - place ?x2 - place )
	:precondition (and
	(not(at-robot ?x2))
	(at-robot ?x1)
	(connected ?x1 ?x2))
	:effect (and
	(at-robot ?x2)
	(not(at-robot ?x1)))
)
(:action visit
	:parameters (?x1 - place )
	:precondition (and
	(at-robot ?x1)
	(not(visited ?x1)))
	:effect (and
	(visited ?x1))
)
)
