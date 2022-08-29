;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Hanoi Tower
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(define (domain HANOI)
	(:requirements :strips :typing :negative-preconditions)
	(:types disk case)
	(:predicates
		(on_disk ?d1 - disk ?d2 - disk)
		(bigger ?d1 - disk ?d2 - disk)
		(on_case ?d1 - disk ?c - case)
		(clear_disk ?d - disk)
		(clear_case ?c - case)
		(handempty)
		(holding ?d - disk)
	)

	(:action pick_up
		 :parameters (?d - disk ?c - case)
	     	 :precondition (and (clear_disk ?d) (on_case ?d ?c) (handempty))
	     	 :effect
	     	 (and (not (on_case ?d ?c))
		      (clear_case ?c)
		      (not (clear_disk ?d))
		      (not (handempty))
		      (holding ?d)
		 )
	)
	(:action unstack
 		 :parameters (?d - disk ?d2 - disk)
	     	 :precondition (and (clear_disk ?d) (on_disk ?d ?d2) (handempty))
	     	 :effect
	     	 (and (not (on_disk ?d ?d2))
		      (not (clear_disk ?d))
		      	   (clear_disk ?d2)
		      (not (handempty))
		      (holding ?d)
		 )
	)

	(:action put_down
		 :parameters (?d - disk ?c - case)
		 :precondition (and (clear_case ?c) (holding ?d) )
		 :effect
		 (and (not (clear_case ?c))
		      (not (holding ?d))
		      (clear_disk ?d)
		      (on_case ?d ?c)
		      (handempty)
		 )
	)

	(:action stack
		 :parameters (?d1 - disk ?d2 - disk)
		 :precondition (and (clear_disk ?d2) (holding ?d1) (bigger ?d2 ?d1))
		 :effect
		 (and (not (clear_disk ?d2))
		      (not (holding ?d1))
		      (on_disk ?d1 ?d2)
		      (clear_disk ?d1)
		      (handempty)
		 )
	)
)
