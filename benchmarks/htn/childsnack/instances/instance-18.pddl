(define (problem p18)
(:domain childsnack)
(:objects
child1 child2 child3 child4 child5 child6 child7 child8 child9 child10 child11 child12 child13 - child
sand1 sand2 sand3 sand4 sand5 sand6 sand7 sand8 sand9 sand10 sand11 sand12 sand13 - sandwich
bread1 bread2 bread3 bread4 bread5 bread6 bread7 bread8 bread9 bread10 bread11 bread12 bread13 - bread-portion
content1 content2 content3 content4 content5 content6 content7 content8 content9 content10 content11 content12 content13 - content-portion
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
(at_kitchen_bread bread7)
(at_kitchen_bread bread8)
(at_kitchen_bread bread9)
(at_kitchen_bread bread10)
(at_kitchen_bread bread11)
(at_kitchen_bread bread12)
(at_kitchen_bread bread13)
(at_kitchen_content content1)
(at_kitchen_content content2)
(at_kitchen_content content3)
(at_kitchen_content content4)
(at_kitchen_content content5)
(at_kitchen_content content6)
(at_kitchen_content content7)
(at_kitchen_content content8)
(at_kitchen_content content9)
(at_kitchen_content content10)
(at_kitchen_content content11)
(at_kitchen_content content12)
(at_kitchen_content content13)
(not_exist sand1)
(not_exist sand2)
(not_exist sand3)
(not_exist sand4)
(not_exist sand5)
(not_exist sand6)
(not_exist sand7)
(not_exist sand8)
(not_exist sand9)
(not_exist sand10)
(not_exist sand11)
(not_exist sand12)
(not_exist sand13)
(waiting child1 table1)
(waiting child2 table1)
(waiting child3 table1)
(waiting child4 table1)
(waiting child5 table1)
(waiting child6 table1)
(waiting child7 table1)
(waiting child8 table1)
(waiting child9 table1)
(waiting child10 table1)
(waiting child11 table1)
(waiting child12 table1)
(waiting child13 table1)
(no_gluten_bread bread1)
(no_gluten_bread bread2)
(no_gluten_bread bread3)
(no_gluten_content content1)
(no_gluten_content content2)
(no_gluten_content content3)
(allergic_gluten child1)
(allergic_gluten child2)
(allergic_gluten child3)
(not_allergic_gluten child4)
(not_allergic_gluten child5)
(not_allergic_gluten child6)
(not_allergic_gluten child7)
(not_allergic_gluten child8)
(not_allergic_gluten child9)
(not_allergic_gluten child10)
(not_allergic_gluten child11)
(not_allergic_gluten child12)
(not_allergic_gluten child13)
)
(:goal (and
(served child1)
(served child2)
(served child3)
(served child4)
(served child5)
(served child6)
(served child7)
(served child8)
(served child9)
(served child10)
(served child11)
(served child12)
(served child13)
))
)
