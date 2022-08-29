(define (domain spanner)
(:requirements :strips :typing :negative-preconditions)
(:types
location T1 - object
nut spanner man - T1
)
(:predicates
	(at ?x1 - T1 ?x2 - location)
	(carrying ?x1 - man ?x2 - spanner)
	(useable ?x1 - spanner)
	(loose ?x1 - nut)
	(tightened ?x1 - nut)
	(link ?x1 - location ?x2 - location)
)
(:action walk
	:parameters (?x1 - location ?x2 - location ?x3 - man )
	:precondition (and
	(at ?x3 ?x1)
	(link ?x1 ?x2)
	(not(at ?x3 ?x2)))
	:effect (and
	(not(at ?x3 ?x1))
	(at ?x3 ?x2))
)
(:action pickup_spanner
	:parameters (?x1 - location ?x2 - spanner ?x3 - man )
	:precondition (and
	(at ?x3 ?x1)
	(at ?x2 ?x1)
	(not(carrying ?x3 ?x2))
	(useable ?x2))
	:effect (and
	(not(at ?x2 ?x1))
	(carrying ?x3 ?x2))
)
(:action tighten_nut
	:parameters (?x1 - location ?x2 - spanner ?x3 - man ?x4 - nut )
	:precondition (and
	(at ?x3 ?x1)
	(carrying ?x3 ?x2)
	(loose ?x4)
	(at ?x4 ?x1)
	(useable ?x2)
	(not(tightened ?x4)))
	:effect (and
	(not(loose ?x4))
	(not(useable ?x2))
	(tightened ?x4))
)
)
