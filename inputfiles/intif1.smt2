(declare-const a_11 Int)
(declare-const x_2 Int)
(declare-const ab_4 Bool)
(declare-const y_12 Int)
(declare-const x_1 Int)
(declare-const a_9 Int)
(declare-const temp_5_0 Bool)
(declare-const temp_4_0 Bool)
(declare-const ab_2 Bool)
(declare-const y_14 Int)
(declare-const y_7 Int)
(declare-const y_2 Int)
(declare-const temp_3_0 Bool)
(declare-const a_4 Int)
(declare-const y_11 Int)
(declare-const y_1 Int)
(declare-const ab_8 Bool)
(declare-const ab_7 Bool)
(declare-const temp_1_0 Bool)
(declare-const y_5 Int)
(declare-const b_0 Int)
(declare-const a_5 Int)
(declare-const y_9 Int)
(declare-const ab_0 Bool)
(declare-const temp_0_0 Bool)
(declare-const ab_3 Bool)
(declare-const y_4 Int)
(declare-const a_2 Int)
(declare-const a_10 Int)
(declare-const y_0 Int)
(declare-const a_6 Int)
(declare-const objective Int)
(declare-const y_13 Int)
(declare-const a_8 Int)
(declare-const ab_6 Bool)
(declare-const b_1 Int)
(declare-const x_0 Int)
(declare-const y_10 Int)
(declare-const a_3 Int)
(declare-const y_3 Int)
(declare-const a_12 Int)
(declare-const y_6 Int)
(declare-const a_1 Int)
(declare-const a_7 Int)
(declare-const ab_5 Bool)
(declare-const x_3 Int)
(declare-const a_0 Int)
(declare-const temp_6_0 Bool)
(declare-const temp_2_0 Bool)
(declare-const ab_1 Bool)
(declare-const y_8 Int)
(assert (= a_1 1))
(assert (= b_1 2))
(assert (or ab_0 (= x_1 0)))
(assert (or ab_1 (= y_1 0)))
(assert (or ab_2 (= temp_0_0 (> a_1 1))))
(assert (or ab_3 (= x_2 2)))
(assert (or ab_4 (= temp_1_0 (> b_1 2))))
(assert (or ab_5 (= y_2 (+ x_2 1))))
(assert (= y_3 (ite temp_1_0 y_2 y_1)))
(assert (or ab_6 (= temp_2_0 (and (not temp_0_0) (< a_1 0)))))
(assert (or ab_7 (= y_4 (+ y_3 1))))
(assert (or ab_8 (= a_2 (+ a_1 1))))
(assert (or ab_6 (= temp_3_0 (and temp_2_0 (< a_2 0)))))
(assert (or ab_7 (= y_5 (+ y_4 1))))
(assert (or ab_8 (= a_3 (+ a_2 1))))
(assert (or ab_6 (= temp_4_0 (and temp_3_0 (< a_3 0)))))
(assert (or ab_7 (= y_6 (+ y_5 1))))
(assert (or ab_8 (= a_4 (+ a_3 1))))
(assert (or ab_6 (= temp_5_0 (and temp_4_0 (< a_4 0)))))
(assert (or ab_7 (= y_7 (+ y_6 1))))
(assert (or ab_8 (= a_5 (+ a_4 1))))
(assert (or ab_6 (= temp_6_0 (and temp_5_0 (< a_5 0)))))
(assert (or ab_7 (= y_8 (+ y_7 1))))
(assert (or ab_8 (= a_6 (+ a_5 1))))
(assert (= y_9 (ite temp_6_0 y_8 y_7)))
(assert (= a_7 (ite temp_6_0 a_6 a_5)))
(assert (= y_10 (ite temp_5_0 y_9 y_6)))
(assert (= a_8 (ite temp_5_0 a_7 a_4)))
(assert (= y_11 (ite temp_4_0 y_10 y_5)))
(assert (= a_9 (ite temp_4_0 a_8 a_3)))
(assert (= y_12 (ite temp_3_0 y_11 y_4)))
(assert (= a_10 (ite temp_3_0 a_9 a_2)))
(assert (= y_13 (ite temp_2_0 y_12 y_3)))
(assert (= a_11 (ite temp_2_0 a_10 a_1)))
(assert (= x_3 (ite temp_0_0 x_2 x_1)))
(assert (= a_12 (ite temp_0_0 a_1 a_11)))
(assert (= y_14 (ite temp_0_0 y_3 y_13)))
(assert (= objective (+ (ite ab_0 1 0) (ite ab_5 1 0) (ite ab_1 1 0) (ite ab_6 1 0) (ite ab_2 1 0) (ite ab_7 1 0) (ite ab_3 1 0) (ite ab_8 1 0) (ite ab_4 1 0))))