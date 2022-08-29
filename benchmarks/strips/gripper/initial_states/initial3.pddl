(define (problem gripper-x-1)
   (:domain gripper-typed)
   (:requirements :typing)
   (:objects rooma roomb - room
             ball0 - ball
left right - gripper)
   (:init (at-robby rooma)
          (free left)
          (carry ball0 right))
   (:goal ()))
