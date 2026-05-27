#!/usr/bin/env bash
#
# Description: Run shellcheck on all scripts in the src/bin directory.
# Usage: ./shellcheck.sh [options]
#

set -euo pipefail

if ! command -v shellcheck &>/dev/null; then
	echo "Error: shellcheck is not installed. Please install it to run this script."
	exit 1
fi

set -x
shellcheck src/deb/usr/bin/*
