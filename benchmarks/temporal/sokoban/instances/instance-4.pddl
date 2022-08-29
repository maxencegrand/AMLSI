
;In guarantee_solution():
;Planning path: 0
;In plan_path():
;In put_box: 
;length of path: 4
;In set_wall_mask(): 
;In clear_box_neighborhood(): 
;In enforce_direction_constraints(): 
;In setup_walls(): 
;Grid: 

; 8 8 8 8 8 
; 8 8 8 8 8 
; 8 8 8 8 8 
; 8 2 8 8 8 
; 0 8 3 3 1 

;Wall mask: 

; 0 1 1 0 0 
; 0 1 1 0 0 
; 0 0 1 0 0 
; 0 0 0 0 0 
; 0 0 1 0 0 




(define (problem typed-sokoban-grid5-boxes1-walls1)
(:domain sokoban-temporal)
(:objects 
        up down left right - direction
        player - player
        box0 - stone
        f0-0f f0-1f f0-2f f0-3f f0-4f 
        f1-0f f1-1f f1-2f f1-3f f1-4f 
        f2-0f f2-1f f2-2f f2-3f f2-4f 
        f3-0f f3-1f f3-2f f3-3f f3-4f 
        f4-0f f4-1f f4-2f f4-3f f4-4f  - location
)
(:init
(MOVE-DIR f0-0f f0-1f right)
(MOVE-DIR f0-0f f1-0f down)
(MOVE-DIR f0-1f f0-0f left)
(MOVE-DIR f0-1f f0-2f right)
(MOVE-DIR f0-1f f1-1f down)
(MOVE-DIR f0-2f f0-1f left)
(MOVE-DIR f0-2f f0-3f right)
(MOVE-DIR f0-2f f1-2f down)
(MOVE-DIR f0-3f f0-2f left)
(MOVE-DIR f0-3f f0-4f right)
(MOVE-DIR f0-3f f1-3f down)
(MOVE-DIR f0-4f f0-3f left)
(MOVE-DIR f0-4f f1-4f down)
(MOVE-DIR f1-0f f1-1f right)
(MOVE-DIR f1-0f f0-0f up)
(MOVE-DIR f1-0f f2-0f down)
(MOVE-DIR f1-1f f1-0f left)
(MOVE-DIR f1-1f f1-2f right)
(MOVE-DIR f1-1f f0-1f up)
(MOVE-DIR f1-1f f2-1f down)
(MOVE-DIR f1-2f f1-1f left)
(MOVE-DIR f1-2f f1-3f right)
(MOVE-DIR f1-2f f0-2f up)
(MOVE-DIR f1-2f f2-2f down)
(MOVE-DIR f1-3f f1-2f left)
(MOVE-DIR f1-3f f1-4f right)
(MOVE-DIR f1-3f f0-3f up)
(MOVE-DIR f1-3f f2-3f down)
(MOVE-DIR f1-4f f1-3f left)
(MOVE-DIR f1-4f f0-4f up)
(MOVE-DIR f1-4f f2-4f down)
(MOVE-DIR f2-0f f2-1f right)
(MOVE-DIR f2-0f f1-0f up)
(MOVE-DIR f2-0f f3-0f down)
(MOVE-DIR f2-1f f2-0f left)
(MOVE-DIR f2-1f f2-2f right)
(MOVE-DIR f2-1f f1-1f up)
(MOVE-DIR f2-1f f3-1f down)
(MOVE-DIR f2-2f f2-1f left)
(MOVE-DIR f2-2f f2-3f right)
(MOVE-DIR f2-2f f1-2f up)
(MOVE-DIR f2-2f f3-2f down)
(MOVE-DIR f2-3f f2-2f left)
(MOVE-DIR f2-3f f2-4f right)
(MOVE-DIR f2-3f f1-3f up)
(MOVE-DIR f2-3f f3-3f down)
(MOVE-DIR f2-4f f2-3f left)
(MOVE-DIR f2-4f f1-4f up)
(MOVE-DIR f2-4f f3-4f down)
(MOVE-DIR f3-0f f3-1f right)
(MOVE-DIR f3-0f f2-0f up)
(MOVE-DIR f3-0f f4-0f down)
(MOVE-DIR f3-1f f3-0f left)
(MOVE-DIR f3-1f f3-2f right)
(MOVE-DIR f3-1f f2-1f up)
(MOVE-DIR f3-1f f4-1f down)
(MOVE-DIR f3-2f f3-1f left)
(MOVE-DIR f3-2f f3-3f right)
(MOVE-DIR f3-2f f2-2f up)
(MOVE-DIR f3-2f f4-2f down)
(MOVE-DIR f3-3f f3-2f left)
(MOVE-DIR f3-3f f3-4f right)
(MOVE-DIR f3-3f f2-3f up)
(MOVE-DIR f3-3f f4-3f down)
(MOVE-DIR f3-4f f3-3f left)
(MOVE-DIR f3-4f f2-4f up)
(MOVE-DIR f3-4f f4-4f down)
(MOVE-DIR f4-0f f4-1f right)
(MOVE-DIR f4-0f f3-0f up)
(MOVE-DIR f4-1f f4-0f left)
(MOVE-DIR f4-1f f4-2f right)
(MOVE-DIR f4-1f f3-1f up)
(MOVE-DIR f4-2f f4-1f left)
(MOVE-DIR f4-2f f4-3f right)
(MOVE-DIR f4-2f f3-2f up)
(MOVE-DIR f4-3f f4-2f left)
(MOVE-DIR f4-3f f4-4f right)
(MOVE-DIR f4-3f f3-3f up)
(MOVE-DIR f4-4f f4-3f left)
(MOVE-DIR f4-4f f3-4f up)
(at box0 f3-1f) 
(clear f0-0f) 
(clear f0-1f) 
(clear f0-2f) 
(clear f0-3f) 
(clear f0-4f) 
(clear f1-0f) 
(clear f1-1f) 
(clear f1-2f) 
(clear f1-3f) 
(clear f1-4f) 
(clear f2-0f) 
(clear f2-1f) 
(clear f2-2f) 
(clear f2-3f) 
(clear f2-4f) 
(clear f3-0f) 
(clear f3-2f) 
(clear f3-3f) 
(clear f3-4f) 
(at player f4-0f) 
(clear f4-1f) 
(clear f4-4f) 
)
(:goal
(and
(at box0 f4-4f) 
)
)
)


;clearing memory: 
