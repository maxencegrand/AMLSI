(define (problem ZTRAVEL-20-70)
(:domain zeno-travel)
(:objects
	plane1 - aircraft
	plane2 - aircraft
	plane3 - aircraft
	plane4 - aircraft
	plane5 - aircraft
	plane6 - aircraft
	plane7 - aircraft
	plane8 - aircraft
	plane9 - aircraft
	plane10 - aircraft
	plane11 - aircraft
	plane12 - aircraft
	plane13 - aircraft
	plane14 - aircraft
	plane15 - aircraft
	plane16 - aircraft
	plane17 - aircraft
	plane18 - aircraft
	plane19 - aircraft
	plane20 - aircraft
	person1 - person
	person2 - person
	person3 - person
	person4 - person
	person5 - person
	person6 - person
	person7 - person
	person8 - person
	person9 - person
	person10 - person
	person11 - person
	person12 - person
	person13 - person
	person14 - person
	person15 - person
	person16 - person
	person17 - person
	person18 - person
	person19 - person
	person20 - person
	person21 - person
	person22 - person
	person23 - person
	person24 - person
	person25 - person
	person26 - person
	person27 - person
	person28 - person
	person29 - person
	person30 - person
	person31 - person
	person32 - person
	person33 - person
	person34 - person
	person35 - person
	person36 - person
	person37 - person
	person38 - person
	person39 - person
	person40 - person
	person41 - person
	person42 - person
	person43 - person
	person44 - person
	person45 - person
	person46 - person
	person47 - person
	person48 - person
	person49 - person
	person50 - person
	person51 - person
	person52 - person
	person53 - person
	person54 - person
	person55 - person
	person56 - person
	person57 - person
	person58 - person
	person59 - person
	person60 - person
	person61 - person
	person62 - person
	person63 - person
	person64 - person
	person65 - person
	person66 - person
	person67 - person
	person68 - person
	person69 - person
	person70 - person
	city0 - city
	city1 - city
	city2 - city
	city3 - city
	city4 - city
	city5 - city
	city6 - city
	city7 - city
	city8 - city
	city9 - city
	city10 - city
	city11 - city
	city12 - city
	city13 - city
	city14 - city
	city15 - city
	city16 - city
	city17 - city
	city18 - city
	city19 - city
	city20 - city
	city21 - city
	city22 - city
	city23 - city
	city24 - city
	fl0 - flevel
	fl1 - flevel
	fl2 - flevel
	fl3 - flevel
	fl4 - flevel
	fl5 - flevel
	fl6 - flevel
	)
(:init
	(at_aircraft plane1 city11)
	(fuel-level plane1 fl0)
	(at_aircraft plane2 city24)
	(fuel-level plane2 fl4)
	(at_aircraft plane3 city4)
	(fuel-level plane3 fl0)
	(at_aircraft plane4 city18)
	(fuel-level plane4 fl0)
	(at_aircraft plane5 city21)
	(fuel-level plane5 fl3)
	(at_aircraft plane6 city10)
	(fuel-level plane6 fl2)
	(at_aircraft plane7 city16)
	(fuel-level plane7 fl4)
	(at_aircraft plane8 city16)
	(fuel-level plane8 fl5)
	(at_aircraft plane9 city19)
	(fuel-level plane9 fl6)
	(at_aircraft plane10 city4)
	(fuel-level plane10 fl1)
	(at_aircraft plane11 city10)
	(fuel-level plane11 fl3)
	(at_aircraft plane12 city7)
	(fuel-level plane12 fl5)
	(at_aircraft plane13 city11)
	(fuel-level plane13 fl0)
	(at_aircraft plane14 city8)
	(fuel-level plane14 fl2)
	(at_aircraft plane15 city7)
	(fuel-level plane15 fl4)
	(at_aircraft plane16 city20)
	(fuel-level plane16 fl2)
	(at_aircraft plane17 city13)
	(fuel-level plane17 fl3)
	(at_aircraft plane18 city19)
	(fuel-level plane18 fl3)
	(at_aircraft plane19 city10)
	(fuel-level plane19 fl4)
	(at_aircraft plane20 city19)
	(fuel-level plane20 fl4)
	(at person1 city5)
	(at person2 city18)
	(at person3 city1)
	(at person4 city11)
	(at person5 city7)
	(at person6 city19)
	(at person7 city21)
	(at person8 city24)
	(at person9 city16)
	(at person10 city1)
	(at person11 city0)
	(at person12 city24)
	(at person13 city19)
	(at person14 city4)
	(at person15 city20)
	(at person16 city3)
	(at person17 city4)
	(at person18 city0)
	(at person19 city12)
	(at person20 city12)
	(at person21 city19)
	(at person22 city16)
	(at person23 city8)
	(at person24 city22)
	(at person25 city5)
	(at person26 city16)
	(at person27 city2)
	(at person28 city4)
	(at person29 city1)
	(at person30 city18)
	(at person31 city21)
	(at person32 city16)
	(at person33 city20)
	(at person34 city10)
	(at person35 city22)
	(at person36 city14)
	(at person37 city4)
	(at person38 city15)
	(at person39 city21)
	(at person40 city13)
	(at person41 city17)
	(at person42 city10)
	(at person43 city15)
	(at person44 city3)
	(at person45 city2)
	(at person46 city9)
	(at person47 city1)
	(at person48 city9)
	(at person49 city6)
	(at person50 city5)
	(at person51 city19)
	(at person52 city18)
	(at person53 city17)
	(at person54 city1)
	(at person55 city18)
	(at person56 city21)
	(at person57 city4)
	(at person58 city18)
	(at person59 city16)
	(at person60 city2)
	(at person61 city19)
	(at person62 city13)
	(at person63 city2)
	(at person64 city5)
	(at person65 city8)
	(at person66 city8)
	(at person67 city21)
	(at person68 city0)
	(at person69 city11)
	(at person70 city1)
	(next fl0 fl1)
	(next fl1 fl2)
	(next fl2 fl3)
	(next fl3 fl4)
	(next fl4 fl5)
	(next fl5 fl6)
)
(:goal (and
	(at_aircraft plane1 city16)
	(at_aircraft plane6 city15)
	(at_aircraft plane11 city23)
	(at_aircraft plane14 city19)
	(at_aircraft plane15 city22)
	(at_aircraft plane20 city3)
	(at person1 city15)
	(at person2 city20)
	(at person3 city20)
	(at person4 city20)
	(at person5 city2)
	(at person6 city0)
	(at person7 city15)
	(at person8 city20)
	(at person9 city19)
	(at person10 city11)
	(at person11 city16)
	(at person12 city9)
	(at person13 city10)
	(at person14 city22)
	(at person15 city4)
	(at person16 city24)
	(at person17 city20)
	(at person18 city19)
	(at person19 city10)
	(at person20 city12)
	(at person21 city13)
	(at person22 city12)
	(at person23 city7)
	(at person24 city11)
	(at person25 city7)
	(at person26 city10)
	(at person27 city14)
	(at person28 city14)
	(at person29 city2)
	(at person30 city14)
	(at person31 city9)
	(at person32 city0)
	(at person33 city9)
	(at person34 city7)
	(at person35 city12)
	(at person36 city3)
	(at person37 city6)
	(at person38 city10)
	(at person39 city11)
	(at person40 city5)
	(at person41 city2)
	(at person42 city19)
	(at person43 city14)
	(at person44 city24)
	(at person45 city22)
	(at person46 city11)
	(at person47 city16)
	(at person48 city7)
	(at person49 city3)
	(at person50 city16)
	(at person51 city9)
	(at person52 city19)
	(at person53 city9)
	(at person54 city13)
	(at person56 city0)
	(at person57 city2)
	(at person58 city11)
	(at person59 city17)
	(at person60 city23)
	(at person61 city18)
	(at person62 city11)
	(at person63 city4)
	(at person64 city6)
	(at person65 city24)
	(at person66 city21)
	(at person67 city0)
	(at person68 city18)
	(at person69 city10)
	(at person70 city13)
	))

)
