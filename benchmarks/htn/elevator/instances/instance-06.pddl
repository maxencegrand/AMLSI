(define (problem is)
(:domain miconic)
(:objects
floor_0 - floor
floor_1 - floor
floor_2 - floor
floor_3 - floor
floor_4 - floor
floor_5 - floor
floor_6 - floor
floor_7 - floor
floor_8 - floor
floor_9 - floor
passenger_0 - passenger
passenger_1 - passenger
passenger_2 - passenger
passenger_3 - passenger
passenger_4 - passenger
passenger_5 - passenger
)
(:init
	(above-direct floor_0 floor_1)
	(above-direct floor_1 floor_2)
	(above-direct floor_2 floor_3)
	(above-direct floor_3 floor_4)
	(above-direct floor_4 floor_5)
	(above-direct floor_5 floor_6)
	(above-direct floor_6 floor_7)
	(above-direct floor_7 floor_8)
	(above-direct floor_8 floor_9)
	(lift-at floor_6)
	(above floor_0 floor_1)
	(above floor_0 floor_2)
	(above floor_0 floor_3)
	(above floor_0 floor_4)
	(above floor_0 floor_5)
	(above floor_0 floor_6)
	(above floor_0 floor_7)
	(above floor_0 floor_8)
	(above floor_0 floor_9)
	(above floor_1 floor_2)
	(above floor_1 floor_3)
	(above floor_1 floor_4)
	(above floor_1 floor_5)
	(above floor_1 floor_6)
	(above floor_1 floor_7)
	(above floor_1 floor_8)
	(above floor_1 floor_9)
	(above floor_2 floor_3)
	(above floor_2 floor_4)
	(above floor_2 floor_5)
	(above floor_2 floor_6)
	(above floor_2 floor_7)
	(above floor_2 floor_8)
	(above floor_2 floor_9)
	(above floor_3 floor_4)
	(above floor_3 floor_5)
	(above floor_3 floor_6)
	(above floor_3 floor_7)
	(above floor_3 floor_8)
	(above floor_3 floor_9)
	(above floor_4 floor_5)
	(above floor_4 floor_6)
	(above floor_4 floor_7)
	(above floor_4 floor_8)
	(above floor_4 floor_9)
	(above floor_5 floor_6)
	(above floor_5 floor_7)
	(above floor_5 floor_8)
	(above floor_5 floor_9)
	(above floor_6 floor_7)
	(above floor_6 floor_8)
	(above floor_6 floor_9)
	(above floor_7 floor_8)
	(above floor_7 floor_9)
	(above floor_8 floor_9)
	(origin passenger_0 floor_0)
	(origin passenger_1 floor_5)
	(origin passenger_2 floor_2)
	(origin passenger_3 floor_4)
	(origin passenger_4 floor_1)
	(origin passenger_5 floor_7)
	(destin passenger_0 floor_3)
	(destin passenger_1 floor_0)
	(destin passenger_2 floor_6)
	(destin passenger_3 floor_7)
	(destin passenger_4 floor_0)
	(destin passenger_5 floor_0)
)
(:goal (and
	(served passenger_0)
	(served passenger_1)
	(served passenger_2)
	(served passenger_3)
	(served passenger_4)
	(served passenger_5)
))
)