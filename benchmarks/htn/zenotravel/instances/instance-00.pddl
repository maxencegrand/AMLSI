(define (problem p0)
(:domain zenotravel)
(:objects
 f0 f1 f2 f3 f4 f5 - FLevel
 a1 - Aircraft
 c1 c2 c3 c4 c5 c6 c7 c8 c9 c10 - City
 p1 p2 p3 p4 p5 - Person
)
(:init
(min f0) (max f5)
(next f0 f1)
(next f1 f2)
(next f2 f3)
(next f3 f4)
(next f4 f5)
(fuel-level a1 f5) (at a1 c7)
(at p1 c3)
(at p2 c4)
(at p3 c2)
(at p4 c6)
(at p5 c3)
)
(:goal (and
(at a1 c7)
(at p1 c8)
(at p2 c5)
(at p3 c1)
(at p4 c4)
(at p5 c10)
))
)