(define
	(problem pfile06)
	(:domain  domain_htn)
	(:objects
		package_2 - package
		package_1 - package
		package_3 - package
		package_0 - package
		package_4 - package
		capacity_0 - capacity_number
		capacity_1 - capacity_number
		capacity_2 - capacity_number
		city_loc_0 - location
		city_loc_2 - location
		city_loc_1 - location
		city_loc_3 - location
		city_loc_4 - location
		truck_0 - vehicle
	)
	(:init
		(capacity_predecessor capacity_0 capacity_1)
		(capacity_predecessor capacity_1 capacity_2)
		(road city_loc_0 city_loc_1)
		(road city_loc_1 city_loc_0)
		(road city_loc_1 city_loc_1)
		(road city_loc_1 city_loc_2)
		(road city_loc_1 city_loc_3)
		(road city_loc_2 city_loc_1)
		(road city_loc_2 city_loc_4)
		(road city_loc_3 city_loc_1)
		(road city_loc_3 city_loc_3)
		(road city_loc_4 city_loc_2)
		(at package_0 city_loc_3)
		(at package_1 city_loc_3)
		(at package_2 city_loc_2)
		(at package_3 city_loc_3)
		(at package_4 city_loc_1)
		(at truck_0 city_loc_4)
		(capacity truck_0 capacity_2)
	)
)
