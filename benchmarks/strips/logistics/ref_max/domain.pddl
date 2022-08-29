(define (domain logistics)
(:requirements :strips :typing :negative-preconditions)
(:types
city T1 T3 - object
location airport - T1
T2 package - T3
airplane truck - T2
)
(:predicates
	(at ?x1 - T3 ?x2 - T1)
	(in ?x1 - package ?x2 - T2)
	(in-city ?x1 - T1 ?x2 - city)
)
(:action load-truck
	:parameters (?x1 - package ?x2 - truck ?x3 - T1 )
	:precondition (and
	(at ?x2 ?x3)
	(not(in ?x1 ?x2))
	(at ?x1 ?x3))
	:effect (and
	(in ?x1 ?x2)
	(not(at ?x1 ?x3)))
)
(:action load-airplane
	:parameters (?x1 - package ?x2 - airplane ?x3 - airport )
	:precondition (and
	(at ?x2 ?x3)
	(not(in ?x1 ?x2))
	(at ?x1 ?x3))
	:effect (and
	(in ?x1 ?x2)
	(not(at ?x1 ?x3)))
)
(:action unload-truck
	:parameters (?x1 - package ?x2 - truck ?x3 - T1 )
	:precondition (and
	(at ?x2 ?x3)
	(in ?x1 ?x2)
	(not(at ?x1 ?x3)))
	:effect (and
	(not(in ?x1 ?x2))
	(at ?x1 ?x3))
)
(:action unload-airplane
	:parameters (?x1 - package ?x2 - airplane ?x3 - airport )
	:precondition (and
	(at ?x2 ?x3)
	(in ?x1 ?x2)
	(not(at ?x1 ?x3)))
	:effect (and
	(not(in ?x1 ?x2))
	(at ?x1 ?x3))
)
(:action drive-truck
	:parameters (?x1 - truck ?x2 - T1 ?x3 - T1 ?x4 - city )
	:precondition (and
	(in-city ?x2 ?x4)
	(in-city ?x3 ?x4)
	(at ?x1 ?x2)
	(not(at ?x1 ?x3)))
	:effect (and
	(not(at ?x1 ?x2))
	(at ?x1 ?x3))
)
(:action fly-airplane
	:parameters (?x1 - airplane ?x2 - airport ?x3 - airport )
	:precondition (and
	(at ?x1 ?x2)
	(not(at ?x1 ?x3)))
	:effect (and
	(not(at ?x1 ?x2))
	(at ?x1 ?x3))
)
)
