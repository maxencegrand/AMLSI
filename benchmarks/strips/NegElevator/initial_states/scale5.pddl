(define (problem is)
(:domain miconic)
(:objects
floor_0 - floor
floor_1 - floor
floor_2 - floor
floor_3 - floor
floor_4 - floor
floor_5 - floor
passenger_0 - passenger
passenger_1 - passenger
passenger_2 - passenger
passenger_3 - passenger
passenger_4 - passenger
)
(:init
	(above floor_0 floor_1)
	(above floor_1 floor_2)
	(above floor_2 floor_3)
	(above floor_3 floor_4)
	(above floor_4 floor_5)
	(lift-at floor_5)
	(origin passenger_0 floor_1)
	(origin passenger_1 floor_3)
	(origin passenger_2 floor_0)
	(origin passenger_3 floor_2)
	(origin passenger_4 floor_1)
	(destin passenger_0 floor_2)
	(destin passenger_1 floor_5)
	(destin passenger_2 floor_3)
	(destin passenger_3 floor_3)
	(destin passenger_4 floor_0)
)
(:goal ())
)