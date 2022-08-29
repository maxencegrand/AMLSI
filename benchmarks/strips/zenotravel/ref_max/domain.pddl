(define (domain zeno-travel)
(:requirements :strips :typing :negative-preconditions)
(:types
aircraft flevel person city - object
)
(:predicates
	(at ?x1 - person ?x2 - city)
	(at_aircraft ?x1 - aircraft ?x2 - city)
	(in ?x1 - person ?x2 - aircraft)
	(fuel-level ?x1 - aircraft ?x2 - flevel)
	(next ?x1 - flevel ?x2 - flevel)
)
(:action board
	:parameters (?x1 - person ?x2 - aircraft ?x3 - city )
	:precondition (and
	(not(in ?x1 ?x2))
	(at_aircraft ?x2 ?x3)
	(at ?x1 ?x3))
	:effect (and
	(in ?x1 ?x2)
	(not(at ?x1 ?x3)))
)
(:action debark
	:parameters (?x1 - person ?x2 - aircraft ?x3 - city )
	:precondition (and
	(in ?x1 ?x2)
	(at_aircraft ?x2 ?x3)
	(not(at ?x1 ?x3)))
	:effect (and
	(not(in ?x1 ?x2))
	(at ?x1 ?x3))
)
(:action fly
	:parameters (?x1 - aircraft ?x2 - city ?x3 - city ?x4 - flevel ?x5 - flevel )
	:precondition (and
	(not(at_aircraft ?x1 ?x3))
	(at_aircraft ?x1 ?x2)
	(not(fuel-level ?x1 ?x5))
	(fuel-level ?x1 ?x4)
	(next ?x5 ?x4))
	:effect (and
	(at_aircraft ?x1 ?x3)
	(not(at_aircraft ?x1 ?x2))
	(fuel-level ?x1 ?x5)
	(not(fuel-level ?x1 ?x4)))
)
(:action zoom
	:parameters (?x1 - aircraft ?x2 - city ?x3 - city ?x4 - flevel ?x5 - flevel ?x6 - flevel )
	:precondition (and
	(not(at_aircraft ?x1 ?x3))
	(at_aircraft ?x1 ?x2)
	(next ?x6 ?x5)
	(not(fuel-level ?x1 ?x5))
	(fuel-level ?x1 ?x4)
	(not(fuel-level ?x1 ?x6))
	(next ?x5 ?x4))
	:effect (and
	(at_aircraft ?x1 ?x3)
	(not(at_aircraft ?x1 ?x2))
	(not(fuel-level ?x1 ?x4))
	(fuel-level ?x1 ?x6))
)
(:action refuel
	:parameters (?x1 - aircraft ?x2 - city ?x3 - flevel ?x4 - flevel )
	:precondition (and
	(at_aircraft ?x1 ?x2)
	(fuel-level ?x1 ?x3)
	(not(fuel-level ?x1 ?x4))
	(next ?x3 ?x4))
	:effect (and
	(not(fuel-level ?x1 ?x3))
	(fuel-level ?x1 ?x4))
)
)
