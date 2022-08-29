(define (domain domain_htn)
	(:requirements :typing)
	(:types
		package - locatable
		capacity_number - object
		location - object
		target - object
		vehicle - locatable
		locatable - object
	)
	(:predicates
		(road ?arg0 - location ?arg1 - location)
		(at ?arg0 - locatable ?arg1 - location)
		(in ?arg0 - package ?arg1 - vehicle)
		(capacity ?arg0 - vehicle ?arg1 - capacity_number)
		(capacity_predecessor ?arg0 - capacity_number ?arg1 - capacity_number)
	)

	(:action drive
		:parameters (?v - vehicle ?l1 - location ?l2 - location)
		:precondition
			(and
				(at ?v ?l1)
				(road ?l1 ?l2)
			)
		:effect
			(and
				(not (at ?v ?l1))
				(at ?v ?l2)
			)
	)

	(:action pick_up
		:parameters (?v - vehicle ?l - location ?p - package ?s1 - capacity_number ?s2 - capacity_number)
		:precondition
			(and
				(at ?v ?l)
				(at ?p ?l)
				(capacity_predecessor ?s1 ?s2)
				(capacity ?v ?s2)
			)
		:effect
			(and
				(not (at ?p ?l))
				(in ?p ?v)
				(capacity ?v ?s1)
				(not (capacity ?v ?s2))
			)
	)

	(:action drop
		:parameters (?v - vehicle ?l - location ?p - package ?s1 - capacity_number ?s2 - capacity_number)
		:precondition
			(and
				(at ?v ?l)
				(in ?p ?v)
				(capacity_predecessor ?s1 ?s2)
				(capacity ?v ?s1)
			)
		:effect
			(and
				(not (in ?p ?v))
				(at ?p ?l)
				(capacity ?v ?s2)
				(not (capacity ?v ?s1))
			)
	)
)
