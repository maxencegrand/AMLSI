(define (domain blocks)
(:requirements :strips :typing :negative-preconditions)
(:types
block - object
)
(:predicates
	(clear ?x1 - block)
	(ontable ?x1 - block)
	(handempty)
	(holding ?x1 - block)
	(on ?x1 - block ?x2 - block)
)
(:action pick-up
	:parameters (?x1 - block )
	:precondition (and
	(clear ?x1)
	(ontable ?x1)
	(not(holding ?x1))
	(handempty))
	:effect (and
	(not(clear ?x1))
	(not(ontable ?x1))
	(holding ?x1)
	(not(handempty)))
)
(:action put-down
	:parameters (?x1 - block )
	:precondition (and
	(not(clear ?x1))
	(not(ontable ?x1))
	(holding ?x1)
	(not(handempty)))
	:effect (and
	(clear ?x1)
	(ontable ?x1)
	(not(holding ?x1))
	(handempty))
)
(:action stack
	:parameters (?x1 - block ?x2 - block )
	:precondition (and
	(not(on ?x2 ?x1))
	(not(clear ?x1))
	(not(ontable ?x1))
	(holding ?x1)
	(clear ?x2)
	(not(holding ?x2))
	(not(handempty))
	(not(on ?x1 ?x2)))
	:effect (and
	(clear ?x1)
	(not(holding ?x1))
	(not(clear ?x2))
	(handempty)
	(on ?x1 ?x2))
)
(:action unstack
	:parameters (?x1 - block ?x2 - block )
	:precondition (and
	(not(on ?x2 ?x1))
	(clear ?x1)
	(not(ontable ?x1))
	(not(holding ?x1))
	(not(clear ?x2))
	(not(holding ?x2))
	(handempty)
	(on ?x1 ?x2))
	:effect (and
	(not(clear ?x1))
	(holding ?x1)
	(clear ?x2)
	(not(handempty))
	(not(on ?x1 ?x2)))
)
)
