(define (problem initial1)
(:domain sokoban-typed)
(:objects 
        down right left up - DIR
        box0 - BOX
        f0-0f f0-1f f0-2f f0-3f - LOC
        f1-0f f1-1f f1-2f f1-3f - LOC
)
(:init
(adjacent f0-0f f0-1f right)
(adjacent f0-0f f1-0f down)

(adjacent f0-1f f0-0f left)
(adjacent f0-1f f1-1f down)
(adjacent f0-1f f0-2f right)

(adjacent f0-2f f0-1f left)
(adjacent f0-2f f1-2f down)
(adjacent f0-2f f0-3f right)

(adjacent f0-3f f0-2f left)
(adjacent f0-3f f1-3f down)

(adjacent f1-0f f0-0f up)
(adjacent f1-0f f1-1f right)

(adjacent f1-1f f1-0f left)
(adjacent f1-1f f1-2f right)
(adjacent f1-1f f0-1f up)

(adjacent f1-2f f1-1f left)
(adjacent f1-2f f1-3f right)
(adjacent f1-2f f0-2f up)

(adjacent f1-3f f1-2f left)
(adjacent f1-3f f0-3f up)

(at box0 f0-2f) 

(at-robot f0-1f) 

(clear f0-0f)
(clear f0-1f)
(clear f0-3f)
(clear f1-1f)
(clear f1-2f)
(clear f1-3f)
)
(:goal
()
)
)
