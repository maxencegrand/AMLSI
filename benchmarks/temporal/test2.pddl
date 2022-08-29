(define (domain floor-tile)
(:requirements :strips :typing :negative-preconditions :durative-actions)
(:types 
robot color tile - object
)(:predicates
	(robot-at ?r - ROBOT ?x - TILE)
	(up ?x - TILE ?y - TILE)
	(down ?x - TILE ?y - TILE)
	(right ?x - TILE ?y - TILE)
	(left ?x - TILE ?y - TILE)
	(clear ?x - TILE)
	(painted ?x - TILE ?c - COLOR)
	(robot-has ?r - ROBOT ?c - COLOR)
	(available-color ?c - COLOR)
)
(:durative-action change-color
	:parameters ( ?r - ROBOT ?c - COLOR ?c2 - COLOR )
	:duration (= ?duration 5.0)
	:condition (and
		(at start (robot-has ?r ?c) )
		(over all (available-color ?c2) )
	)
	:effect (and
		(at start (not (robot-has ?r ?c)) )
		(at end (robot-has ?r ?c2) )
	)
)
(:durative-action paint-down
	:parameters ( ?r - ROBOT ?y - TILE ?x - TILE ?c - COLOR )
	:duration (= ?duration 2.0)
	:condition (and
		(at start (robot-at ?r ?x) )
		(at start (clear ?y) )
		(over all (robot-has ?r ?c) )
		(over all (down ?y ?x) )
	)
	:effect (and
		(at start (not (clear ?y)) )
		(at end (painted ?y ?c) )
	)
)
(:durative-action left
	:parameters ( ?r - ROBOT ?x - TILE ?y - TILE )
	:duration (= ?duration 1.0)
	:condition (and
		(at start (robot-at ?r ?x) )
		(at start (clear ?y) )
		(over all (left ?y ?x) )
	)
	:effect (and
		(at start (not (robot-at ?r ?x)) )
		(at start (not (clear ?y)) )
		(at end (robot-at ?r ?y) )
		(at end (clear ?x) )
	)
)
(:durative-action paint-up
	:parameters ( ?r - ROBOT ?y - TILE ?x - TILE ?c - COLOR )
	:duration (= ?duration 2.0)
	:condition (and
		(at start (robot-at ?r ?x) )
		(at start (clear ?y) )
		(over all (robot-has ?r ?c) )
		(over all (up ?y ?x) )
	)
	:effect (and
		(at start (not (clear ?y)) )
		(at end (painted ?y ?c) )
	)
)
(:durative-action up
	:parameters ( ?r - ROBOT ?x - TILE ?y - TILE )
	:duration (= ?duration 3.0)
	:condition (and
		(at start (robot-at ?r ?x) )
		(at start (clear ?y) )
		(over all (up ?y ?x) )
	)
	:effect (and
		(at start (not (robot-at ?r ?x)) )
		(at start (not (clear ?y)) )
		(at end (robot-at ?r ?y) )
		(at end (clear ?x) )
	)
)
(:durative-action right
	:parameters ( ?r - ROBOT ?x - TILE ?y - TILE )
	:duration (= ?duration 1.0)
	:condition (and
		(at start (robot-at ?r ?x) )
		(at start (clear ?y) )
		(over all (right ?y ?x) )
	)
	:effect (and
		(at start (not (robot-at ?r ?x)) )
		(at start (not (clear ?y)) )
		(at end (robot-at ?r ?y) )
		(at end (clear ?x) )
	)
)
(:durative-action down
	:parameters ( ?r - ROBOT ?x - TILE ?y - TILE )
	:duration (= ?duration 1.0)
	:condition (and
		(at start (robot-at ?r ?x) )
		(at start (clear ?y) )
		(over all (down ?y ?x) )
	)
	:effect (and
		(at start (not (robot-at ?r ?x)) )
		(at start (not (clear ?y)) )
		(at end (robot-at ?r ?y) )
		(at end (clear ?x) )
	)
)
)