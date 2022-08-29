test.pddl


(define (problem BW-rand-5)
(:domain blocks)
(:objects
b1 - blocks
b2 - blocks
b3 - blocks
b4 - blocks
b5 - blocks
)
(:init
(on-table b1)
(on-table b2)
(on-table b3)
(on-table b4)
(on-table b5)
(clear b1)
(clear b2)
(clear b3)
(clear b4)
(clear b5)
)
(:goal
(and)
)
)


