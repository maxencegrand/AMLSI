(define (problem gripper-x-1)
   (:domain gripper-typed)
   (:requirements :typing)
   (:objects r1 r2 - room
             b1 b2 - ball
	         l r - gripper)
   (:init (at-robby r1)
          (free l)
          (free r)
          (at b1 r1)
          (at b2 r1))
   (:goal ()))
