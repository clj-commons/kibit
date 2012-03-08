# kibit

*There's a function for that!*

`kibit` is a static code analyzer for Clojure which uses the
[`core.logic`](https://github.com/clojure/core.logic) unifier to
search for patterns of code for which there might exist a more
idiomatic function or macro. For example if kibit finds the code

    (if (some test)
      (some action)
      nil)

it will make the suggestion to use the `when` macro instead of `if`.

## Usage

Add `[jonase/kibit "0.0.1"]` to your `:plugins` vector in your `:user`
profile (Leiningen 2) or if you are using Leiningen 1:

    $ lein plugin install jonase/kibit 0.0.1

Then you can run

    $ lein kibit

to analyze your namespaces.

## Contributing

It is very easy to write new patterns for `kibit`. Take a look at
[`control-structures.clj`](https://github.com/jonase/kibit/blob/master/src/jonase/kibit/control_structures.clj)
to see how new patterns are created. If you know of a recurring
pattern of code that can be simplified, please consider sending me a
pull request.

Bugs can be reported using the github bug tracker.

## Contributors

* Jonas Enlund
* Phil Hagelberg
* Alan Malloy
* Paul deGrandis

## TODO

* Rules for function definitions (make this more of a lint tool)
* Rules for collection lookup; "2 is a bad smell" [see this blog post](http://tech.puredanger.com/2011/10/12/2-is-a-smell/)
* Extract the "when to use" rules from [Joy of Clojure](http://joyofclojure.com/)
* gh-pages of the Marginalia docs as done [here](http://www.maybetechnology.com/2011/08/literate-programming-with-marginalia.html)
* Leiningen project.clj setting for rule exclusion
* Leiningen project.clj setting for a directory of rules to include
* More rules
* Remove reflection warnings (how?)
* Can core.logic be used to its full potential?
* Analyse ClojureScript files?

## License

Copyright © 2012 Jonas Enlund

Distributed under the Eclipse Public License, the same as Clojure.

