(define (problem initial1)
(:domain sokoban-typed)
(:objects
        down right left up - direction
        box0 - stone
        robot - player
        f0-0f f0-1f f0-2f f0-3f - location
        f1-0f f1-1f f1-2f f1-3f - location
)
(:init
(MOVE-DIR f0-0f f0-1f right)
(MOVE-DIR f0-0f f1-0f down)

(MOVE-DIR f0-1f f0-0f left)
(MOVE-DIR f0-1f f1-1f down)
(MOVE-DIR f0-1f f0-2f right)

(MOVE-DIR f0-2f f0-1f left)
(MOVE-DIR f0-2f f1-2f down)
(MOVE-DIR f0-2f f0-3f right)

(MOVE-DIR f0-3f f0-2f left)
(MOVE-DIR f0-3f f1-3f down)

(MOVE-DIR f1-0f f0-0f up)
(MOVE-DIR f1-0f f1-1f right)

(MOVE-DIR f1-1f f1-0f left)
(MOVE-DIR f1-1f f1-2f right)
(MOVE-DIR f1-1f f0-1f up)

(MOVE-DIR f1-2f f1-1f left)
(MOVE-DIR f1-2f f1-3f right)
(MOVE-DIR f1-2f f0-2f up)

(MOVE-DIR f1-3f f1-2f left)
(MOVE-DIR f1-3f f0-3f up)

(at box0 f1-1f)

(at robot f0-0f)

(clear f0-0f)
(clear f0-1f)
(clear f0-2f)
(clear f0-3f)
(clear f1-0f)
(clear f1-2f)
(clear f1-3f)
)
(:goal
()
)
)
