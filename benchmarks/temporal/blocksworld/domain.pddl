(define (domain blocksworld)
  (:requirements :durative-actions :typing)
  (:types block hand)
  (:predicates
    (on ?x - block ?y - block)
	(ontable ?x - block)
	(clear ?x - block)
	(handempty ?a - hand)
	(holding ?a - hand ?x - block))

    (:durative-action pick-up
	     :parameters (?a - hand ?x - block)
	     :duration (= ?duration 1)
	     :condition
    	     (and
    	        (at start (clear ?x))
    	        (at start (ontable ?x))
    	        (at start (handempty ?a))
    	     )
	     :effect
    	    (and
    	        (at start (not (ontable ?x)))
    		    (at start (not (clear ?x)))
    		    (at start (not (handempty ?a)))
    		    (at end (holding ?a ?x))
    	    )
	)

    (:durative-action put-down
	     :parameters (?a - hand ?x - block)
	     :duration (= ?duration 1)
	     :condition (at start (holding ?a ?x))
	     :effect
	        (and
	            (at start (not (holding ?a ?x)))
		        (at end (clear ?x))
                (at end (handempty ?a))
		        (at end (ontable ?x))
		    )
	)

    (:durative-action stack
	     :parameters (?a - hand ?x - block ?y - block)
	     :duration (= ?duration 1)
	     :condition
	        (and
	            (overall (holding ?a ?x))
	            (at end (clear ?y))
	        )
	     :effect
	        (and
	            (at end (not (holding ?a ?x)))
		        (at end (not (clear ?y)))
		        (at end (clear ?x))
		        (at end (handempty ?a))
		        (at end (on ?x ?y))
		    )
    )

    (:durative-action unstack
	     :parameters (?a - hand ?x - block ?y - block)
	     :duration (= ?duration 1)
	     :condition
	        (and
	            (at start (on ?x ?y))
	            (at start (clear ?x))
	            (at start (handempty ?a))
	        )
	     :effect
	        (and
	            (at end (holding ?a ?x))
		        (at end (clear ?y))
		        (at start (not (clear ?x)))
        		(at start (not (handempty ?a)))
        		(at start (not (on ?x ?y )))
		   )
	)
)
