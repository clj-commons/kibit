(ns kibit.rules.util)

(defmacro defrules [name & rules]
  (let [rules (for [[pat alt & {:keys [when]}] rules]
                (let [constraint (or when [])]
                  `['~pat ~constraint '~alt]))]
    (list 'def name (vec rules))))