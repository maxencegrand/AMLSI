(define (problem prob1)
 (:domain tms)
 (:objects
 kiln81 kiln82 - kiln8
 kiln21 kiln22 - kiln20
 pone0 - piecetype1
 ptwo0 - piecetype2
 pthree0 - piecetype3
)
 (:init
  (energy)
  (unused-fire kiln81)
  (unused-fire kiln21)
  (unused-fire kiln82)
  (unused-fire kiln22)
  (unused-ceramic pone0)
  (unused-ceramic ptwo0)
  (unused-ceramic pthree0)
)
 (:goal  (and))
)