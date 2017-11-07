# Changelog

All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com).

## [Unreleased]

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
