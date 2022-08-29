(define (problem gripper-x-1)
   (:domain gripper-typed)
   (:requirements :typing)
   (:objects rooma roomb roomc roomd - room
             ball0 ball1 ball2 - ball
	     left right - gripper)
   (:init (at-robby rooma)
          (free left)
          (free right)
          (at ball0 rooma)
          (at ball1 roomb)
          (at ball2 roomc))
   (:goal ()))