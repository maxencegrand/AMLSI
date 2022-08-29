(define (problem init1)
 (:domain spanner)
 (:objects 
     bob - man
     loc1 loc2 loc3 - location
     nut - nut
     span - spanner
    )
 (:init
    (at bob loc1)
    (at span loc2)
    (at nut loc3)
    (loose nut)
    (useable span)
    (link loc1 loc2)
    (link loc2 loc1)
    (link loc3 loc2)
    (link loc2 loc3)
    )
 (:goal
  (and)
 )
)
