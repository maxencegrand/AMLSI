(define (problem ZTRAVEL-25-70)
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
	plane21 - aircraft
	plane22 - aircraft
	plane23 - aircraft
	plane24 - aircraft
	plane25 - aircraft
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
	city25 - city
	city26 - city
	city27 - city
	city28 - city
	city29 - city
	fl0 - flevel
	fl1 - flevel
	fl2 - flevel
	fl3 - flevel
	fl4 - flevel
	fl5 - flevel
	fl6 - flevel
	)
(:init
	(at_aircraft plane1 city10)
	(fuel-level plane1 fl3)
	(at_aircraft plane2 city16)
	(fuel-level plane2 fl0)
	(at_aircraft plane3 city3)
	(fuel-level plane3 fl1)
	(at_aircraft plane4 city18)
	(fuel-level plane4 fl2)
	(at_aircraft plane5 city25)
	(fuel-level plane5 fl2)
	(at_aircraft plane6 city23)
	(fuel-level plane6 fl4)
	(at_aircraft plane7 city11)
	(fuel-level plane7 fl0)
	(at_aircraft plane8 city8)
	(fuel-level plane8 fl3)
	(at_aircraft plane9 city18)
	(fuel-level plane9 fl2)
	(at_aircraft plane10 city12)
	(fuel-level plane10 fl1)
	(at_aircraft plane11 city29)
	(fuel-level plane11 fl5)
	(at_aircraft plane12 city10)
	(fuel-level plane12 fl4)
	(at_aircraft plane13 city1)
	(fuel-level plane13 fl4)
	(at_aircraft plane14 city25)
	(fuel-level plane14 fl1)
	(at_aircraft plane15 city3)
	(fuel-level plane15 fl4)
	(at_aircraft plane16 city29)
	(fuel-level plane16 fl6)
	(at_aircraft plane17 city17)
	(fuel-level plane17 fl0)
	(at_aircraft plane18 city13)
	(fuel-level plane18 fl2)
	(at_aircraft plane19 city0)
	(fuel-level plane19 fl2)
	(at_aircraft plane20 city4)
	(fuel-level plane20 fl2)
	(at_aircraft plane21 city26)
	(fuel-level plane21 fl0)
	(at_aircraft plane22 city4)
	(fuel-level plane22 fl6)
	(at_aircraft plane23 city26)
	(fuel-level plane23 fl4)
	(at_aircraft plane24 city24)
	(fuel-level plane24 fl6)
	(at_aircraft plane25 city8)
	(fuel-level plane25 fl5)
	(at person1 city22)
	(at person2 city27)
	(at person3 city13)
	(at person4 city7)
	(at person5 city24)
	(at person6 city16)
	(at person7 city12)
	(at person8 city20)
	(at person9 city21)
	(at person10 city17)
	(at person11 city19)
	(at person12 city19)
	(at person13 city24)
	(at person14 city18)
	(at person15 city14)
	(at person16 city16)
	(at person17 city3)
	(at person18 city29)
	(at person19 city17)
	(at person20 city26)
	(at person21 city23)
	(at person22 city16)
	(at person23 city23)
	(at person24 city29)
	(at person25 city16)
	(at person26 city5)
	(at person27 city27)
	(at person28 city12)
	(at person29 city24)
	(at person30 city1)
	(at person31 city23)
	(at person32 city21)
	(at person33 city23)
	(at person34 city11)
	(at person35 city24)
	(at person36 city26)
	(at person37 city4)
	(at person38 city0)
	(at person39 city5)
	(at person40 city11)
	(at person41 city5)
	(at person42 city5)
	(at person43 city4)
	(at person44 city24)
	(at person45 city24)
	(at person46 city19)
	(at person47 city25)
	(at person48 city15)
	(at person49 city21)
	(at person50 city28)
	(at person51 city5)
	(at person52 city5)
	(at person53 city14)
	(at person54 city26)
	(at person55 city11)
	(at person56 city15)
	(at person57 city13)
	(at person58 city3)
	(at person59 city28)
	(at person60 city22)
	(at person61 city17)
	(at person62 city7)
	(at person63 city12)
	(at person64 city26)
	(at person65 city0)
	(at person66 city21)
	(at person67 city24)
	(at person68 city14)
	(at person69 city22)
	(at person70 city24)
	(next fl0 fl1)
	(next fl1 fl2)
	(next fl2 fl3)
	(next fl3 fl4)
	(next fl4 fl5)
	(next fl5 fl6)
)
(:goal (and
	(at_aircraft plane3 city5)
	(at_aircraft plane5 city7)
	(at_aircraft plane6 city11)
	(at_aircraft plane10 city21)
	(at_aircraft plane17 city23)
	(at person2 city14)
	(at person3 city28)
	(at person4 city11)
	(at person5 city2)
	(at person6 city2)
	(at person7 city23)
	(at person8 city26)
	(at person9 city25)
	(at person10 city14)
	(at person11 city6)
	(at person12 city3)
	(at person13 city16)
	(at person14 city24)
	(at person15 city18)
	(at person16 city5)
	(at person17 city18)
	(at person18 city9)
	(at person19 city0)
	(at person20 city17)
	(at person21 city7)
	(at person22 city27)
	(at person23 city21)
	(at person24 city10)
	(at person25 city24)
	(at person26 city10)
	(at person27 city14)
	(at person28 city14)
	(at person29 city2)
	(at person30 city29)
	(at person31 city22)
	(at person32 city9)
	(at person33 city3)
	(at person35 city18)
	(at person36 city24)
	(at person37 city21)
	(at person38 city4)
	(at person39 city29)
	(at person40 city1)
	(at person41 city24)
	(at person42 city15)
	(at person43 city8)
	(at person44 city20)
	(at person45 city15)
	(at person46 city12)
	(at person47 city17)
	(at person48 city17)
	(at person49 city23)
	(at person50 city5)
	(at person51 city10)
	(at person52 city16)
	(at person53 city20)
	(at person54 city14)
	(at person55 city9)
	(at person56 city29)
	(at person57 city24)
	(at person58 city10)
	(at person59 city1)
	(at person60 city0)
	(at person61 city5)
	(at person62 city11)
	(at person63 city25)
	(at person64 city22)
	(at person65 city4)
	(at person66 city19)
	(at person67 city2)
	(at person68 city6)
	(at person69 city4)
	(at person70 city26)
	))

)
