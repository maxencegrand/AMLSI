(define (problem ei1)
(:domain zeno-travel)
(:objects
	plane1 - aircraft
	person1 - person
	city0 - city
	city1 - city
	fl0 - flevel
	fl1 - flevel
	fl2 - flevel
	)
(:init
	(at_aircraft plane1 city0)
	(fuel-level plane1 fl2)
	
	(at person1 city0)

	(next fl1 fl2)
        (next fl0 fl1)
)
(:goal ())

)
