(define (domain miconic)
(:requirements :strips :typing :negative-preconditions)
(:types
floor passenger - object
)
(:predicates
	(lift-at ?x1 - floor)
	(boarded ?x1 - passenger)
	(served ?x1 - passenger)
	(above ?x1 - floor ?x2 - floor)
	(origin ?x1 - passenger ?x2 - floor)
	(destin ?x1 - passenger ?x2 - floor)
)
(:action board
	:parameters (?x1 - floor ?x2 - passenger )
	:precondition (and
	(not(boarded ?x2))
	(not(served ?x2))
	(lift-at ?x1)
	(origin ?x2 ?x1))
	:effect (and
	(boarded ?x2))
)
(:action depart
	:parameters (?x1 - floor ?x2 - passenger )
	:precondition (and
	(boarded ?x2)
	(not(served ?x2))
	(lift-at ?x1)
	(destin ?x2 ?x1))
	:effect (and
	(not(boarded ?x2))
	(served ?x2))
)
(:action up
	:parameters (?x1 - floor ?x2 - floor )
	:precondition (and
	(not(lift-at ?x2))
	(above ?x1 ?x2)
	(lift-at ?x1))
	:effect (and
	(lift-at ?x2)
	(not(lift-at ?x1)))
)
(:action down
	:parameters (?x1 - floor ?x2 - floor )
	:precondition (and
	(not(lift-at ?x2))
	(lift-at ?x1)
	(above ?x2 ?x1))
	:effect (and
	(lift-at ?x2)
	(not(lift-at ?x1)))
)
)
