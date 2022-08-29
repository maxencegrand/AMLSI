(define (problem ZTRAVEL-5-20)
(:domain zeno-travel)
(:objects
	plane1 - aircraft
	person1 - person
	person2 - person
	city0 - city
	city1 - city
	fl0 - flevel
	fl1 - flevel
	)
(:init
	(at plane1 city0)
	(fuel-level plane1 fl0)
	(at person1 city0)
	(at person2 city1)
	(next fl0 fl1)
)
(:goal (and
))

(:metric minimize (total-time))
)
