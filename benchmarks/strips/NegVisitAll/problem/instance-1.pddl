(define (problem grid-2)
(:domain grid-visitAll)
(:objects
	loc-x0-y0
	loc-x0-y1
	loc-x1-y0
	loc-x1-y1
- place

)
(:init
	(at-robot loc-x1-y1)
	(visited loc-x1-y1)
	(connected loc-x0-y0 loc-x1-y0)
 	(connected loc-x0-y0 loc-x0-y1)
 	(connected loc-x0-y1 loc-x1-y1)
 	(connected loc-x0-y1 loc-x0-y0)
 	(connected loc-x1-y0 loc-x0-y0)
 	(connected loc-x1-y0 loc-x1-y1)
 	(connected loc-x1-y1 loc-x0-y1)
 	(connected loc-x1-y1 loc-x1-y0)

)
(:goal
(and
	(visited loc-x0-y0)
	(visited loc-x0-y1)
	(visited loc-x1-y0)
	(visited loc-x1-y1)
)
)
)
