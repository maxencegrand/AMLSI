(define (problem HANOI_4)
(:domain HANOI)
(:objects D1 D2 - disk
	  C1 C2 C3 - case
)
(:init
	(handempty)
	(bigger D1 D2)
	(clear_case C1)
	(clear_case C2)
	(on_case D1 C3)
	(on_disk D2 D1)
	(clear_disk D2)
)
(:goal ()
))