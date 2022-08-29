(define (problem HANOI_4)
(:domain HANOI)
(:objects D1 D2 D3 - disk
	  C1 C2 C3 - case
)
(:init
	(handempty)
	(bigger D1 D2)
	(bigger D1 D3)
	(bigger D2 D3)
	(on_case D1 C1)
	(on_case D2 C2)
    (on_disk D3 D2)
    (clear_case C3)
	(clear_disk D1)
	(clear_disk D3)
)
(:goal ()
))
