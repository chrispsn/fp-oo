(load-file "../../sources/add-and-a.clj")

(def valid-triangle?
    (fn [& args]
        (= args (distinct? args))
    )
)
