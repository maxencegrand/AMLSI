 (define (problem DLOG-20-20-20)
	(:domain driverlog)
	(:objects
	driver - driver
	truck1 - truck
	truck2 - truck
	package1 - obj
	package2 - obj
	package3 - obj
	s0 - location
	s1 - location
	)
	(:init
	(at driver s0)
	(at truck1 s0)
	(empty truck1)
	(at truck2 s1)
	(empty truck2)
	(at package1 s0)
	(at package2 s1)
	(at package3 s1)
	(path s0 s1)
	(path s1 s0)
	(link s1 s0)
	(link s0 s1)
)
	(:goal (and
	(at driver s0)
	(at truck1 s0)
	(at truck2 s0)
	(at package1 s0)
	(at package2 s0)
	(at package3 s0)
	))

(:metric minimize (total-time)))
