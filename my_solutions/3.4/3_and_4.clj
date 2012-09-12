(load-file "../../sources/add-and-a.clj")

(def equal-triangles?
    (fn [& args]
        (apply = args)
    )
)

(print (equal-triangles? right-triangle right-triangle))
(print (equal-triangles? right-triangle equal-right-triangle))
(print (equal-triangles? right-triangle different-triangle))
