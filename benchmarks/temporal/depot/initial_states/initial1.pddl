(define (problem depotprob1818) (:domain Depot)
(:objects
	depot0 - Depot
	distributor0 - Distributor
	truck0 truck1 - Truck
	pallet0 pallet1 - Pallet
	crate0 crate1 - Crate
	hoist0 hoist1 - Hoist)
(:init
	(at pallet0 depot0)
	(at pallet1 distributor0)
	(at crate0 depot0)
	(at crate1 distributor0)

	(on crate0 pallet0)
	(on crate1 pallet1)
	
	(clear crate0)
	(clear crate1)
	;(clear pallet0)
	;(clear pallet1)

	(at truck0 distributor0)
	(at truck1 depot0)
	
	(at hoist0 depot0)
	(available hoist0)
	(at hoist1 distributor0)
	(available hoist1)
)

(:goal (and
	)
))