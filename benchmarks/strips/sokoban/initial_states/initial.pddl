(define (problem sokoban-bootstrap-n05-b02-w04-01-typed)
(:domain sokoban-typed)
(:objects 
        up down left right - DIR
        box0 - BOX
        f0-0f f0-1f f0-2f 
        f1-0f f1-1f f1-2f 
        f2-0f f2-1f f2-2f - LOC
)
(:init
(adjacent f0-0f f0-1f right)
(adjacent f0-0f f1-0f down)

(adjacent f0-1f f0-0f left)
(adjacent f0-1f f1-1f down)
(adjacent f0-1f f0-2f right)

(adjacent f2-1f f2-0f left)
(adjacent f2-1f f1-1f up)
(adjacent f2-1f f2-2f right)

(adjacent f0-2f f0-1f left)
(adjacent f0-2f f1-2f down)

(adjacent f1-0f f0-0f up)
(adjacent f1-0f f1-1f right)
(adjacent f1-0f f2-0f down)

(adjacent f1-2f f0-2f up)
(adjacent f1-2f f1-1f left)
(adjacent f1-2f f2-2f down)

(adjacent f2-0f f2-1f right)
(adjacent f2-0f f1-0f up)

(adjacent f2-2f f2-1f left)
(adjacent f2-2f f1-2f up)

(adjacent f1-1f f1-0f left)
(adjacent f1-1f f2-1f down)
(adjacent f1-1f f1-2f right)
(adjacent f1-1f f0-1f up)

(at box0 f1-1f) 

(at-robot f0-0f) 

(clear f0-0f) 
(clear f0-1f) 
(clear f0-2f) 
(clear f1-0f) 
(clear f1-2f) 
(clear f2-0f) 
(clear f2-1f) 
(clear f2-2f))
(:goal
()
)
)
