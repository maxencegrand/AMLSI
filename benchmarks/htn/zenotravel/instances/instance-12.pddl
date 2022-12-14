(define (problem p12)
(:domain zenotravel)
(:objects
 f0 f1 f2 - FLevel
 a1 a2 a3 - Aircraft
 c1 c2 c3 c4 c5 - City
 p1 p2 p3 p4 p5 p6 p7 p8 p9 p10 p11 p12 p13 p14 p15 p16 p17 p18 p19 p20 - Person
)
(:init
(min f0) (max f2)
(next f0 f1)
(not-min f1)
(next f1 f2)
(not-min f2)
(fuel-level a1 f2) (at a1 c2)
(fuel-level a2 f2) (at a2 c1)
(fuel-level a3 f2) (at a3 c5)
(at p1 c3)
(at p2 c5)
(at p3 c2)
(at p4 c3)
(at p5 c3)
(at p6 c1)
(at p7 c2)
(at p8 c2)
(at p9 c5)
(at p10 c2)
(at p11 c4)
(at p12 c3)
(at p13 c5)
(at p14 c4)
(at p15 c2)
(at p16 c1)
(at p17 c4)
(at p18 c3)
(at p19 c5)
(at p20 c5)
)
(:goal (and
(at a1 c2)
(at a2 c1)
(at a3 c5)
(at p1 c3)
(at p2 c4)
(at p3 c4)
(at p4 c3)
(at p5 c2)
(at p6 c2)
(at p7 c1)
(at p8 c3)
(at p9 c3)
(at p10 c2)
(at p11 c1)
(at p12 c3)
(at p13 c1)
(at p14 c3)
(at p15 c3)
(at p16 c3)
(at p17 c5)
(at p18 c2)
(at p19 c2)
(at p20 c5)
))
)