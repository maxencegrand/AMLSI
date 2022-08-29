(define (problem p2)
(:domain zenotravel)
(:objects
 f0 f1 f2 - FLevel
 a1 - Aircraft
 c1 c2 c3 c4 c5 - City
 p1 p2 p3 p4 p5 p6 p7 - Person
)
(:init
(min f0) (max f2)
(next f0 f1)
(not-min f1)
(next f1 f2)
(not-min f2)
(fuel-level a1 f2) (at a1 c1)
(at p1 c3)
(at p2 c5)
(at p3 c5)
(at p4 c2)
(at p5 c1)
(at p6 c1)
(at p7 c4)
)
(:goal (and
(at a1 c1)
(at p1 c4)
(at p2 c4)
(at p3 c4)
(at p4 c5)
(at p5 c4)
(at p6 c4)
(at p7 c4)
))
)