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
passenger_0 - passenger
passenger_1 - passenger
passenger_2 - passenger
passenger_3 - passenger
passenger_4 - passenger
passenger_5 - passenger
passenger_6 - passenger
passenger_7 - passenger
passenger_8 - passenger
passenger_9 - passenger
passenger_10 - passenger
passenger_11 - passenger
passenger_12 - passenger
passenger_13 - passenger
passenger_14 - passenger
)
(:init
	(above-direct floor_0 floor_1)
	(above-direct floor_1 floor_2)
	(above-direct floor_2 floor_3)
	(above-direct floor_3 floor_4)
	(above-direct floor_4 floor_5)
	(above-direct floor_5 floor_6)
	(above-direct floor_6 floor_7)
	(lift-at floor_4)
	(above floor_0 floor_1)
	(above floor_0 floor_2)
	(above floor_0 floor_3)
	(above floor_0 floor_4)
	(above floor_0 floor_5)
	(above floor_0 floor_6)
	(above floor_0 floor_7)
	(above floor_1 floor_2)
	(above floor_1 floor_3)
	(above floor_1 floor_4)
	(above floor_1 floor_5)
	(above floor_1 floor_6)
	(above floor_1 floor_7)
	(above floor_2 floor_3)
	(above floor_2 floor_4)
	(above floor_2 floor_5)
	(above floor_2 floor_6)
	(above floor_2 floor_7)
	(above floor_3 floor_4)
	(above floor_3 floor_5)
	(above floor_3 floor_6)
	(above floor_3 floor_7)
	(above floor_4 floor_5)
	(above floor_4 floor_6)
	(above floor_4 floor_7)
	(above floor_5 floor_6)
	(above floor_5 floor_7)
	(above floor_6 floor_7)
	(origin passenger_0 floor_0)
	(origin passenger_1 floor_7)
	(origin passenger_2 floor_2)
	(origin passenger_3 floor_6)
	(origin passenger_4 floor_3)
	(origin passenger_5 floor_3)
	(origin passenger_6 floor_5)
	(origin passenger_7 floor_7)
	(origin passenger_8 floor_7)
	(origin passenger_9 floor_1)
	(origin passenger_10 floor_0)
	(origin passenger_11 floor_1)
	(origin passenger_12 floor_1)
	(origin passenger_13 floor_0)
	(origin passenger_14 floor_0)
	(destin passenger_0 floor_6)
	(destin passenger_1 floor_5)
	(destin passenger_2 floor_6)
	(destin passenger_3 floor_7)
	(destin passenger_4 floor_7)
	(destin passenger_5 floor_5)
	(destin passenger_6 floor_4)
	(destin passenger_7 floor_5)
	(destin passenger_8 floor_4)
	(destin passenger_9 floor_6)
	(destin passenger_10 floor_6)
	(destin passenger_11 floor_6)
	(destin passenger_12 floor_3)
	(destin passenger_13 floor_4)
	(destin passenger_14 floor_5)
)
(:goal (and
	(served passenger_0)
	(served passenger_1)
	(served passenger_2)
	(served passenger_3)
	(served passenger_4)
	(served passenger_5)
	(served passenger_6)
	(served passenger_7)
	(served passenger_8)
	(served passenger_9)
	(served passenger_10)
	(served passenger_11)
	(served passenger_12)
	(served passenger_13)
	(served passenger_14)
))
)