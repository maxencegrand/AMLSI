(define (domain robot)

(:requirements
 :negative-preconditions
 :typing)

(:types package room roomdoor)

(:predicates
    (armempty)
    (rloc ?loc - room)
    (in ?obj - package ?loc - room)
    (holding ?obj - package)
    (closed ?d - roomdoor)
    (opened ?d - roomdoor)
    (goal_in ?obj - package ?loc - room)
    (door ?loc1 - room ?loc2 - room ?d - roomdoor)
    (no_door ?loc1 - room ?loc2 - room))

;primitives
(:action pickup
 :parameters (?obj - package ?loc - room)
 :precondition (and
    (armempty)
    (rloc ?loc)
    (in ?obj ?loc))
 :effect (and
    (not (in ?obj ?loc))
    (not (armempty))
    (holding ?obj)))

(:action putdown
 :parameters (?obj - package ?loc - room)
 :precondition (and
    (rloc ?loc)
    (holding ?obj)
    (goal_in ?obj ?loc))
 :effect (and
    (not (holding ?obj))
    (armempty)
    (in ?obj ?loc)))

(:action move
 :parameters (?loc1 - room ?loc2 - room ?d - roomdoor)
 :precondition (and
    (rloc ?loc1)
    (door ?loc1 ?loc2 ?d)
    (opened ?d))
 :effect (and
    (rloc ?loc2)
    (not (rloc ?loc1))))

(:action open
 :parameters (?loc1 - room ?loc2 - room ?d - roomdoor)
 :precondition (and
    (rloc ?loc1)
    (door ?loc1 ?loc2 ?d)
    (closed ?d))
 :effect (and
     (not (closed ?d))
     (opened ?d)))


)
