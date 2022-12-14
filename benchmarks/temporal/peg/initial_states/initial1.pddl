(define (problem pegsolitaire-sequential-036)
    (:domain pegsolitaire-temporal)
    (:objects
        pos-0-0 pos-0-1 pos-0-2 - location
        pos-1-0 pos-1-1 pos-1-2 - location
        pos-2-0 pos-2-1 pos-2-2 - location
    )
    (:init
        (IN-LINE pos-0-0 pos-0-1 pos-0-2)
        (IN-LINE pos-0-2 pos-0-1 pos-0-0)
	(IN-LINE pos-1-0 pos-1-1 pos-1-2)
        (IN-LINE pos-1-2 pos-1-1 pos-1-0)
	(IN-LINE pos-2-0 pos-2-1 pos-2-2)
        (IN-LINE pos-2-2 pos-2-1 pos-2-0)
	
	(IN-LINE pos-0-0 pos-1-0 pos-2-0)
	(IN-LINE pos-2-0 pos-1-0 pos-0-0)
	(IN-LINE pos-0-1 pos-1-1 pos-2-1)
	(IN-LINE pos-2-1 pos-1-1 pos-0-1)
	(IN-LINE pos-0-2 pos-1-2 pos-2-2)
	(IN-LINE pos-2-2 pos-1-2 pos-0-2)

	(free pos-0-0)
	(occupied pos-0-1)
	(occupied pos-0-2)
	(occupied pos-1-0)
	(occupied pos-1-1)
	(occupied pos-1-2)
	(occupied pos-2-0)
	(occupied pos-2-1)
	(free pos-2-2)
	
    )
    (:goal (and
    	   (free pos-0-0)
    	   (free pos-0-1)
	   (free pos-0-2)
	   (free pos-1-0)
	   (free pos-1-1)
	   (free pos-1-2)
	   (free pos-2-1)
	   (free pos-2-0)))
	   
(:metric minimize (total-time))
)