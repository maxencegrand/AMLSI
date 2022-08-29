(define (problem p20)
(:domain zenotravel)
(:objects
 f0 f1 f2 - FLevel
 a1 - Aircraft
 c1 c2 c3 c4 c5 c6 c7 c8 c9 c10 - City
 p1 - Person
)
(:init
(min f0) (max f2)
(next f0 f1)
(not-min f1)
(next f1 f2)
(not-min f2)
(fuel-level a1 f2) (at a1 c4)
(at p1 c7)
)
(:goal (and
(at a1 c4)
(at p1 c5)
))
)