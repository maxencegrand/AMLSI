(define (problem init1) (:domain spanner) (:objects 
     bob - man
 spanner1 spanner2 - spanner
     nut1 - nut
     location1 location2 location3 - location
     shed gate - location
    ) (:init 
    (at bob shed)
    (at spanner1 location2)
    (useable spanner1)
    (at spanner2 location2)
    (useable spanner2)
    (loose nut1)
    (at nut1 gate)
    (link shed location1)
    (link location3 gate)
    (link location1 location2)
    (link location2 location3)
) (:goal
  (and
   (tightened nut1)
)))