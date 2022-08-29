(define (domain floor-tile)
(:requirements :strips :typing :negative-preconditions)
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
(:action change-color-start
	:parameters ( ?r - ROBOT ?c - COLOR ?c2 - COLOR )
	:precondition (and
		(robot-has ?r ?c)
		(available-color ?c2)
	)
	:effect (and
		(not (robot-has ?r ?c))
	)
)
(:action change-color-end
	:parameters ( ?r - ROBOT ?c - COLOR ?c2 - COLOR )
	:precondition (and
		(available-color ?c2)
	)
	:effect (and
		(robot-has ?r ?c2)
	)
)
(:action paint-up-start
	:parameters ( ?r - ROBOT ?y - TILE ?x - TILE ?c - COLOR )
	:precondition (and
		(robot-at ?r ?x)
		(clear ?y)
		(robot-has ?r ?c)
		(up ?y ?x)
	)
	:effect (and
		(not (clear ?y))
	)
)
(:action paint-up-end
	:parameters ( ?r - ROBOT ?y - TILE ?x - TILE ?c - COLOR )
	:precondition (and
		(robot-has ?r ?c)
		(up ?y ?x)
	)
	:effect (and
		(painted ?y ?c)
	)
)
(:action paint-down-start
	:parameters ( ?r - ROBOT ?y - TILE ?x - TILE ?c - COLOR )
	:precondition (and
		(robot-at ?r ?x)
		(clear ?y)
		(robot-has ?r ?c)
		(down ?y ?x)
	)
	:effect (and
		(not (clear ?y))
	)
)
(:action paint-down-end
	:parameters ( ?r - ROBOT ?y - TILE ?x - TILE ?c - COLOR )
	:precondition (and
		(robot-has ?r ?c)
		(down ?y ?x)
	)
	:effect (and
		(painted ?y ?c)
	)
)
(:action up-start
	:parameters ( ?r - ROBOT ?x - TILE ?y - TILE )
	:precondition (and
		(robot-at ?r ?x)
		(clear ?y)
		(up ?y ?x)
	)
	:effect (and
		(not (robot-at ?r ?x))
		(not (clear ?y))
	)
)
(:action up-end
	:parameters ( ?r - ROBOT ?x - TILE ?y - TILE )
	:precondition (and
		(up ?y ?x)
	)
	:effect (and
		(robot-at ?r ?y)
		(clear ?x)
	)
)
(:action down-start
	:parameters ( ?r - ROBOT ?x - TILE ?y - TILE )
	:precondition (and
		(robot-at ?r ?x)
		(clear ?y)
		(down ?y ?x)
	)
	:effect (and
		(not (robot-at ?r ?x))
		(not (clear ?y))
	)
)
(:action down-end
	:parameters ( ?r - ROBOT ?x - TILE ?y - TILE )
	:precondition (and
		(down ?y ?x)
	)
	:effect (and
		(robot-at ?r ?y)
		(clear ?x)
	)
)
(:action right-start
	:parameters ( ?r - ROBOT ?x - TILE ?y - TILE )
	:precondition (and
		(robot-at ?r ?x)
		(clear ?y)
		(right ?y ?x)
	)
	:effect (and
		(not (robot-at ?r ?x))
		(not (clear ?y))
	)
)
(:action right-end
	:parameters ( ?r - ROBOT ?x - TILE ?y - TILE )
	:precondition (and
		(right ?y ?x)
	)
	:effect (and
		(robot-at ?r ?y)
		(clear ?x)
	)
)
(:action left-start
	:parameters ( ?r - ROBOT ?x - TILE ?y - TILE )
	:precondition (and
		(robot-at ?r ?x)
		(clear ?y)
		(left ?y ?x)
	)
	:effect (and
		(not (robot-at ?r ?x))
		(not (clear ?y))
	)
)
(:action left-end
	:parameters ( ?r - ROBOT ?x - TILE ?y - TILE )
	:precondition (and
		(left ?y ?x)
	)
	:effect (and
		(robot-at ?r ?y)
		(clear ?x)
	)
)
)