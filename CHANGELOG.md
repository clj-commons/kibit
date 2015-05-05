# Changelog

## [Unreleased][unreleased]

## [0.1.2] / 2015-04-21
### Additions
* Clojurescript/Cljx support (cljc support coming soon). This just works™, kibit will pick up your source paths from your `project.clj`'s `:source-paths`, `[:cljsbuild :builds]`, and `[:cljx :builds]`.
* Non-zero exit codes. Kibit now exits non-zero when one or more suggestions are made. This is particularly useful for those running checks in a CI environment.
* You can now run kibit on any Clojure project without a project.clj file. Just call `lein kibit` with any number of files and folders and it will inspect the Clojure files contained within.
