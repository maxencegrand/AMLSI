(define (domain hanoi)
(:requirements :strips :typing :negative-preconditions)
(:types
disk case - object
)
(:predicates
	(clear_disk ?x1 - disk)
	(on_case ?x1 - disk ?x2 - case)
	(handempty)
	(clear_case ?x1 - case)
	(holding ?x1 - disk)
	(on_disk ?x1 - disk ?x2 - disk)
	(bigger ?x1 - disk ?x2 - disk)
)
(:action pick_up
	:parameters (?x1 - disk ?x2 - case )
	:precondition (and
	(clear_disk ?x1)
	(on_case ?x1 ?x2)
	(not(holding ?x1))
	(not(clear_case ?x2))
	(handempty))
	:effect (and
	(not(clear_disk ?x1))
	(not(on_case ?x1 ?x2))
	(holding ?x1)
	(clear_case ?x2)
	(not(handempty)))
)
(:action unstack
	:parameters (?x1 - disk ?x2 - disk )
	:precondition (and
	(clear_disk ?x1)
	(not(clear_disk ?x2))
	(not(holding ?x1))
	(bigger ?x2 ?x1)
	(not(holding ?x2))
	(on_disk ?x1 ?x2)
	(handempty))
	:effect (and
	(not(clear_disk ?x1))
	(clear_disk ?x2)
	(holding ?x1)
	(not(on_disk ?x1 ?x2))
	(not(handempty)))
)
(:action put_down
	:parameters (?x1 - disk ?x2 - case )
	:precondition (and
	(not(clear_disk ?x1))
	(not(on_case ?x1 ?x2))
	(holding ?x1)
	(clear_case ?x2)
	(not(handempty)))
	:effect (and
	(clear_disk ?x1)
	(on_case ?x1 ?x2)
	(not(holding ?x1))
	(not(clear_case ?x2))
	(handempty))
)
(:action stack
	:parameters (?x1 - disk ?x2 - disk )
	:precondition (and
	(not(clear_disk ?x1))
	(clear_disk ?x2)
	(holding ?x1)
	(bigger ?x2 ?x1)
	(not(holding ?x2))
	(not(on_disk ?x1 ?x2))
	(not(handempty)))
	:effect (and
	(clear_disk ?x1)
	(not(clear_disk ?x2))
	(not(holding ?x1))
	(on_disk ?x1 ?x2)
	(handempty))
)
)
