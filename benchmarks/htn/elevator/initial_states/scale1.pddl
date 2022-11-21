(define (problem is)
(:domain miconic)
(:objects
floor_0 - floor
floor_1 - floor
passenger_0 - passenger
)
(:init
	(above floor_0 floor_1)
	(lift-at floor_0)
	(origin passenger_0 floor_1)
	(destin passenger_0 floor_0)
)
(:goal ())
)