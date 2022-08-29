(define (problem initial2)
(:domain logistics)
(:objects
 apn1 apn2 - airplane
 apt1 apt2 - airport
 pos2 pos1 - location
 cit2 cit1 - city
 tru1 tru2 - truck
 obj obj2 - package)

(:init
  (at apn2 apt2)
  (at apn1 apt1)
  (at tru1 pos1)
  (at tru2 pos2)
  (at obj pos1)
  (at obj2 pos2)
  (in-city pos1 cit1)
  (in-city apt1 cit1)
  (in-city pos2 cit2)
  (in-city apt2 cit2))

(:goal (and))
)
