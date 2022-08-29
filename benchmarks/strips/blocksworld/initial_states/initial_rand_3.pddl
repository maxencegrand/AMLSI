test.pddl


(define (problem BW-rand-3)
(:domain blocks)
(:objects
b1 - blocks
b2 - blocks
b3 - blocks
)
(:init
(on-table b1)
(on-table b2)
(on-table b3)
(clear b1)
(clear b2)
(clear b3)
)
(:goal
(and)
)
)


