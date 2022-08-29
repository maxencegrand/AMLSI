(define (problem ei3)
    (:domain pegsolitaire-sequential)
    (:objects
        pos-0-0 pos-0-1 pos-0-2 - location
        pos-1-0 pos-1-1 pos-1-2 - location
        pos-2-0 pos-2-1 pos-2-2 - location
        pos-3-0 pos-3-1 pos-3-2 - location
    )
    (:init
        (move-ended)
        (IN-LINE pos-0-0 pos-0-1 pos-0-2)
        (IN-LINE pos-0-2 pos-0-1 pos-0-0)

	(IN-LINE pos-1-0 pos-1-1 pos-1-2)
        (IN-LINE pos-1-2 pos-1-1 pos-1-0)

	(IN-LINE pos-2-0 pos-2-1 pos-2-2)
        (IN-LINE pos-2-2 pos-2-1 pos-2-0)

	(IN-LINE pos-3-0 pos-3-1 pos-3-2)
        (IN-LINE pos-3-2 pos-3-1 pos-3-0)


	(IN-LINE pos-0-0 pos-1-0 pos-2-0)
	(IN-LINE pos-2-0 pos-1-0 pos-0-0)

	(IN-LINE pos-0-1 pos-1-1 pos-2-1)
	(IN-LINE pos-2-1 pos-1-1 pos-0-1)

	(IN-LINE pos-0-2 pos-1-2 pos-2-2)
	(IN-LINE pos-2-2 pos-1-2 pos-0-2)

	(IN-LINE pos-1-0 pos-2-0 pos-3-0)
	(IN-LINE pos-3-0 pos-2-0 pos-1-0)

	(IN-LINE pos-1-1 pos-2-1 pos-3-1)
	(IN-LINE pos-3-1 pos-2-1 pos-1-1)

	(IN-LINE pos-1-2 pos-2-2 pos-3-2)
	(IN-LINE pos-3-2 pos-2-2 pos-1-2)

	(free pos-0-0)
	(free pos-0-1)
	(occupied pos-0-2)
	(occupied pos-1-0)
	(occupied pos-1-1)
	(occupied pos-1-2)
	(occupied pos-2-0)
	(occupied pos-2-1)
	(free pos-2-2)
	(free pos-3-0)
	(occupied pos-3-1)
	(free pos-3-2)
	
    )
    (:goal ())
)
