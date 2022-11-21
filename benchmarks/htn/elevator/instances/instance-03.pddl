(define (problem is)
(:domain miconic)
(:objects
floor_0 - floor
floor_1 - floor
floor_2 - floor
floor_3 - floor
floor_4 - floor
passenger_0 - passenger
passenger_1 - passenger
passenger_2 - passenger
passenger_3 - passenger
)
(:init
	(above-direct floor_0 floor_1)
	(above-direct floor_1 floor_2)
	(above-direct floor_2 floor_3)
	(above-direct floor_3 floor_4)
	(lift-at floor_0)
	(above floor_0 floor_1)
	(above floor_0 floor_2)
	(above floor_0 floor_3)
	(above floor_0 floor_4)
	(above floor_1 floor_2)
	(above floor_1 floor_3)
	(above floor_1 floor_4)
	(above floor_2 floor_3)
	(above floor_2 floor_4)
	(above floor_3 floor_4)
	(origin passenger_0 floor_1)
	(origin passenger_1 floor_2)
	(origin passenger_2 floor_0)
	(origin passenger_3 floor_4)
	(destin passenger_0 floor_3)
	(destin passenger_1 floor_0)
	(destin passenger_2 floor_4)
	(destin passenger_3 floor_0)
)
(:goal (and
	(served passenger_0)
	(served passenger_1)
	(served passenger_2)
	(served passenger_3)
))
)