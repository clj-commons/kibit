#!/usr/bin/env bash

# Locally install all the components.

set -ex

lein install
pushd lein-kibit
lein install
popd

