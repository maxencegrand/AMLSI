(define (problem init)
(:domain grid-visit-all)
(:objects 
	n0 n1 n2 n3 n4 - place 
)
(:init
 (at-robot n0)

 (connected n0 n1)
 (connected n1 n3)
 (connected n3 n2)
 (connected n3 n0)
 (connected n2 n4)
 (connected n4 n1)
)
(:goal ()
)
)
