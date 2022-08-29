(define (problem initial1)
(:domain logistics)
(:objects
 apn1 - airplane
 apt1 apt2 - airport
 pos2 pos1 - location
 cit2 cit1 - city
 tru1 - truck
 obj obj2 - package)

(:init
  (at apn1 apt2)
  (at tru1 pos2)
  (at obj apt1)
  (at obj2 apt1)
  (in-city pos1 cit1)
  (in-city apt1 cit1)
  (in-city pos2 cit2)
  (in-city apt2 cit2))

(:goal (and))
)
