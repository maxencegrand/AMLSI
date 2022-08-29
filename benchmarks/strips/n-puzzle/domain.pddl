(define (domain n-puzzle-typed)
  (:requirements :typing :negative-preconditions)
  (:types position tile)
  (:predicates (at ?tile - tile ?position - position)
	       (neighbor ?p1 - position ?p2 - position) 
	       (empty ?position - position)
   )

  (:action move
     :parameters (?tile - tile ?from ?to - position)
     :precondition (and (neighbor ?from ?to)
			(at ?tile ?from)
			(empty ?to))
     :effect (and (at ?tile ?to) (empty ?from) 
		  (not (at ?tile ?from)) (not (empty ?to)))
  )
)