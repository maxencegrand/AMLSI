test.pddl


(define (problem BW-rand-4)
(:domain blocks)
(:objects
b1 - blocks
b2 - blocks
b3 - blocks
b4 - blocks
)
(:init
(on-table b1)
(on-table b2)
(on-table b3)
(on-table b4)
(clear b1)
(clear b2)
(clear b3)
(clear b4)
)
(:goal
(and)
)
)


