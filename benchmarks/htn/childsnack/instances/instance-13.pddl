(define (problem p13)
(:domain childsnack)
(:objects
child1 - child
sand1 - sandwich
bread1 - bread-portion
content1 - content-portion
tray1 - tray
table1 kitchen - place
)
(:init
(at tray1 kitchen)
(at_kitchen_bread bread1)
(at_kitchen_content content1)
(not_exist sand1)
(waiting child1 table1)
(no_gluten_bread bread1)
(no_gluten_content content1)
(allergic_gluten child1)
)
(:goal (and
(served child1)
))
)
