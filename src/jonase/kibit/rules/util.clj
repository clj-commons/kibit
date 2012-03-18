(ns jonase.kibit.rules.util)

(defmacro defrules [name & rules]
  (let [rules (for [rule rules]
                (if (= (count rule) 2)
                  (let [[pat alt] rule]
                    `['~pat [] '~alt])
                  (let [[pat constraint alt] rule]
                    `['~pat ~constraint '~alt])))]
    (list 'def name (vec rules))))