#!/bin/bash
set -e

# shellcheck disable=SC1091
. ./release.env

mvn versions:set -DnewVersion="$RELEASE_VERSION"

sed -i 'src/deb/DEBIAN/control' -e 's@Version: .*@Version: '"$RELEASE_VERSION"'@g'
sed -i 'src/RPMS/siakhooi-jexl-executor.spec' -e 's@Version:        .*@Version:        '"$RELEASE_VERSION"'@g'

sed -i 'src/main/java/io/github/siakhooi/jexl/executor/Version.java' -e 's@APPLICATION_VERSION = ".*"@APPLICATION_VERSION = "'"$RELEASE_VERSION"'"@g'
