(declare-const temp_1_0 Bool)
(declare-const ab_0 Bool)
(declare-const x_2 Int)
(declare-const x_6 Int)
(declare-const x_1 Int)
(declare-const temp_4_0 Bool)
(declare-const x_11 Int)
(declare-const a_1 Int)
(declare-const temp_3_0 Bool)
(declare-const x_7 Int)
(declare-const temp_0_0 Bool)
(declare-const x_8 Int)
(declare-const objective Int)
(declare-const x_9 Int)
(declare-const x_4 Int)
(declare-const ab_2 Bool)
(declare-const x_10 Int)
(declare-const x_5 Int)
(declare-const x_0 Int)
(declare-const x_3 Int)
(declare-const a_0 Int)
(declare-const temp_2_0 Bool)
(declare-const ab_1 Bool)
(assert (= a_1 0))
(assert (or ab_0 (= x_1 0)))
(assert (or ab_1 (= temp_0_0 (< x_1 1))))
(assert (or ab_2 (= x_2 (+ x_1 1))))
(assert (or ab_1 (= temp_1_0 (and temp_0_0 (< x_2 1)))))
(assert (or ab_2 (= x_3 (+ x_2 1))))
(assert (or ab_1 (= temp_2_0 (and temp_1_0 (< x_3 1)))))
(assert (or ab_2 (= x_4 (+ x_3 1))))
(assert (or ab_1 (= temp_3_0 (and temp_2_0 (< x_4 1)))))
(assert (or ab_2 (= x_5 (+ x_4 1))))
(assert (or ab_1 (= temp_4_0 (and temp_3_0 (< x_5 1)))))
(assert (or ab_2 (= x_6 (+ x_5 1))))
(assert (= x_7 (ite temp_4_0 x_6 x_5)))
(assert (= x_8 (ite temp_3_0 x_7 x_4)))
(assert (= x_9 (ite temp_2_0 x_8 x_3)))
(assert (= x_10 (ite temp_1_0 x_9 x_2)))
(assert (= x_11 (ite temp_0_0 x_10 x_1)))
(assert (= objective (+ (ite ab_0 1 0) (ite ab_1 1 0) (ite ab_2 1 0))))