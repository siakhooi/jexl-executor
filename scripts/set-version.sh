#!/usr/bin/env bash
#
# Description: Set the version for the Debian and RPM packages.
# Usage: ./set-version.sh [options]
#

set -euo pipefail

# shellcheck disable=SC1091
. ./release.env

if [[ -z "${RELEASE_VERSION:-}" ]]; then
	echo "Error: RELEASE_VERSION must be set in release.env."
	exit 1
fi

# shellcheck disable=SC1091
. ./build.env
if [[ -z "${PACKAGE_NAME:-}" ]]; then
	echo "Error: PACKAGE_NAME variable not set in build.env."
	exit 1
fi

mvn versions:set -DnewVersion="$RELEASE_VERSION"

sed -i "src/deb/DEBIAN/control" -e 's@Version: .*@Version: '"$RELEASE_VERSION"'@g'
sed -i "src/RPMS/${PACKAGE_NAME}.spec" -e 's@Version:        .*@Version:        '"$RELEASE_VERSION"'@g'

sed -i "src/main/java/io/github/siakhooi/jexl/executor/Version.java" -e 's@APPLICATION_VERSION = ".*"@APPLICATION_VERSION = "'"$RELEASE_VERSION"'"@g'
