(define (problem HANOI_20)
(:domain HANOI)
(:objects D1 D2 D3 D4 D5 D6 D7 D8 D9 D10 - disk
	  C1 C2 C3 - case
)
(:init
    (handempty)

    (bigger D1 D2)
    (bigger D1 D3)
    (bigger D1 D4)
    (bigger D1 D5)
    (bigger D1 D6)
    (bigger D1 D7)
    (bigger D1 D8)
    (bigger D1 D9)
    (bigger D1 D10)
    (bigger D2 D3)
    (bigger D2 D4)
    (bigger D2 D5)
    (bigger D2 D6)
    (bigger D2 D7)
    (bigger D2 D8)
    (bigger D2 D9)
    (bigger D2 D10)
    (bigger D3 D4)
    (bigger D3 D5)
    (bigger D3 D5)
    (bigger D3 D6)
    (bigger D3 D7)
    (bigger D3 D8)
    (bigger D3 D9)
    (bigger D3 D10)
    (bigger D4 D5)
    (bigger D4 D6)
    (bigger D4 D7)
    (bigger D4 D8)
    (bigger D4 D9)
    (bigger D4 D10)
    (bigger D5 D6)
    (bigger D5 D7)
    (bigger D5 D8)
    (bigger D5 D9)
    (bigger D5 D10)
    (bigger D6 D7)
    (bigger D6 D8)
    (bigger D6 D9)
    (bigger D6 D10)
    (bigger D7 D8)
    (bigger D7 D9)
    (bigger D7 D10)
    (bigger D8 D9)
    (bigger D8 D10)
    (bigger D9 D10)
    
    (clear_disk D10)
    (clear_disk D4)

    (clear_case C3)
    
    (on_case D1 C1)
    (on_case D5 C2)
        
    (on_disk D2 D1)
    (on_disk D6 D5)
    (on_disk D3 D2)
    (on_disk D7 D6)
        
    (on_disk D4 D3)
    (on_disk D8 D7)
        
    (on_disk D9 D8)
    (on_disk D10 D9)

)
(:goal
	(and
        (on_case D1 C1)
        (on_case D2 C2)
        (on_case D3 C3)
        (on_disk D4 D1)
        (on_disk D5 D2)
        (on_disk D6 D3)
        (on_disk D7 D4)
        (on_disk D8 D5)
        (on_disk D9 D6)
        (on_disk D10 D9)
        (handempty)
	)
))