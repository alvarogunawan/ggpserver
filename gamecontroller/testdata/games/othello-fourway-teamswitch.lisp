;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;role
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(role whiterow)
(role whitecol)
(role blackrow)
(role blackcol)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;initialize
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(init (cell c1 c1 green))
(init (cell c1 c2 green))
(init (cell c1 c3 green))
(init (cell c1 c4 green))
(init (cell c1 c5 green))
(init (cell c1 c6 green))
(init (cell c1 c7 green))
(init (cell c1 c8 green))

(init (cell c2 c1 green))
(init (cell c2 c2 green))
(init (cell c2 c3 green))
(init (cell c2 c4 green))
(init (cell c2 c5 green))
(init (cell c2 c6 green))
(init (cell c2 c7 green))
(init (cell c2 c8 green))

(init (cell c3 c1 green))
(init (cell c3 c2 green))
(init (cell c3 c3 green))
(init (cell c3 c4 green))
(init (cell c3 c5 green))
(init (cell c3 c6 green))
(init (cell c3 c7 green))
(init (cell c3 c8 green))

(init (cell c4 c1 green))
(init (cell c4 c2 green))
(init (cell c4 c3 green))
(init (cell c4 c4 white))
(init (cell c4 c5 black))
(init (cell c4 c6 green))
(init (cell c4 c7 green))
(init (cell c4 c8 green))

(init (cell c5 c1 green))
(init (cell c5 c2 green))
(init (cell c5 c3 green))
(init (cell c5 c4 black))
(init (cell c5 c5 white))
(init (cell c5 c6 green))
(init (cell c5 c7 green))
(init (cell c5 c8 green))

(init (cell c6 c1 green))
(init (cell c6 c2 green))
(init (cell c6 c3 green))
(init (cell c6 c4 green))
(init (cell c6 c5 green))
(init (cell c6 c6 green))
(init (cell c6 c7 green))
(init (cell c6 c8 green))

(init (cell c7 c1 green))
(init (cell c7 c2 green))
(init (cell c7 c3 green))
(init (cell c7 c4 green))
(init (cell c7 c5 green))
(init (cell c7 c6 green))
(init (cell c7 c7 green))
(init (cell c7 c8 green))

(init (cell c8 c1 green))
(init (cell c8 c2 green))
(init (cell c8 c3 green))
(init (cell c8 c4 green))
(init (cell c8 c5 green))
(init (cell c8 c6 green))
(init (cell c8 c7 green))
(init (cell c8 c8 green))

(init (control whiterow))

(init (fringe c3 c3))
(init (fringe c3 c4))
(init (fringe c3 c5))
(init (fringe c3 c6))
(init (fringe c4 c3))
(init (fringe c4 c6))
(init (fringe c5 c3))
(init (fringe c5 c6))
(init (fringe c6 c3))
(init (fringe c6 c4))
(init (fringe c6 c5))
(init (fringe c6 c6))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;define a relationship Greater
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(succ c0 c1)
(succ c1 c2)
(succ c2 c3)
(succ c3 c4)
(succ c4 c5)
(succ c5 c6)
(succ c6 c7)
(succ c7 c8)
(succ c8 c9)
(succ c9 c10)
(succ c10 c11)
(succ c11 c12)
(succ c12 c13)
(succ c13 c14)
(succ c14 c15)
(succ c15 c16)
(succ c16 c17)
(succ c17 c18)
(succ c18 c19)
(succ c19 c20)
 (succ c20 c21)
  (succ c21 c22)
  (succ c22 c23)
  (succ c23 c24)
  (succ c24 c25)
  (succ c25 c26)
  (succ c26 c27)
  (succ c27 c28)
  (succ c28 c29)
  (succ c29 c30)
  (succ c30 c31)
  (succ c31 c32)
  (succ c32 c33)
  (succ c33 c34)
  (succ c34 c35)
  (succ c35 c36)
  (succ c36 c37)
  (succ c37 c38)
  (succ c38 c39)
  (succ c39 c40)
  (succ c40 c41)
  (succ c41 c42)
  (succ c42 c43)
  (succ c43 c44)
  (succ c44 c45)
  (succ c45 c46)
  (succ c46 c47)
  (succ c47 c48)
  (succ c48 c49)
  (succ c49 c50)
  (succ c50 c51)
  (succ c51 c52)
  (succ c52 c53)
  (succ c53 c54)
  (succ c54 c55)
  (succ c55 c56)
  (succ c56 c57)
  (succ c57 c58)
  (succ c58 c59)
  (succ c59 c60)
  (succ c60 c61)
  (succ c61 c62)
  (succ c62 c63)
  (succ c63 c64)

(<= (greater ?a ?b)
    (succ ?b ?a))
(<= (greater ?a ?b)
    (distinct ?a ?b)
    (succ ?c ?a)
    (greater ?c ?b))
    
(coordinate c1)
(coordinate c2)
(coordinate c3)
(coordinate c4)
(coordinate c5)
(coordinate c6)
(coordinate c7)
(coordinate c8)

(piece white)
(piece black)

(opponent white black)
(opponent black white)

(selects whiterow white row)
(selects whitecol white col)
(selects blackrow black row)
(selects blackcol black col)

(direction n)
(direction s)
(direction e)
(direction w)
(direction nw)
(direction ne)
(direction sw)
(direction se)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;Define flip
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(opp n s)
(opp s n)
(opp e w)
(opp w e)
(opp nw se)
(opp se nw)
(opp ne sw)
(opp sw ne)

(<= (adj nw ?a ?b ?c ?d)
    (succ ?c ?a)
    (succ ?d ?b))

(<= (adj sw ?a ?b ?c ?d)
    (succ ?a ?c)
    (succ ?d ?b))

(<= (adj ne ?a ?b ?c ?d)
    (succ ?c ?a)
    (succ ?b ?d))

(<= (adj se ?a ?b ?c ?d)
    (succ ?a ?c)
    (succ ?b ?d))

(<= (adj w ?a ?b ?a ?d)
    (succ ?d ?b)
    (coordinate ?a))

(<= (adj e ?a ?b ?a ?d)
    (succ ?b ?d)
    (coordinate ?a))

(<= (adj n ?a ?b ?c ?b)
    (succ ?c ?a)
    (coordinate ?b))

(<= (adj s ?a ?b ?c ?b)
    (succ ?a ?c)
    (coordinate ?b))

;; True if there is a line of ?c1 pieces from ?i ?j on a line 
;; ending just before ?m ?n pointing in ?dir

(<= (onalinep ?i ?j ?c1 ?m ?n ?c2 ?dir)
    (true (cell ?i ?j ?c1))
    (adj ?dir ?i ?j ?m ?n)
    (piece ?c2))

(<= (onalinep ?i ?j ?c1 ?m ?n ?c2 ?dir)
    (true (cell ?i ?j ?c1))
    (adj ?dir ?i ?j ?x ?y)
    (onalinep ?x ?y ?c1 ?m ?n ?c2 ?dir))

;; True if there is a line of ?c1 pieces from ?i ?j on a line 
;; ending just before ?m ?n pointing in ?dir, where ?m ?n is
;; a ?c2 piece

(<= (onaline ?i ?j ?c1 ?m ?n ?c2 ?dir)
    (true (cell ?i ?j ?c1))
    (true (cell ?m ?n ?c2))
    (adj ?dir ?i ?j ?m ?n))

(<= (onaline ?i ?j ?c1 ?m ?n ?c2 ?dir)
    (true (cell ?i ?j ?c1))
    (adj ?dir ?i ?j ?x ?y)
    (onaline ?x ?y ?c1 ?m ?n ?c2 ?dir))
    
;; A black piece at ?i ?j gets flipped by white placement at ?m ?n 

(<= (flip ?i ?j white ?m ?n)
    (onalinep ?i ?j black ?m ?n white ?dir)
    (opp ?dir ?odir)
    (onaline ?i ?j black ?x ?y white ?odir))

;; A white piece at ?i ?j gets flipped by black placement at ?m ?n 

(<= (flip ?i ?j black ?m ?n)
    (onalinep ?i ?j white ?m ?n black ?dir)
    (opp ?dir ?odir)
    (onaline ?i ?j white ?x ?y black ?odir))

(<= (onanylinep ?i ?j ?color ?m ?n ?othercolor)
    (onalinep ?i ?j ?color ?m ?n ?othercolor ?dir))

(<= (clear ?i ?j black ?m ?n)
    (not (onanylinep ?i ?j white ?m ?n black))
    (coordinate ?i)
    (coordinate ?j)
    (coordinate ?m)
    (coordinate ?n))

(<= (clear ?i ?j white ?m ?n)
    (not (onanylinep ?i ?j black ?m ?n white))
    (coordinate ?i)
    (coordinate ?j)
    (coordinate ?m)
    (coordinate ?n))

(<= (clear ?i ?j black ?m ?n)
    (onalinep ?i ?j white ?m ?n black ?dir)
    (opp ?dir ?odir)
    (not (onaline ?i ?j white ?x ?y black ?odir))
    (coordinate ?x)
    (coordinate ?y))

(<= (clear ?i ?j white ?m ?n)
    (onalinep ?i ?j black ?m ?n white ?dir)
    (opp ?dir ?odir)
    (not (onaline ?i ?j black ?x ?y white ?odir))
    (coordinate ?x)
    (coordinate ?y))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;Update rules
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(<= (next (cell ?m ?n white))
    (does whitecol (place ?m ?n))
    (true (cell ?m ?n green)))

(<= (next (cell ?m ?n black))
    (does blackcol (place ?m ?n))
    (true (cell ?m ?n green)))

(<= (next (cell ?m ?n white))
    (true (cell ?m ?n black))
    (does whitecol (place ?i ?j))
    (flip ?m ?n white ?i ?j))

(<= (next (cell ?m ?n black))
    (true (cell ?m ?n white))
    (does blackcol (place ?i ?j))
    (flip ?m ?n black ?i ?j))

;; a piece stays the same color if that player moved--can't flip your own

(<= (next (cell ?m ?n black))
    (true (cell ?m ?n black))
    (does blackcol (place ?x ?y)))

(<= (next (cell ?m ?n white))
    (true (cell ?m ?n white))
    (does whitecol (place ?x ?y)))

(<= (next (cell ?m ?n black))
    (true (cell ?m ?n black))
    (does whitecol (place ?i ?j))
    (clear ?m ?n white ?i ?j))

(<= (next (cell ?m ?n white))
    (true (cell ?m ?n white))
    (does blackcol (place ?i ?j))
    (clear ?m ?n black ?i ?j))

(<= (next (cell ?m ?n green))
    (does ?w (place ?j ?k))
    (true (cell ?m ?n green))
    (or (distinct ?m ?j) (distinct ?n ?k)))

;; A cell is in the fringe if it is was in the fringe and 
;; did not get placed in.

(<= (next (fringe ?x ?y))
    (true (fringe ?x ?y))
    (does ?player (place ?i ?j))
    (or (distinct ?x ?i) (distinct ?y ?j)))

(<= (next (fringe ?x ?y))
    (true (fringe ?x ?y))
    (true (control ?player))
    (does ?player noop))

(<= (next (fringe ?x ?y))
    (true (fringe ?x ?y))
    (does ?rowselector (pickrow ?r)))

(<= (next (rowselected ?m))
    (does ?rowselector (pickrow ?m)))

;; A cell becomes in the fringe if it is a green cell adjacent 
;; to whereever a player placed.

(<= (next (fringe ?x ?y))
    (does ?player (place ?i ?j))
    (adj ?dir ?i ?j ?x ?y)
    (true (cell ?x ?y green)))

;;; Everything stays the same if the player with control noops
;;; and if a rowselector is selectiing

(<= (next (cell ?m ?n ?color))
    (true (cell ?m ?n ?color))
    (true (control ?p))
    (does ?p noop))

(<= (next (cell ?m ?n ?color))
    (true (cell ?m ?n ?color))
    (true (control ?p))
    (does ?p (pickrow ?row)))

(<= (next (control whitecol))
    (true (control whiterow)))

(<= (next (control blackrow))
    (true (control whitecol)))

(<= (next (control blackcol))
    (true (control blackrow)))

(<= (next (control whiterow))
    (true (control blackcol)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; White can place a square if
;;;; It will flip some pieces
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(<= (legal whitecol (place ?m ?n))
    (true (control whitecol))
    (true (rowselected ?m))
    (true (fringe ?m ?n))
    (true (cell ?m ?n green))
    (flip ?i ?j white ?m ?n))

(<= (legal blackcol (place ?m ?n))
    (true (control blackcol))
    (true (rowselected ?m))
    (true (fringe ?m ?n))
    (true (cell ?m ?n green))
    (flip ?i ?j black ?m ?n))

(<= (legal whiterow (pickrow ?m))
    (true (control whiterow))
    (true (fringe ?m ?n))
    (true (cell ?m ?n green))
    (flip ?i ?j white ?m ?n))

(<= (legal blackrow (pickrow ?m))
    (true (control blackrow))
    (true (fringe ?m ?n))
    (true (cell ?m ?n green))
    (flip ?i ?j black ?m ?n))

(<= (legal ?player noop)
    (role ?player)
    (true (control ?someoneelse))
    (distinct ?player ?someoneelse)
    (true (cell ?x ?y green)))

(<= (legal ?p noop)
    (true (control ?p))
    (selects ?p ?color ?something)
    (not (flippable ?color)))

(<= (flippable ?p)
    (true (fringe ?x ?y))
    (true (cell ?x ?y green))
    (flip ?i ?j ?p ?x ?y))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; count pieces
;;;; if the game is in terminal state
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(<= (count ?row ?column ?whitenum ?blacknum)
    (distinct ?column c8)
    (true (cell ?row ?column white))
    (succ ?column ?nc)
    (count ?row ?nc ?rest ?blacknum)
    (succ ?rest ?whitenum))

(<= (count ?row ?column ?whitenum ?blacknum)
    (distinct ?column c8)
    (true (cell ?row ?column black))
    (succ ?column ?nc)
    (count ?row ?nc ?whitenum ?rest)
    (succ ?rest ?blacknum))

(<= (count ?row ?column ?whitenum ?blacknum)
    (distinct ?column c8)
    (true (cell ?row ?column green))
    (succ ?column ?nc)
    (count ?row ?nc ?whitenum ?blacknum))

(<= (count ?row c8 ?whitenum ?blacknum)
    (distinct ?row c8)
    (true (cell ?row c8 white))
    (succ ?row ?nr)
    (count ?nr c1 ?rest ?blacknum)
    (succ ?rest ?whitenum))

(<= (count ?row c8 ?whitenum ?blacknum)
    (distinct ?row c8)
    (true (cell ?row c8 black))
    (succ ?row ?nr)
    (count ?nr c1 ?whitenum ?rest)
    (succ ?rest ?blacknum))

(<= (count ?row c8 ?whitenum ?blacknum)
    (distinct ?row c8)
    (true (cell ?row c8 green))
    (succ ?row ?nr)
    (count ?nr c1 ?whitenum ?blacknum))

(<= (count c8 c8 c1 c0)
    (true (cell c8 c8 white)))

(<= (count c8 c8 c0 c1)
    (true (cell c8 c8 black)))

(<= (count c8 c8 c0 c0)
    (true (cell c8 c8 green)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; define the goal state
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(<= (goal whitecol 100)
    (count c1 c1 ?w ?b)
    (greater ?w ?b))

(<= (goal blackrow 100)
    (count c1 c1 ?w ?b)
    (greater ?w ?b))

(<= (goal blackcol 100)
    (count c1 c1 ?w ?b)
    (greater ?b ?w))

(<= (goal whiterow 100)
    (count c1 c1 ?w ?b)
    (greater ?b ?w))

(<= (goal ?role 50)
    (count c1 c1 ?x ?x)
    (role ?role))

(<= (goal blackcol 0)
    (count c1 c1 ?w ?b)
    (greater ?w ?b))

(<= (goal whiterow 0)
    (count c1 c1 ?w ?b)
    (greater ?w ?b))

(<= (goal blackrow 0)
    (count c1 c1 ?w ?b)
    (greater ?b ?w))

(<= (goal whitecol 0)
    (count c1 c1 ?w ?b)
    (greater ?b ?w))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; define the terminal state
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(<= open
    (true (cell ?m ?n green)))
(<= terminal
    (not open))
(<= terminal
    (not (flippable white))
    (not (flippable black)))