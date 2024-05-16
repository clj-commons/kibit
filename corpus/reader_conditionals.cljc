#?(:clj (+ 1 1)
   :cljs (+ 2 2)
   :default (+ 3 3))

{:a 1 :b 2 #?@(:clj [:c 3 :d 4])}
