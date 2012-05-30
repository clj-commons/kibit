# kibit

*There's a function for that!*

`kibit` is a static code analyzer for Clojure which uses
[`core.logic`](https://github.com/clojure/core.logic) to search for
patterns of code for which there might exist a more idiomatic function
or macro. For example if kibit finds the code

    (if (some test)
      (some action)
      nil)

it will make the suggestion to use the `when` macro instead of `if`.

## Usage

Add `[jonase/kibit "0.0.4"]` to your `:plugins` vector in your `:user`
profile (Leiningen 2) or if you are using Leiningen 1:

    $ lein plugin install jonase/kibit 0.0.4

Then you can run

    $ lein kibit

to analyze your namespaces.

## Contributing

It is very easy to write new patterns for `kibit`. Take a look at
[`control-structures.clj`](https://github.com/jonase/kibit/blob/master/src/kibit/rules/control_structures.clj)
to see how new patterns are created. If you know of a recurring
pattern of code that can be simplified, please consider sending me a
pull request.

Bugs can be reported using the github bug tracker.

## Contributors

* Jonas Enlund
* Phil Hagelberg
* Tassilo Horn
* Alan Malloy
* Paul deGrandis
* Kevin Lynagh

## TODO

* Rules for function definitions (make this more of a lint tool)
* Rules for collection lookup; "2 is a bad smell" [see this blog post](http://tech.puredanger.com/2011/10/12/2-is-a-smell/)
* Extract the "when to use" rules from [Joy of Clojure](http://joyofclojure.com/)
* Leiningen project.clj setting for rule exclusion
* Leiningen project.clj setting for a directory of rules to include
* Analyse ClojureScript files

## License

Copyright © 2012 Jonas Enlund

Distributed under the Eclipse Public License, the same as Clojure.

