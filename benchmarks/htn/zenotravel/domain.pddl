(define (domain zenotravel)
	(:requirements :typing :negative-preconditions)
	(:types
		Time - Object
		City - Object
		Aircraft - Thing
		Person - Thing
		Thing - Object
		FLevel - Object
	)
	(:predicates
		(at ?arg0 - Thing ?arg1 - City)
		(in ?arg0 - Person ?arg1 - Aircraft)
		(next ?arg0 - FLevel ?arg1 - FLevel)
		(max ?arg0 - FLevel)
        (min ?arg0 - FLevel)
        (not-min ?arg0 - FLevel)
		(fuel-level ?arg0 - Aircraft ?arg1 - FLevel)
	)
	(:action board
		:parameters (?p - Person ?a - Aircraft ?c - City)
		:precondition
			(and
				(at ?p ?c)
                (at ?a ?c)
			)
		:effect
			(and
				(not (at ?p ?c))
				(in ?p ?a)
			)
	)

	(:action debark
		:parameters (?p - Person ?a - Aircraft ?c - City)
		:precondition
			(and
				(in ?p ?a)
                (at ?a ?c)
			)
		:effect
			(and
				(not (in ?p ?a))
				(at ?p ?c)
			)
	)

	(:action refuel
		:parameters (?a - Aircraft ?l - FLevel ?l1 - FLevel)
		:precondition
			(and
				(fuel-level ?a ?l)
			)
		:effect
			(and
				(not (fuel-level ?a ?l))
				(fuel-level ?a ?l1)
			)
	)

	(:action fly
		:parameters (?a - Aircraft ?c1 - City ?c2 - City ?l1 - FLevel ?l2 - FLevel)
		:precondition
			(and
				(at ?a ?c1)
				(fuel-level ?a ?l1)
                (next ?l2 ?l1)
			)
		:effect
			(and
				(not (at ?a ?c1))
				(at ?a ?c2)
				(not (fuel-level ?a ?l1))
				(fuel-level ?a ?l2)
			)
	)
)
