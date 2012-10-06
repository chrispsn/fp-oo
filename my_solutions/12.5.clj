(require '[clojure.zip :as zip])


;; This is a handy function for inserting into a flow:
;; (-> zipper zlog zip/right zlog...)
(def zlog
     (fn [z] (println "LOG:" (pr-str (zip/node z))) z))

;; This prints the tree above the current node.
(def zuplog
     (fn [z] (zlog (zip/up z)) z))


;; For the first set of exercises
(def flattenize
     (fn [tree]
       (letfn [(flatten-zipper [so-far zipper]
                 (cond (zip/end? zipper)
                       so-far
                       
                       (zip/branch? zipper)
                       (flatten-zipper so-far (zip/next zipper))
                       
                       :else
                       (flatten-zipper (cons (zip/node zipper) so-far)
                                       (zip/next zipper))))]
         (reverse (flatten-zipper '() (zip/seq-zip tree))))))


; Exercise 1
(def all-vectors
    (fn [tree]
        (letfn [(vector-grab [so-far zipper]
            (cond (zip/end? zipper)
                  so-far

                  (zip/branch? zipper)
                  (vector-grab so-far (zip/next zipper))

                  (vector? (zip/node zipper))
                  (vector-grab (cons (zip/node zipper) so-far)
                               (zip/next zipper))

                  :else
                  (vector-grab so-far (zip/next zipper))
            ))]    
            (reverse (vector-grab '() (zip/seq-zip tree)))
        )
    )
)

(println (all-vectors '(fn [a b] (concat [a] [b]))))


; Exercise 2
(def first-vector
    (fn [tree]
        (letfn [(vector-grab [zipper]
            (cond (zip/end? zipper)
                  nil

                  (vector? (zip/node zipper))
                  (zip/node zipper)

                  :else
                  (vector-grab (zip/next zipper))
            )
            )]
            (vector-grab (zip/seq-zip tree))
        )
    )
)
(println (first-vector '(fn [a b] (concat [a] [b]))))


; Comments
; Zippers feel like iterators/generators in Python...
