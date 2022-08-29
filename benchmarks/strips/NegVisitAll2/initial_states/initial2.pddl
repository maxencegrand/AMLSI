(define (problem init)
(:domain grid-visit-all)
(:objects 
	n0 n1 n2 - place 
)
(:init
 (at-robot n0)

 (connected n0 n1)
 (connected n1 n2)
 (connected n2 n0)
)
(:goal ()
)
)
