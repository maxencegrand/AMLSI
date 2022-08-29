(define (problem p1)
 (:domain floor-tile)
 (:objects tile_0-1 tile_0-2 tile_0-3 tile_1-1 tile_1-2 tile_1-3 - tile
           robot1 robot2 - robot
           white black - color
)
 (:init

   (robot-at robot1 tile_1-1)
   (robot-has robot1 black)

   (robot-at robot2 tile_1-2)
   (robot-has robot2 black)

   (available-color white)
   (available-color black)

   (clear tile_0-2)
   (clear tile_0-1)
   (clear tile_0-3)
   (clear tile_1-3)

   (up tile_1-1 tile_0-1)
   (up tile_1-2 tile_0-2)
   (up tile_1-3 tile_0-3)
   (down tile_0-1 tile_1-1)
   (down tile_0-2 tile_1-2)
   (down tile_0-3 tile_1-3)
   (right tile_0-2 tile_0-1)
   (right tile_1-2 tile_1-1)
   (right tile_1-3 tile_1-2)
   (right tile_0-3 tile_0-2)
   (left tile_0-1 tile_0-2)
   (left tile_1-1 tile_1-2)
   (left tile_1-3 tile_1-3)
   (left tile_0-3 tile_0-3)
)
 (:goal (and))
)
