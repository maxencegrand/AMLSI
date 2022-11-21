(define (problem is)
(:domain miconic)
(:objects
floor_0 - floor
floor_1 - floor
floor_2 - floor
passenger_0 - passenger
passenger_1 - passenger
)
(:init
	(above-direct floor_0 floor_1)
	(above-direct floor_1 floor_2)
	(lift-at floor_1)
	(above floor_0 floor_1)
	(above floor_0 floor_2)
	(above floor_1 floor_2)
	(origin passenger_0 floor_1)
	(origin passenger_1 floor_2)
	(destin passenger_0 floor_0)
	(destin passenger_1 floor_0)
)
(:goal (and
	(served passenger_0)
	(served passenger_1)
))
)