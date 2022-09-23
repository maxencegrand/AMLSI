(define (problem ei2)
   (:domain miconic)
   (:objects p0 p1 - passenger
             f0 f1 f2 - floor)
(:init

(above f0 f1)
(above f1 f2)

(origin p1 f0)
(origin p0 f0)

(destin p1 f2)
(destin p0 f2)

(lift-at f1)
)
(:goal())
)