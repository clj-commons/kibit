#!/usr/bin/env bash

# Locally install all the components.

set -ex

pushd kibit
lein install
popd
pushd lein-kibit
lein install
popd

