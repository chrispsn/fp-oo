; Exercise 1

; starter pieces
(def seq-zip
    (fn [tree] tree))

; my answers
(defn znode [tree] (identity tree))

(defn zdown [tree] (first tree))

; tests
(println 
;    (-> '(a b c) seq-zip zdown znode)               ; a
;    (-> '( (+ 1 2) 3 4) seq-zip zdown znode)        ; (+ 1 2)
;    (-> '( (+ 1 2) 3 4) seq-zip zdown zdown znode)  ; +
)


; Exercise 2

; starter pieces
(def seq-zip
    (fn [tree] {
        :here tree
        :parents '()
    })
)

; my answers
(defn znode [zipper] (:here zipper)) ; Brian's solution here is nice

(defn zdown [zipper] 
    (if (empty? (:here zipper))
        nil
        (assoc zipper
            :here (first (:here zipper))
            :parents (concat [(:here zipper)] (:parents zipper)) 
        )
    )
)
; Notice Brian's zdown solution actually keeps the entire zipper, not just the node...
; ...which means his zup is actually really simple.

(defn zup [zipper] 
    (if (empty? (:parents zipper))
        nil
        (assoc zipper
            :here (first (:parents zipper))
            :parents (rest (:parents zipper))
        )
    )
)

(defn zroot [zipper]
    (if (empty? (:parents zipper))
        (:here zipper)
        (zroot (zup zipper))
    )
)



; tests
(println 
    (-> '(a b c) seq-zip znode)             ; (a b c)
    (-> '(a b c) seq-zip zdown znode)       ; a
    (-> '(a b c) seq-zip zdown zup znode)   ; (a b c)
    (-> '(a b c) seq-zip zup)               ; nil
    (-> '() seq-zip zdown)                  ; nil
    (-> '(a) seq-zip zroot)                 ; (a)
    (-> '(((a)) b c) seq-zip zdown zdown zdown zroot) ; (((a)) b c)
)

; Exercise 3

; starter pieces

(defn znode [zipper] (:here zipper))

(defn zup [zipper] 
    (if (empty? (:parents zipper))
        nil
        (assoc zipper
            :here (first (:parents zipper))
            :parents (rest (:parents zipper))
        )
    )
)

(defn zroot [zipper]
    (if (empty? (:parents zipper))
        (:here zipper)
        (zroot (zup zipper))
    )
)

; my answers
; Interestingly: very very similar to Brian's!
(defn zdown [zipper] 
    (if (empty? (:here zipper))
        nil
        (assoc zipper
            :here (first (:here zipper))
            :parents (concat [(:here zipper)] (:parents zipper)) 
            :lefts '()
            :rights (rest (:here zipper))
        )
    )
)

(defn zleft [zipper] 
    (if (empty? (:lefts zipper))
        nil
        (assoc zipper
            :here (last (:lefts zipper))
            :lefts (butlast (:lefts zipper))
            :rights (cons (:here zipper) (:rights zipper))
        )
    )
)

(defn zright [zipper]
    (if (empty? (:rights zipper))
        nil
        (assoc zipper
            :here (first (:rights zipper))
            :lefts (concat (:lefts zipper) [(:here zipper)])
            :rights (rest (:rights zipper))
        )
    )
)


; tests
; Note I _should_ technically change zup, but it's not necessary to make the examples work.
; To be honest though, I have no idea how it would be done aside from the way Brian does parents.
; (Interestingly: he didn't implement zup either!)
(println 
    (-> (seq-zip '(a b c)) zdown zright znode)                  ; b
    (-> (seq-zip '(a b c)) zdown zright zright zleft znode)     ; b
    (-> (seq-zip '(a b c)) zdown zleft)                         ; nil
    (-> (seq-zip '(a b c)) zdown zright zright zright)          ; nil
    (-> (seq-zip '(a b c)) zdown zup znode)                     ; (a b c)
)


; Exercise 4
; <note: switching to Brian's base functions because the approach is better.


; TEMPLATE

; starter pieces

(def seq-zip
     (fn [tree]
       {:here tree
        :parents '()
        :lefts '()
        :rights '()}))

(def zdown
     (fn [zipper]
       (if (empty? (:here zipper))
         nil
         (assoc zipper 
           :here (first (:here zipper))
           :lefts '()
           :rights (rest (:here zipper))
           :parents (cons zipper (:parents zipper))))))

(def zup
     (fn [zipper] (first (:parents zipper))));

(def zleft
     (fn [zipper]
       (if (empty? (:lefts zipper))
         nil
         (assoc zipper
           :here (last (:lefts zipper))
           :lefts (butlast (:lefts zipper))
           :rights (cons (last zipper) (:rights zipper))))))

(def zright
     (fn [zipper]
       (if (empty? (:rights zipper))
         nil
         (assoc zipper
           :here (first (:rights zipper))
           :lefts (concat (:lefts zipper) (list (:here zipper)))
           :rights (rest (:rights zipper))))))

(def zroot
     (fn [zipper]
       (if (empty? (:parents zipper))
         (znode zipper)
         (zroot (zup zipper)))))

(def znode :here)

; my answers

(defn zreplace [zipper replacement]
    (assoc zipper
        :here replacement
    )
)

; tests
(println 
    (-> (seq-zip '(a b c)) zdown zright (zreplace 3)
        znode)                                          ; 3
    (-> (seq-zip '(a b c)) zdown zright (zreplace 3)
        zright zleft
        znode)                                          ; 3
    (-> (seq-zip '(a b c)) zdown zright (zreplace 3)
        zleft zright zright
        znode)                                          ; c
)


; Exercise 5

; starter pieces

(def zreplace
    (fn [zipper subtree]
        (assoc zipper
            :here subtree
            :changed true
        )
    )
)

; my answers
; should have used cond instead of if... but aside from that. Oh, and use of let.
; And I also included an unnecessary 'parents' line in there. Gone now.
(defn zup [zipper]
    (if (:changed zipper)
        (if (empty? (:parents zipper))
            nil
            (assoc (first (:parents zipper))
                :here (concat (:lefts zipper) (list (:here zipper)) (:rights zipper))
                :changed true
            )
        )
        (first (:parents zipper))
    )
)


; tests
(println 
    (-> (seq-zip '(a b c)) zdown zright (zreplace 3)
        zup 
        znode)                                          ; (a 3 c)
    (-> (seq-zip '(a b c)) zdown zright (zreplace 3)
        zright (zreplace 4) zup 
        znode)                                          ; (a 3 4)
    (-> (seq-zip '(a)) zdown (zreplace 3) zup zup)      ; nil
    (-> (seq-zip '(a (b) c)) zdown zright zdown (zreplace 3)
        zroot)                                          ; (a (3) c)
    (-> (seq-zip '(a (b) c)) zdown zright zdown (zreplace 3)
        zup zright (zreplace 4)
        zroot)                                          ; (a (3) 4)
)


; Exercise 6

; my answers

(def zbranch? (comp seq? znode)) ; note it just has to be a sequence - doesn't matter if empty'

; you need to make the 'search-up function independent of znext,
; because otherwise when it gets to the bottom and you move up
; because there's no rights, it'll just go back down again.
; ORIGINAL BROKEN ATTEMPT - STOPPED AT PART 3
(defn znext [zipper]
    (cond   (and (zbranch? zipper) (zdown zipper))
            (zdown zipper) 

            (empty? (:rights zipper))
            (znext (zup zipper))

            :else
            (zright zipper)
    )
)

(defn zend? [zipper] 
    (or (:end zipper)
        false
    )
)

(def znext
     (fn [zipper]
       (letfn [(search-up [zipper]
                  (or (if (empty? (:parents zipper))        ; don't need an 'or' yet (see below)
                          (assoc zipper :end true)
                          false)                            ; could have just led straight into an 'or' here
                      (-> zipper zup zright)
                      (-> zipper zup search-up)))]
         (or (and (zbranch? zipper)
                  (zdown zipper))
             (zright zipper)
             (search-up zipper)
         )
        )
    )
)

; tests
(println
    (-> (seq-zip '(a b))    zdown znext znode)              ; b
    (-> (seq-zip '(a ((b)))) zdown znext znode)             ; ((b))
    (-> (seq-zip '(a b)) znext znode)                       ; a
    (-> (seq-zip '(() b)) znext znext znode)                ; b
    (-> (seq-zip '((a) b)) zdown zdown znext znode)         ; b
    (-> (seq-zip '(((a)) b)) zdown zdown zdown znext znode) ; b
    (-> (seq-zip '()) zend?)                                ; false
    (-> (seq-zip '()) znext zend?)                          ; true
)

(def zipper (-> (seq-zip '(a)) znext (zreplace 5) znext))
(println
    (zend? zipper)                                          ; true
    (zroot zipper)                                          ; (5)
)
