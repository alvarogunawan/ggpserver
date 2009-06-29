;*******************************************************************************
;* checkers.kif                                                                   *
;*                                             *
;* Last revision 5/31/06 by Nat Love                                            *
;*******************************************************************************

(role white)
(role black)

;*******************************************************************************
;* Initial state.                                                              *
;* Letters are columns: row 1 is WHITE side, row 8 is BLACK                    *
;* Numbers are rows:    column a is left, h is right (from white side)         *
;*******************************************************************************

(init (cell a 1 b))
(init (cell a 3 b))
(init (cell a 4 b))
(init (cell a 5 b))
(init (cell a 7 b))

(init (cell b 2 b))
(init (cell b 4 b))
(init (cell b 5 b))
(init (cell b 6 b))
(init (cell b 8 b))

(init (cell c 1 b))
(init (cell c 3 b))
(init (cell c 4 b))
(init (cell c 5 b))
(init (cell c 7 b))

(init (cell d 2 b))
(init (cell d 4 b))
(init (cell d 5 b))
(init (cell d 6 b))
(init (cell d 8 b))

(init (cell e 1 b))
(init (cell e 3 b))
(init (cell e 4 b))
(init (cell e 5 b))
(init (cell e 7 b))

(init (cell f 2 b))
(init (cell f 4 b))
(init (cell f 5 b))
(init (cell f 6 b))
(init (cell f 8 b))

(init (cell g 1 b))
(init (cell g 3 b))
(init (cell g 4 b))
(init (cell g 5 b))
(init (cell g 7 b))

(init (cell h 2 b))
(init (cell h 4 b))
(init (cell h 5 b))
(init (cell h 6 b))
(init (cell h 8 b))
 (init (cell a 2 wp))
 (init (cell b 1 wp))
 (init (cell c 2 wp))
 (init (cell d 1 wp))
 (init (cell e 2 wp))
 (init (cell f 1 wp))
 (init (cell g 2 wp))
 (init (cell h 1 wp))
 (init (cell b 3 wp))
 (init (cell d 3 wp))
 (init (cell f 3 wp))
 (init (cell h 3 wp))
 (init (cell a 8 bp))
 (init (cell c 8 bp))
 (init (cell e 8 bp))
 (init (cell g 8 bp))
 (init (cell h 7 bp))
 (init (cell f 7 bp))
 (init (cell d 7 bp))
 (init (cell b 7 bp))
 (init (cell a 6 bp))
 (init (cell c 6 bp))
 (init (cell e 6 bp))
 (init (cell g 6 bp))

(init (control white))
(init (step 1))

(init (piece_count white 12))
(init (piece_count black 12))

; End initial state

;*******************************************************************************
;* NEXT STATE AXIOMS: REGULAR MOVES                                            *
;*******************************************************************************

; MOVE SOURCE
; Piece ?p moves out of ?u ?v leaving square blank
(<= (next (cell ?u ?v b))
    (does ?player (move ?p ?u ?v ?x ?y)))

(<= (next (cell ?u ?v b))
    (does ?player (doublejump ?p ?u ?v ?x ?y ?x3 ?y3)))

(<= (next (cell ?u ?v b))
    (does ?player (triplejump ?p ?u ?v ?x ?y ?x3 ?y3 ?x4 ?y4)))

; MOVE DESTINATION: NON-KINGING MOVE
; Piece ?p moves to ?x ?y
(<= (next (cell ?x ?y ?p))
    (does ?player (move ?p ?u ?v ?x ?y))
    (or (distinct ?p wp) (distinct ?y 8))
    (or (distinct ?p bp) (distinct ?y 1)))

(<= (next (cell ?x ?y ?p))
    (does ?player (doublejump ?p ?u ?v ?x3 ?y3 ?x ?y))
    (or (distinct ?p wp) (distinct ?y 8))
    (or (distinct ?p bp) (distinct ?y 1)))

(<= (next (cell ?x ?y ?p))
    (does ?player (triplejump ?p ?u ?v ?x3 ?y3 ?x4 ?y4 ?x ?y))
    (or (distinct ?p wp) (distinct ?y 8))
    (or (distinct ?p bp) (distinct ?y 1)))

; UNDISTURBED CELL: NON-CAPTURE MOVE
; Piece (or blank) ?p at ?x ?y remains unchanged if:
; 1) This move is not a capture
; 2) ?x ?y is a different cell from the move source cell
; 3) ?x ?y is a different cell from the move destination cell
(<= (next (cell ?x ?y ?p))
    (does ?player (move ?piece ?x1 ?y1 ?x2 ?y2))
    (true (cell ?x ?y ?p))
    (not (single_jump_capture ?player ?x1 ?y1 ?x ?y ?x2 ?y2))
    (different_cells ?x ?y ?x1 ?y1)
    (different_cells ?x ?y ?x2 ?y2))

(<= (next (cell ?x ?y ?p))
    (does ?player (doublejump ?piece ?x1 ?y1 ?x2 ?y2 ?x3 ?y3))
    (true (cell ?x ?y ?p))
    (not (single_jump_capture ?player ?x1 ?y1 ?x ?y ?x2 ?y2))
    (not (single_jump_capture ?player ?x2 ?y2 ?x ?y ?x3 ?y3))
    (different_cells ?x ?y ?x1 ?y1)
    (different_cells ?x ?y ?x3 ?y3))

(<= (next (cell ?x ?y ?p))
    (does ?player (triplejump ?piece ?x1 ?y1 ?x2 ?y2 ?x3 ?y3 ?x4 ?y4))
    (true (cell ?x ?y ?p))
    (not (single_jump_capture ?player ?x1 ?y1 ?x ?y ?x2 ?y2))
    (not (single_jump_capture ?player ?x2 ?y2 ?x ?y ?x3 ?y3))
    (not (single_jump_capture ?player ?x3 ?y3 ?x ?y ?x4 ?y4))
    (different_cells ?x ?y ?x1 ?y1)
    (different_cells ?x ?y ?x4 ?y4))
    
; CAPTURED CELL (single jump)
(<= (next (cell ?x ?y b))
	(does ?player (move ?piece ?x1 ?y1 ?x2 ?y2))
	(single_jump_capture ?player ?x1 ?y1 ?x ?y ?x2 ?y2))

; CAPTURED CELL (double jump)
(<= (next (cell ?x ?y b))
	(does ?player (doublejump ?piece ?x1 ?y1 ?x2 ?y2 ?x3 ?y3))
	(or (single_jump_capture ?player ?x1 ?y1 ?x ?y ?x2 ?y2)
		(single_jump_capture ?player ?x2 ?y2 ?x ?y ?x3 ?y3)))

; CAPTURED CELL (triple jump)
(<= (next (cell ?x ?y b))
	(does ?player (triplejump ?piece ?x1 ?y1 ?x2 ?y2 ?x3 ?y3 ?x4 ?y4))
	(or (single_jump_capture ?player ?x1 ?y1 ?x ?y ?x2 ?y2)
		(single_jump_capture ?player ?x2 ?y2 ?x ?y ?x3 ?y3)
		(single_jump_capture ?player ?x3 ?y3 ?x ?y ?x4 ?y4)))

; CONTROL TRANSFER
(<= (next (control white))
    (true (control black)))
(<= (next (control black))
    (true (control white)))

; MOVE COUNT
(<= (next (step ?y))
    (true (step ?x))
    (succ ?x ?y))

;*******************************************************************************
;* NEXT STATE AXIOMS: SPECIAL MOVES                                            *
;*******************************************************************************

; MOVE DESTINATION: KINGING MOVE
(<= (next (cell ?x 8 wk))
    (does white (move wp ?u ?v ?x 8)))
(<= (next (cell ?x 1 bk))
    (does black (move bp ?u ?v ?x 1)))
    
(<= (next (cell ?x 8 wk))
 	(does white (doublejump wp ?u ?v ?x3 ?y3 ?x 8)))
(<= (next (cell ?x 1 bk))
 	(does black (doublejump bp ?u ?v ?x3 ?y3 ?x 1)))

(<= (next (cell ?x 8 wk))
    (does white (triplejump ?p ?u ?v ?x3 ?y3 ?x4 ?y4 ?x 8)))
(<= (next (cell ?x 1 bk))
    (does black (triplejump ?p ?u ?v ?x3 ?y3 ?x4 ?y4 ?x 1)))

;; NEXT for PIECE COUNTER
(<= (next (piece_count ?player ?n))
	(or (does ?player (move ?p ?u ?v ?x ?y))
		(does ?player (doublejump ?p ?u ?v ?x3 ?y3 ?x ?y))
		(does ?player (triplejump ?p ?u ?v ?x3 ?y3 ?x4 ?y4 ?x ?y)))
	(true (piece_count ?player ?n)))

(<= (next (piece_count white ?n))
	(does black (move ?p ?x1 ?y1 ?x2 ?y2))
    (kingmove black ?x1 ?y1 ?x2 ?y2)
	(true (piece_count white ?n)))

(<= (next (piece_count white ?lower))
	(does black (move ?p ?x1 ?y1 ?x2 ?y2))
	(single_jump_capture black ?x1 ?y1 ?x ?y ?x2 ?y2)
	(true (piece_count white ?higher))
	(minus1 ?higher ?lower))

(<= (next (piece_count white ?lower))
	(does black (doublejump ?p ?u ?v ?x3 ?y3 ?x ?y))
	(true (piece_count white ?higher))
	(minus2 ?higher ?lower))

(<= (next (piece_count white ?lower))
	(does black (triplejump ?p ?u ?v ?x3 ?y3 ?x4 ?y4 ?x ?y))
	(true (piece_count white ?higher))
	(minus3 ?higher ?lower))

(<= (next (piece_count black ?n))
	(does white (move ?p ?x1 ?y1 ?x2 ?y2))
    (kingmove white ?x1 ?y1 ?x2 ?y2)
	(true (piece_count black ?n)))
	
(<= (next (piece_count black ?lower))
	(does white (move ?p ?x1 ?y1 ?x2 ?y2))
	(single_jump_capture white ?x1 ?y1 ?x ?y ?x2 ?y2)
	(true (piece_count black ?higher))
	(minus1 ?higher ?lower))

(<= (next (piece_count black ?lower))
	(does white (doublejump ?p ?u ?v ?x3 ?y3 ?x ?y))
	(true (piece_count black ?higher))
	(minus2 ?higher ?lower))

(<= (next (piece_count black ?lower))
	(does white (triplejump ?p ?u ?v ?x3 ?y3 ?x4 ?y4 ?x ?y))
	(true (piece_count black ?higher))
	(minus3 ?higher ?lower))

; End next state axioms

;*******************************************************************************
;* LEGAL MOVES and their auxilliary axioms                                     *
;*******************************************************************************

; Legal Move when you are not jumping (pawn):
(<= (legal ?player (move ?piece ?u ?v ?x ?y))
    (true (control ?player))
	(true (cell ?u ?v ?piece))
    (piece_owner_type ?piece ?player pawn)
    (pawnmove ?player ?u ?v ?x ?y)
    (true (cell ?x ?y b)))

; Legal Move when you are not jumping (king):
(<= (legal ?player (move ?piece ?u ?v ?x ?y))
    (true (control ?player))
	(true (cell ?u ?v ?piece))
    (piece_owner_type ?piece ?player king)
    (kingmove ?player ?u ?v ?x ?y)
    (true (cell ?x ?y b)))
    
; Legal Move when you are single jumping (pawn):
(<= (legal ?player (move ?piece ?u ?v ?x ?y))
    (true (control ?player))
	(true (cell ?u ?v ?piece))
    (piece_owner_type ?piece ?player pawn)
    (pawnjump ?player ?u ?v ?x ?y)
    (true (cell ?x ?y b))
	(single_jump_capture ?player ?u ?v ?c ?d ?x ?y))

; Legal Move when you are single jumping (king):
(<= (legal ?player (move ?piece ?u ?v ?x ?y))
    (true (control ?player))
	(true (cell ?u ?v ?piece))
    (piece_owner_type ?piece ?player king)
    (kingjump ?player ?u ?v ?x ?y)
    (true (cell ?x ?y b))
	(single_jump_capture ?player ?u ?v ?c ?d ?x ?y))

; Legal Move when you are double jumping (pawn):
(<= (legal ?player (doublejump ?piece ?u ?v ?x1 ?y1 ?x2 ?y2))
    (true (control ?player))
	(true (cell ?u ?v ?piece))
    (piece_owner_type ?piece ?player pawn)
    (pawnjump ?player ?u ?v ?x1 ?y1)
    (true (cell ?x1 ?y1 b))
    (pawnjump ?player ?x1 ?y1 ?x2 ?y2)
    (true (cell ?x2 ?y2 b))
    (different_cells ?u ?v ?x2 ?y2)
	(single_jump_capture ?player ?u ?v ?c ?d ?x1 ?y1)
	(single_jump_capture ?player ?x1 ?y1 ?c1 ?d1 ?x2 ?y2))

; Legal Move when you are double jumping (king):
(<= (legal ?player (doublejump ?piece ?u ?v ?x1 ?y1 ?x2 ?y2))
    (true (control ?player))
	(true (cell ?u ?v ?piece))
    (piece_owner_type ?piece ?player king)
    (kingjump ?player ?u ?v ?x1 ?y1)
    (true (cell ?x1 ?y1 b))
    (kingjump ?player ?x1 ?y1 ?x2 ?y2)
    (true (cell ?x2 ?y2 b))
    (different_cells ?u ?v ?x2 ?y2)
	(single_jump_capture ?player ?u ?v ?c ?d ?x1 ?y1)
	(single_jump_capture ?player ?x1 ?y1 ?c1 ?d1 ?x2 ?y2))

; Legal Move when you are triple jumping (pawn):
(<= (legal ?player (triplejump ?piece ?u ?v ?x1 ?y1 ?x2 ?y2 ?x3 ?y3))
    (true (control ?player))
	(true (cell ?u ?v ?piece))
    (piece_owner_type ?piece ?player pawn)
    (pawnjump ?player ?u ?v ?x1 ?y1)
    (true (cell ?x1 ?y1 b))
    (pawnjump ?player ?x1 ?y1 ?x2 ?y2)
    (true (cell ?x2 ?y2 b))
    (different_cells ?u ?v ?x2 ?y2)
    (pawnjump ?player ?x2 ?y2 ?x3 ?y3)
    (true (cell ?x3 ?y3 b))
    (different_cells ?x1 ?y1 ?x3 ?y3)
	(single_jump_capture ?player ?u ?v ?c ?d ?x1 ?y1)
	(single_jump_capture ?player ?x1 ?y1 ?c1 ?d1 ?x2 ?y2)
	(single_jump_capture ?player ?x2 ?y2 ?c2 ?d2 ?x3 ?y3))

; Legal Move when you are triple jumping (king):
(<= (legal ?player (triplejump ?piece ?u ?v ?x1 ?y1 ?x2 ?y2 ?x3 ?y3))
    (true (control ?player))
	(true (cell ?u ?v ?piece))
    (piece_owner_type ?piece ?player king)
    (kingjump ?player ?u ?v ?x1 ?y1)
    (true (cell ?x1 ?y1 b))
    (kingjump ?player ?x1 ?y1 ?x2 ?y2)
    (true (cell ?x2 ?y2 b))
    (different_cells ?u ?v ?x2 ?y2)
    (kingjump ?player ?x2 ?y2 ?x3 ?y3)
    (true (cell ?x3 ?y3 b))
    (different_cells ?x1 ?y1 ?x3 ?y3)
	(single_jump_capture ?player ?u ?v ?c ?d ?x1 ?y1)
	(single_jump_capture ?player ?x1 ?y1 ?c1 ?d1 ?x2 ?y2)
	(single_jump_capture ?player ?x2 ?y2 ?c2 ?d2 ?x3 ?y3))

; NO-OPs for player not moving
(<= (legal white noop)
    (true (control black)))
(<= (legal black noop)
    (true (control white)))
    
; pawnmove

(<= (pawnmove white ?u ?v ?x ?y)
    (next_rank ?v ?y)
    (or (next_file ?u ?x) (next_file ?x ?u)))
	
(<= (pawnmove black ?u ?v ?x ?y)
    (next_rank ?y ?v)
    (or (next_file ?u ?x) (next_file ?x ?u)))

; kingmove is a pawnmove by any player

(<= (kingmove ?player ?u ?v ?x ?y)
	(role ?player)
	(role ?player2)
	(pawnmove ?player2 ?u ?v ?x ?y))
	
; pawnjump

(<= (pawnjump white ?u ?v ?x ?y)
    (next_rank ?v ?v1)
    (next_rank ?v1 ?y)
    (next_file ?u ?x1)
    (next_file ?x1 ?x))

(<= (pawnjump white ?u ?v ?x ?y)
    (next_rank ?v ?v1)
    (next_rank ?v1 ?y)
    (next_file ?x ?x1)
    (next_file ?x1 ?u))

(<= (pawnjump black ?u ?v ?x ?y)
    (next_rank ?y ?v1)
    (next_rank ?v1 ?v)
    (next_file ?u ?x1)
    (next_file ?x1 ?x))

(<= (pawnjump black ?u ?v ?x ?y)
    (next_rank ?y ?v1)
    (next_rank ?v1 ?v)
    (next_file ?x ?x1)
    (next_file ?x1 ?u))

; kingjump is a pawnjump by any player

(<= (kingjump ?player ?u ?v ?x ?y)
	(role ?player)
	(role ?player2)
	(pawnjump ?player2 ?u ?v ?x ?y))

; single jump capture ?player means player is jumping from 
; u v to x y over c d, and an opponent's piece is at c d.

(<= (single_jump_capture ?player ?u ?v ?c ?d ?x ?y)
	(kingjump ?player ?u ?v ?x ?y)
	(kingmove ?player ?u ?v ?c ?d)
	(kingmove ?player ?c ?d ?x ?y)
	(true (cell ?c ?d ?piece))
	(opponent ?player ?opponent)
    (piece_owner_type ?piece ?opponent ?type))

;; Goals and Terminal
(<= (has_legal_move ?player)
	(piece_owner_type ?piece ?player ?type)
    (or (legal ?player (move ?piece ?u ?v ?x ?y))
		(legal ?player (doublejump ?piece ?u ?v ?x1 ?y1 ?x ?y))
		(legal ?player (triplejump ?piece ?u ?v ?x1 ?y1 ?x2 ?y2 ?x ?y))))
		
(<= (stuck ?player)
    (role ?player)
    (not (has_legal_move ?player)))

(<= terminal
	(true (control ?player))
	(stuck ?player))
	
(<= terminal
	(true (piece_count ?player 0)))

(<= terminal
	(true (step 102)))

(<= (goal white 100)
	(true (piece_count white ?rc))
	(true (piece_count black ?bc))
	(greater ?rc ?bc))

(<= (goal white 50)
	(true (piece_count white ?x))
	(true (piece_count black ?x)))

(<= (goal white 0)
	(true (piece_count white ?rc))
	(true (piece_count black ?bc))
	(greater ?bc ?rc))

(<= (goal black 100)
	(true (piece_count white ?rc))
	(true (piece_count black ?bc))
	(greater ?bc ?rc))

(<= (goal black 50)
	(true (piece_count white ?x))
	(true (piece_count black ?x)))

(<= (goal black 0)
	(true (piece_count white ?rc))
	(true (piece_count black ?bc))
	(greater ?rc ?bc))


;*******************************************************************************
; AUXILIARY PREDICATES                                                         *
;*******************************************************************************

;;;  DIFFERENT CELLS
;;;  True iff ?x1 ?y1 is a different cell from ?x2 ?y2

(<= (adjacent ?x1 ?x2)
    (next_file ?x1 ?x2))

(<= (adjacent ?x1 ?x2)
    (next_file ?x2 ?x1))

(<= (adjacent ?y1 ?y2)
    (next_rank ?y1 ?y2))

(<= (adjacent ?y1 ?y2)
    (next_rank ?y2 ?y1))

(<= (different_cells ?x1 ?y1 ?x2 ?y2)
    (distinct ?x1 ?x2)
    (coordinate ?x1)
    (coordinate ?x2)
    (coordinate ?y1)
    (coordinate ?y2))

(<= (different_cells ?x1 ?y1 ?x2 ?y2)
    (distinct ?y1 ?y2)
    (coordinate ?x1)
    (coordinate ?x2)
    (coordinate ?y1)
    (coordinate ?y2))

; PLAYER OPPONENTS
(opponent white black)
(opponent black white)

; PIECE OWNERSHIP AND TYPE 
(piece_owner_type wk white king)
(piece_owner_type wp white pawn)

(piece_owner_type bk black king)
(piece_owner_type bp black pawn)

; BOARD TOPOLOGY
(next_rank 1 2)
(next_rank 2 3)
(next_rank 3 4)
(next_rank 4 5)
(next_rank 5 6)
(next_rank 6 7)
(next_rank 7 8)

(next_file a b)
(next_file b c)
(next_file c d)
(next_file d e)
(next_file e f)
(next_file f g)
(next_file g h)

; BOARD COORDINATES

(coordinate 1)
(coordinate 2)
(coordinate 3)
(coordinate 4)
(coordinate 5)
(coordinate 6)
(coordinate 7)
(coordinate 8)
(coordinate a)
(coordinate b)
(coordinate c)
(coordinate d)
(coordinate e)
(coordinate f)
(coordinate g)
(coordinate h)

(<= (greater ?a ?b)
    (succ ?b ?a))
(<= (greater ?a ?b)
    (distinct ?a ?b)
    (succ ?c ?a)
    (greater ?c ?b))

(minus3 12 9)
(minus3 11 8)
(minus3 10 7)
(minus3 9 6)
(minus3 8 5)
(minus3 7 4)
(minus3 6 3)
(minus3 5 2)
(minus3 4 1)
(minus3 3 0)
(minus2 12 10)
(minus2 11 9)
(minus2 10 8)
(minus2 9 7)
(minus2 8 6)
(minus2 7 5)
(minus2 6 4)
(minus2 5 3)
(minus2 4 2)
(minus2 3 1)
(minus2 2 0)
(minus1 12 11)
(minus1 11 10)
(minus1 10 9)
(minus1 9 8)
(minus1 8 7)
(minus1 7 6)
(minus1 6 5)
(minus1 5 4)
(minus1 4 3)
(minus1 3 2)
(minus1 2 1)
(minus1 1 0)


; MOVE COUNT SUCCESSOR
(succ 0 1)
(succ 1 2)
(succ 2 3)
(succ 3 4)
(succ 4 5)
(succ 5 6)
(succ 6 7)
(succ 7 8)
(succ 8 9)
(succ 9 10)
(succ 10 11)
(succ 11 12)
(succ 12 13)
(succ 13 14)
(succ 14 15)
(succ 15 16)
(succ 16 17)
(succ 17 18)
(succ 18 19)
(succ 19 20)
(succ 20 21)
(succ 21 22)
(succ 22 23)
(succ 23 24)
(succ 24 25)
(succ 25 26)
(succ 26 27)
(succ 27 28)
(succ 28 29)
(succ 29 30)
(succ 30 31)
(succ 31 32)
(succ 32 33)
(succ 33 34)
(succ 34 35)
(succ 35 36)
(succ 36 37)
(succ 37 38)
(succ 38 39)
(succ 39 40)
(succ 40 41)
(succ 41 42)
(succ 42 43)
(succ 43 44)
(succ 44 45)
(succ 45 46)
(succ 46 47)
(succ 47 48)
(succ 48 49)
(succ 49 50)
(succ 50 51)
(succ 51 52)
(succ 52 53)
(succ 53 54)
(succ 54 55)
(succ 55 56)
(succ 56 57)
(succ 57 58)
(succ 58 59)
(succ 59 60)
(succ 60 61)
(succ 61 62)
(succ 62 63)
(succ 63 64)
(succ 64 65)
(succ 65 66)
(succ 66 67)
(succ 67 68)
(succ 68 69)
(succ 69 70)
(succ 70 71)
(succ 71 72)
(succ 72 73)
(succ 73 74)
(succ 74 75)
(succ 75 76)
(succ 76 77)
(succ 77 78)
(succ 78 79)
(succ 79 80)
(succ 80 81)
(succ 81 82)
(succ 82 83)
(succ 83 84)
(succ 84 85)
(succ 85 86)
(succ 86 87)
(succ 87 88)
(succ 88 89)
(succ 89 90)
(succ 90 91)
(succ 91 92)
(succ 92 93)
(succ 93 94)
(succ 94 95)
(succ 95 96)
(succ 96 97)
(succ 97 98)
(succ 98 99)
(succ 99 100)
(succ 100 101)
(succ 101 102)
(succ 102 103)
(succ 103 104)
(succ 104 105)
(succ 105 106)
(succ 106 107)
(succ 107 108)
(succ 108 109)
(succ 109 110)
(succ 110 111)
(succ 111 112)
(succ 112 113)
(succ 113 114)
(succ 114 115)
(succ 115 116)
(succ 116 117)
(succ 117 118)
(succ 118 119)
(succ 119 120)
(succ 120 121)
(succ 121 122)
(succ 122 123)
(succ 123 124)
(succ 124 125)
(succ 125 126)
(succ 126 127)
(succ 127 128)
(succ 128 129)
(succ 129 130)
(succ 130 131)
(succ 131 132)
(succ 132 133)
(succ 133 134)
(succ 134 135)
(succ 135 136)
(succ 136 137)
(succ 137 138)
(succ 138 139)
(succ 139 140)
(succ 140 141)
(succ 141 142)
(succ 142 143)
(succ 143 144)
(succ 144 145)
(succ 145 146)
(succ 146 147)
(succ 147 148)
(succ 148 149)
(succ 149 150)
(succ 150 151)
(succ 151 152)
(succ 152 153)
(succ 153 154)
(succ 154 155)
(succ 155 156)
(succ 156 157)
(succ 157 158)
(succ 158 159)
(succ 159 160)
(succ 160 161)
(succ 161 162)
(succ 162 163)
(succ 163 164)
(succ 164 165)
(succ 165 166)
(succ 166 167)
(succ 167 168)
(succ 168 169)
(succ 169 170)
(succ 170 171)
(succ 171 172)
(succ 172 173)
(succ 173 174)
(succ 174 175)
(succ 175 176)
(succ 176 177)
(succ 177 178)
(succ 178 179)
(succ 179 180)
(succ 180 181)
(succ 181 182)
(succ 182 183)
(succ 183 184)
(succ 184 185)
(succ 185 186)
(succ 186 187)
(succ 187 188)
(succ 188 189)
(succ 189 190)
(succ 190 191)
(succ 191 192)
(succ 192 193)
(succ 193 194)
(succ 194 195)
(succ 195 196)
(succ 196 197)
(succ 197 198)
(succ 198 199)
(succ 199 200)
(succ 200 201)

; END of checkers.kif
