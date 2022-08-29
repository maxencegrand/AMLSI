(define (problem p2)
(:domain childsnack)
(:objects
child1 child2 child3 child4 child5 child6 - child
sand1 sand2 sand3 sand4 sand5 sand6 - sandwich
bread1 bread2 bread3 bread4 bread5 bread6 - bread-portion
content1 content2 content3 content4 content5 content6 - content-portion
tray1 - tray
table1 kitchen - place
)
(:init
(at tray1 kitchen)
(at_kitchen_bread bread1)
(at_kitchen_bread bread2)
(at_kitchen_bread bread3)
(at_kitchen_bread bread4)
(at_kitchen_bread bread5)
(at_kitchen_bread bread6)
(at_kitchen_content content1)
(at_kitchen_content content2)
(at_kitchen_content content3)
(at_kitchen_content content4)
(at_kitchen_content content5)
(at_kitchen_content content6)
(not_exist sand1)
(not_exist sand2)
(not_exist sand3)
(not_exist sand4)
(not_exist sand5)
(not_exist sand6)
(waiting child1 table1)
(waiting child2 table1)
(waiting child3 table1)
(waiting child4 table1)
(waiting child5 table1)
(waiting child6 table1)
(no_gluten_bread bread1)
(no_gluten_bread bread2)
(no_gluten_bread bread3)
(no_gluten_bread bread4)
(no_gluten_bread bread5)
(no_gluten_bread bread6)
(no_gluten_content content1)
(no_gluten_content content2)
(no_gluten_content content3)
(no_gluten_content content4)
(no_gluten_content content5)
(no_gluten_content content6)
(allergic_gluten child1)
(allergic_gluten child2)
(allergic_gluten child3)
(allergic_gluten child4)
(allergic_gluten child5)
(allergic_gluten child6)
)
(:goal (and
(served child1)
(served child2)
(served child3)
(served child4)
(served child5)
(served child6)
))
)
