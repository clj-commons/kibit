# Changelog

All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com).

## [Unreleased]

* Update to Clojure 1.11 to handle ##Inf etc. [#237](https://github.com/clj-commons/kibit/pull/237)
* Switch to borkdude/edamame for side-effect free parsing. [#235](https://github.com/clj-commons/kibit/pull/235), [#246](https://github.com/clj-commons/kibit/pull/246)
* Correctly gather options-spec require vectors as maps so we can check for :as and :as-alias. [#238](https://github.com/clj-commons/kibit/pull/238)
* Moved all of the test/resources files to a new corpus folder which isn't loaded by default on test runs.
* Clarify maintenance status in README.

## 0.1.10 / 2024-05-09

* Fix scm information in generated pom.xml.

## [0.1.9] / 2024-05-09

### Added

* Add deps.edn section to the README [#251](https://github.com/clj-commons/kibit/pull/251) - [@port19x](https://github.com/port19x)
* Add `kibit.driver/exec` to further support deps.edn usage [#252](https://github.com/clj-commons/kibit/pull/252) - [@carrete](https://github.com/carrete)

### Changed

* Include end of range in `simplify-map` [#239](https://github.com/clj-commons/kibit/pull/239) - [@svdo](https://github.com/svdo)
* Update README, fix various typos [#256](https://github.com/clj-commons/kibit/pull/256) - [@terop](https://github.com/terop)

### Fixed

* Kibit cannot parse ns with string requires [#244](https://github.com/clj-commons/kibit/pull/244) and [#247](https://github.com/clj-commons/kibit/pull/247) - [@marksto](https://github.com/marksto)

## [0.1.8] / 2019-11-18

### Fixed

* Handle namespace docstrings [231](https://github.com/jonase/kibit/issues/231) - [@tomjkidd](https://github.com/tomjkidd)

## [0.1.7] / 2019-07-15

### Added

* Print the file path, column, and line number if an error occurs while reading a file. [#212](https://github.com/jonase/kibit/pull/212) - [@LukasRychtecky](https://github.com/LukasRychtecky)

### Changed

* Use platform-independent newlines when joining strings

### Fixed

* Make tests pass on Windows. [#208](https://github.com/jonase/kibit/pull/208) - [@voytech](https://github.com/voytech)
* Transfer `:repositories` from the original project.clj. [#222](https://github.com/jonase/kibit/pull/226) - [@tomjkidd](https://github.com/tomjkidd)
* Handle nested requires with aliases. [#226](https://github.com/jonase/kibit/pull/226)- [@tomjkidd](https://github.com/tomjkidd)

## [0.1.6] / 2018-11-08

### Fixed

* A long awaited feature/fix - Kibit now supports reading namespaced keywords correctly. A very special thanks to Alex Redington who took this tricky task on. [#198](https://github.com/jonase/kibit/pull/198).
* Make Kibit work with local-repos. [#195](https://github.com/jonase/kibit/pull/195)
* Fixup the monkeypatching. [#192](https://github.com/jonase/kibit/pull/192)
* Add alias support to the reader
* Improve source path handling to prevent checking duplicates

## [0.1.5] / 2017-05-02

* 0.1.4, but released properly.

## [0.1.4] / 2017-05-05

### Additions

* Automatic replacement of suggestions (`--replace` and `--interactive` cli arguments)
* Rules for using `run!` instead of `(dorun (map f coll))`

## [0.1.3] / 2016-11-21
### Additions

* Enabled Emacs' next error function to go to next Kibit suggestion. See the updated code in the README for the change.
* #172 Kibit can now handle sets without crashing!
* #152 Send exceptions to STDERR instead of STDOUT
* New rules (#154, #165, )
* #168 Bumped to new versions of clojure and tools.cli dependencies
* #171 Update core.logic to avoid exception from spec

## [0.1.2] / 2015-04-21
### Additions
* Clojurescript/Cljx support (cljc support coming soon). This just works™, kibit will pick up your source paths from your `project.clj`'s `:source-paths`, `[:cljsbuild :builds]`, and `[:cljx :builds]`.
* Non-zero exit codes. Kibit now exits non-zero when one or more suggestions are made. This is particularly useful for those running checks in a CI environment.
* You can now run kibit on any Clojure project without a project.clj file. Just call `lein kibit` with any number of files and folders and it will inspect the Clojure files contained within.
