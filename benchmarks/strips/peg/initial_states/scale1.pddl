(define (problem pegsolitaire-sequential-036)
(:domain pegsolitaire-sequential)
(:objects
pos_0_0 - location
pos_0_1 - location
pos_0_2 - location
pos_0_3 - location
pos_1_0 - location
pos_1_1 - location
pos_1_2 - location
pos_1_3 - location
pos_2_0 - location
pos_2_1 - location
pos_2_2 - location
pos_2_3 - location
pos_3_0 - location
pos_3_1 - location
pos_3_2 - location
pos_3_3 - location
)
(:init
(move-ended)(IN-LINE pos_0_0 pos_1_0 pos_2_0)
(IN-LINE pos_2_0 pos_1_0 pos_0_0)
(IN-LINE pos_0_0 pos_0_1 pos_0_2)
(IN-LINE pos_0_2 pos_0_1 pos_0_0)
(IN-LINE pos_0_1 pos_1_1 pos_2_1)
(IN-LINE pos_2_1 pos_1_1 pos_0_1)
(IN-LINE pos_0_1 pos_0_2 pos_0_3)
(IN-LINE pos_0_3 pos_0_2 pos_0_1)
(IN-LINE pos_0_2 pos_1_2 pos_2_2)
(IN-LINE pos_2_2 pos_1_2 pos_0_2)
(IN-LINE pos_0_3 pos_1_3 pos_2_3)
(IN-LINE pos_2_3 pos_1_3 pos_0_3)
(IN-LINE pos_1_0 pos_2_0 pos_3_0)
(IN-LINE pos_3_0 pos_2_0 pos_1_0)
(IN-LINE pos_1_0 pos_1_1 pos_1_2)
(IN-LINE pos_1_2 pos_1_1 pos_1_0)
(IN-LINE pos_1_1 pos_2_1 pos_3_1)
(IN-LINE pos_3_1 pos_2_1 pos_1_1)
(IN-LINE pos_1_1 pos_1_2 pos_1_3)
(IN-LINE pos_1_3 pos_1_2 pos_1_1)
(IN-LINE pos_1_2 pos_2_2 pos_3_2)
(IN-LINE pos_3_2 pos_2_2 pos_1_2)
(IN-LINE pos_1_3 pos_2_3 pos_3_3)
(IN-LINE pos_3_3 pos_2_3 pos_1_3)
(IN-LINE pos_2_0 pos_2_1 pos_2_2)
(IN-LINE pos_2_2 pos_2_1 pos_2_0)
(IN-LINE pos_2_1 pos_2_2 pos_2_3)
(IN-LINE pos_2_3 pos_2_2 pos_2_1)
(IN-LINE pos_3_0 pos_3_1 pos_3_2)
(IN-LINE pos_3_2 pos_3_1 pos_3_0)
(IN-LINE pos_3_1 pos_3_2 pos_3_3)
(IN-LINE pos_3_3 pos_3_2 pos_3_1)

(FREE pos_0_0)
(occupied pos_0_1)
(occupied pos_0_2)
(occupied pos_0_3)

(FREE pos_1_0)
(FREE pos_1_1)
(FREE pos_1_2)
(FREE pos_1_3)

(FREE pos_2_0)
(FREE pos_2_1)
(FREE pos_2_2)
(FREE pos_2_3)

(FREE pos_3_0)
(FREE pos_3_1)
(FREE pos_3_2)
(FREE pos_3_3)

)
(:goal ())
)