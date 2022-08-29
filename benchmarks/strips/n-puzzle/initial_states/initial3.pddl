(define (problem ei1)
  (:domain n-puzzle-typed)
  (:requirements :typing)
  (:objects p_1_1 p_1_2 p_2_1 p_2_2 - position t_1 t_2 t_3 - tile)
  (:init
    (at t_3 p_1_2)
    (empty p_1_1)
    (at t_2 p_2_1)
    (at t_1 p_2_2)
    (neighbor p_1_1 p_1_2)
    (neighbor p_1_2 p_1_1)
    (neighbor p_2_1 p_2_2)
    (neighbor p_2_2 p_2_1)
    (neighbor p_1_1 p_2_1)
    (neighbor p_2_1 p_1_1)
    (neighbor p_1_2 p_2_2)
    (neighbor p_2_2 p_1_2))
  (:goal ()))
