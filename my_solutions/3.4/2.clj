(load-file "../../sources/add-and-a.clj")

(def a
    (fn [class & args]
        (apply class args) ;; how you can make the list 'expand'
    )
)

(a Triangle (a Point 1 2) (a Point 1 3) (a Point 3 1))

;; Continue from ex 3 onwards
