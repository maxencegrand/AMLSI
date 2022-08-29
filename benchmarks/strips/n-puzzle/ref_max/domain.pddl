(define (domain n-puzzle-typed)
(:requirements :strips :typing :negative-preconditions)
(:types
tile position - object
)
(:predicates
	(at ?x1 - tile ?x2 - position)
	(empty ?x1 - position)
	(neighbor ?x1 - position ?x2 - position)
)
(:action move
	:parameters (?x1 - tile ?x2 - position ?x3 - position )
	:precondition (and
	(not(empty ?x2))
	(empty ?x3)
	(neighbor ?x3 ?x2)
	(neighbor ?x2 ?x3)
	(at ?x1 ?x2)
	(not(at ?x1 ?x3)))
	:effect (and
	(empty ?x2)
	(not(empty ?x3))
	(not(at ?x1 ?x2))
	(at ?x1 ?x3))
)
)
