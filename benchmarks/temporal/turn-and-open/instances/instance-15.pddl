


(define (problem turnandopen-4-13-56)
(:domain turnandopen-strips)
(:objects robot1 robot2 robot3 robot4 - robot
rgripper1 lgripper1 rgripper2 lgripper2 rgripper3 lgripper3 rgripper4 lgripper4 - gripper
room1 room2 room3 room4 room5 room6 room7 room8 room9 room10 room11 room12 room13 - room
door1 door2 door3 door4 door5 door6 door7 door8 door9 door10 door11 door12 - door
ball1 ball2 ball3 ball4 ball5 ball6 ball7 ball8 ball9 ball10 ball11 ball12 ball13 ball14 ball15 ball16 ball17 ball18 ball19 ball20 ball21 ball22 ball23 ball24 ball25 ball26 ball27 ball28 ball29 ball30 ball31 ball32 ball33 ball34 ball35 ball36 ball37 ball38 ball39 ball40 ball41 ball42 ball43 ball44 ball45 ball46 ball47 ball48 ball49 ball50 ball51 ball52 ball53 ball54 ball55 ball56 - obj)
(:init
(closed door1)
(closed door2)
(closed door3)
(closed door4)
(closed door5)
(closed door6)
(closed door7)
(closed door8)
(closed door9)
(closed door10)
(closed door11)
(closed door12)
(connected room1 room2 door1)
(connected room2 room1 door1)
(connected room2 room3 door2)
(connected room3 room2 door2)
(connected room3 room4 door3)
(connected room4 room3 door3)
(connected room4 room5 door4)
(connected room5 room4 door4)
(connected room5 room6 door5)
(connected room6 room5 door5)
(connected room6 room7 door6)
(connected room7 room6 door6)
(connected room7 room8 door7)
(connected room8 room7 door7)
(connected room8 room9 door8)
(connected room9 room8 door8)
(connected room9 room10 door9)
(connected room10 room9 door9)
(connected room10 room11 door10)
(connected room11 room10 door10)
(connected room11 room12 door11)
(connected room12 room11 door11)
(connected room12 room13 door12)
(connected room13 room12 door12)
(at-robby robot1 room3)
(free robot1 rgripper1)
(free robot1 lgripper1)
(at-robby robot2 room12)
(free robot2 rgripper2)
(free robot2 lgripper2)
(at-robby robot3 room11)
(free robot3 rgripper3)
(free robot3 lgripper3)
(at-robby robot4 room10)
(free robot4 rgripper4)
(free robot4 lgripper4)
(at ball1 room6)
(at ball2 room8)
(at ball3 room9)
(at ball4 room1)
(at ball5 room11)
(at ball6 room3)
(at ball7 room1)
(at ball8 room3)
(at ball9 room12)
(at ball10 room3)
(at ball11 room1)
(at ball12 room9)
(at ball13 room7)
(at ball14 room5)
(at ball15 room12)
(at ball16 room2)
(at ball17 room8)
(at ball18 room8)
(at ball19 room1)
(at ball20 room11)
(at ball21 room7)
(at ball22 room6)
(at ball23 room3)
(at ball24 room4)
(at ball25 room7)
(at ball26 room2)
(at ball27 room6)
(at ball28 room9)
(at ball29 room1)
(at ball30 room4)
(at ball31 room6)
(at ball32 room6)
(at ball33 room11)
(at ball34 room1)
(at ball35 room6)
(at ball36 room8)
(at ball37 room4)
(at ball38 room7)
(at ball39 room11)
(at ball40 room2)
(at ball41 room9)
(at ball42 room11)
(at ball43 room11)
(at ball44 room2)
(at ball45 room3)
(at ball46 room10)
(at ball47 room4)
(at ball48 room11)
(at ball49 room4)
(at ball50 room4)
(at ball51 room8)
(at ball52 room11)
(at ball53 room10)
(at ball54 room10)
(at ball55 room2)
(at ball56 room3)
)
(:goal
(and
(at ball1 room12)
(at ball2 room7)
(at ball3 room12)
(at ball4 room12)
(at ball5 room10)
(at ball6 room4)
(at ball7 room4)
(at ball8 room8)
(at ball9 room5)
(at ball10 room10)
(at ball11 room3)
(at ball12 room8)
(at ball13 room3)
(at ball14 room13)
(at ball15 room9)
(at ball16 room12)
(at ball17 room11)
(at ball18 room6)
(at ball19 room1)
(at ball20 room13)
(at ball21 room3)
(at ball22 room4)
(at ball23 room10)
(at ball24 room6)
(at ball25 room8)
(at ball26 room5)
(at ball27 room3)
(at ball28 room4)
(at ball29 room1)
(at ball30 room5)
(at ball31 room7)
(at ball32 room12)
(at ball33 room11)
(at ball34 room5)
(at ball35 room11)
(at ball36 room8)
(at ball37 room9)
(at ball38 room2)
(at ball39 room2)
(at ball40 room13)
(at ball41 room11)
(at ball42 room4)
(at ball43 room7)
(at ball44 room1)
(at ball45 room4)
(at ball46 room3)
(at ball47 room12)
(at ball48 room1)
(at ball49 room9)
(at ball50 room13)
(at ball51 room13)
(at ball52 room11)
(at ball53 room3)
(at ball54 room10)
(at ball55 room4)
(at ball56 room11)
)
)
(:metric minimize (total-time))

)
