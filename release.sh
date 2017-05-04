#!/usr/bin/env bash

echo "This is just a dummy representation of what steps you need to take"
echo "It could be automated in the future"

set -ex

echo "Bump the version in kibit-common/resources/jonase/kibit/VERSION to a release version"
pushd kibit
echo "lein deploy"
popd
pushd lein-kibit
echo "lein deploy"
popd
echo "Bump the version in kibit-common to the next SNAPSHOT"
