(define (problem init)
(:domain grid-visit-all)
(:objects 
	n0 n1 n2 n3 - place 
)
(:init
 (at-robot n0)


 (connected n0 n1)
 (connected n0 n3)

 (connected n1 n3)

 (connected n2 n0)
 
 (connected n3 n2)

)
(:goal ()
)
)
