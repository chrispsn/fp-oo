; Exercise 1
(print (-> [1] first list))

; Exercise 2
(print (-> [1] first inc (* 3) list))

; Exercise 3
(print (-> 3 ((fn [n] (* 2 n))) inc))

; Exercise 4
(print (-> (+ 1 2) (* 3) (+ 4)))
