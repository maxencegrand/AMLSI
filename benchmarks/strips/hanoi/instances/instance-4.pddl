(define (problem HANOI_4)
(:domain HANOI)
(:objects D1 D2 D3 D4 D5 - disk
	  C1 C2 C3 - case
)
(:init
	(handempty)
	(bigger D1 D2)
	(bigger D1 D3)
	(bigger D1 D4)
	(bigger D1 D5)
	(bigger D2 D3)
	(bigger D2 D4)
	(bigger D2 D5)
	(bigger D3 D4)
	(bigger D3 D5)
	(bigger D4 D5)
	(clear_case C1)
	(clear_case C2)
	(on_case D1 C3)
	(on_disk D2 D1)
	(on_disk D3 D2)
	(on_disk D4 D3)
	(on_disk D5 D4)
	(clear_disk D5)
)
(:goal
	(and (on_case D1 C1)
	     (on_disk D2 D1)
	     (on_disk D3 D2)
	     (on_case D4 C2)
	     (on_case D5 C3)	
	     (handempty)
	)
))