(define  (problem ei1)
  (:domain parking)
  (:objects
     car_00  car_01  car_02 - car
     curb_0 curb_1 curb_2 - curb
  )
  (:init
    (at-curb car_00)
    (at-curb-num car_00 curb_0)
    (car-clear car_00)
    (at-curb car_01)
    (at-curb-num car_01 curb_1)
    (behind-car car_02 car_01)
    (car-clear car_02)
    (curb-clear curb_1)
  )
  (:goal()
  )
)
