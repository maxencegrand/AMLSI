(define (domain grid-visit-all)
(:requirements :typing :negative-preconditions)
(:types        place - object)
(:predicates (connected ?x ?y - place)
	     (at-robot ?x - place)
	     (visited ?x - place)
)
	
(:action move
:parameters (?curpos ?nextpos - place)
:precondition (and (at-robot ?curpos) (connected ?curpos ?nextpos))
:effect (and (at-robot ?nextpos) (not (at-robot ?curpos)))
)
(:action visit
:parameters (?pos - place)
:precondition (and (at-robot ?pos) (not(visited ?pos)))
:effect (and (visited ?pos))
)

)
