(declare-const x Int)
(declare-const y Int)
(assert (>= x -50))
(assert (<= y 50))
(check-sat)