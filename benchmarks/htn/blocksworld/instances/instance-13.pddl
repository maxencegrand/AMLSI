(define (problem p13)
(:domain blocks)
(:objects
 b1 b2 b3 b4 b5 b6 b7 b8 b9 b10 b11 b12 b13 b14 b15 b16 b17 b18 b19 b20 b21 b22 b23 b24 b25 b26 b27 b28 b29 b30 - block
)
(:init
(handempty)
(ontable b11)
(clear b16)
(on b4 b11)
(on b18 b4)
(on b17 b18)
(on b13 b17)
(on b10 b13)
(on b9 b10)
(on b23 b9)
(on b24 b23)
(on b5 b24)
(on b6 b5)
(on b25 b6)
(on b12 b25)
(on b2 b12)
(on b21 b2)
(on b16 b21)
(ontable b22)
(clear b8)
(on b19 b22)
(on b15 b19)
(on b27 b15)
(on b28 b27)
(on b7 b28)
(on b14 b7)
(on b29 b14)
(on b30 b29)
(on b8 b30)
(ontable b20)
(clear b1)
(on b1 b20)
(ontable b3)
(clear b26)
(on b26 b3)
)
(:goal (and
(on b12 b9)
(on b30 b12)
(on b20 b30)
(on b21 b20)
(on b14 b21)
(on b7 b14)
(on b4 b7)
(on b11 b4)
(on b27 b11)
(on b17 b27)
(on b28 b17)
(on b22 b28)
(on b18 b22)
(on b2 b18)
(on b10 b2)
(on b26 b10)
(on b13 b26)
(on b16 b13)
(on b15 b16)
(on b25 b15)
(on b29 b25)
(on b6 b29)
(on b23 b6)
(on b19 b23)
(on b5 b19)
(on b8 b5)
(on b1 b8)
(on b3 b1)
(on b24 b3)
))
)