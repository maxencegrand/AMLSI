(define (problem ei3)
   (:domain miconic)
   (:objects p0 p1 - passenger
             f0 f1 f2 - floor)
(:init

(above f0 f1)
(above f1 f2)

(origin p1 f2)
(origin p0 f0)

(destin p1 f1)
(destin p0 f1)

(lift-at f1)
)
(:goal())
)