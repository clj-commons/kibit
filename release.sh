#!/usr/bin/env bash

echo "This is just a dummy representation of what steps you need to take"
echo "It could be automated in the future"
exit 1

set -ex

echo "Bump the version in kibit-common/resources/jonase/kibit/VERSION to a release version before running this"
pushd kibit
lein deploy
popd
pushd lein-kibit
lein deploy
popd
echo "Bump the version in kibit-common to the next SNAPSHOT"
