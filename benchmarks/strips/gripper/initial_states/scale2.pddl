(define (problem gripper-x-1)
   (:domain gripper-typed)
   (:requirements :typing)
   (:objects rooma roomb roomc - room
             ball0 ball1 - ball
	     left right - gripper)
   (:init (at-robby rooma)
          (free left)
          (free right)
          (at ball0 rooma)
          (at ball1 roomb))
   (:goal ()))