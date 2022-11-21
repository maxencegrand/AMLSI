(define (problem is)
(:domain miconic)
(:objects
floor_0 - floor
floor_1 - floor
floor_2 - floor
floor_3 - floor
passenger_0 - passenger
passenger_1 - passenger
)
(:init
	(above-direct floor_0 floor_1)
	(above-direct floor_1 floor_2)
	(above-direct floor_2 floor_3)
	(lift-at floor_2)
	(above floor_0 floor_1)
	(above floor_0 floor_2)
	(above floor_0 floor_3)
	(above floor_1 floor_2)
	(above floor_1 floor_3)
	(above floor_2 floor_3)
	(origin passenger_0 floor_0)
	(origin passenger_1 floor_0)
	(destin passenger_0 floor_1)
	(destin passenger_1 floor_2)
)
(:goal (and
	(served passenger_0)
	(served passenger_1)
))
)