(define (problem p0)
(:domain blocks)
(:objects
 b1 b2 b3 b4 b5 - block
)
(:init
(handempty)
(ontable b5)
(clear b3)
(on b2 b5)
(on b4 b2)
(on b3 b4)
(ontable b1)
(clear b1)
)
(:goal (and
(on b2 b4)
(on b3 b2)
(on b5 b3)
(on b1 b5)
))
)