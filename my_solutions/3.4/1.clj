(load-file "../../sources/add-and-a.clj")

;; Without shift
(def add 
    (fn [point-a point-b] 
        (Point 
            (apply + (map :x [point-a point-b]))
            (apply + (map :y [point-a point-b]))
        )
    )
)

(add (Point 1 2) (Point 3 4))

;; With shift
(def add
    (fn [point-a point-b]
        (shift point-a (:x point-b) (:y point-b))
    )
)

(add (Point 1 2) (Point 2 3))
