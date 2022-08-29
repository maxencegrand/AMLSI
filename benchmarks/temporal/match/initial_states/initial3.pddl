(define (problem pfile0)
 (:domain matchcellar)
 (:objects
    match0 match1 match2 match3 - match
    fuse0 fuse1 fuse2 fuse3 - fuse
)
 (:init
  (handfree)
  (unused match3)
  (unused match1)
  (unused match2)
  (unused match0)
)
 (:goal
  (and
     (mended fuse0)
     (mended fuse1)
))
(:metric minimize (total-time))
)
