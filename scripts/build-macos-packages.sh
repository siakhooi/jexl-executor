#!/usr/bin/env bash
# Build macOS artifacts in one run (used by GitHub Actions). Runs jdeps/jlink once, then
# jpackage for app-image (zipped .app), DMG, and PKG. Requires macOS (jpackage).
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck disable=SC1091
source "${SCRIPT_DIR}/macos-jpackage-prep.sh"

_arch="$(uname -m)"
case "$_arch" in
arm64) _macos_arch_label=arm64 ;;
x86_64) _macos_arch_label=x86_64 ;;
*)
	echo "error: unsupported machine hardware name: $_arch" >&2
	exit 1
	;;
esac

zip_name="${PACKAGE_NAME}-${VERSION}-macos-${_macos_arch_label}.zip"
zip_path="${REPO_ROOT}/${zip_name}"
dmg_path="${REPO_ROOT}/${PACKAGE_NAME}-${VERSION}.dmg"
pkg_path="${REPO_ROOT}/${PACKAGE_NAME}-${VERSION}.pkg"
app_bundle="${JPACKAGE_ROOT}/${PACKAGE_NAME}.app"

for p in "$zip_path" "$dmg_path" "$pkg_path"; do
	if [[ -e "$p" ]]; then
		rm -f "$p"
	fi
done
if [[ -d "$app_bundle" ]]; then
	rm -rf "$app_bundle"
fi

common=(
	--name "$PACKAGE_NAME"
	--input input
	--main-jar "$MAIN_JAR"
	--runtime-image runtime
	--app-version "$VERSION"
	--vendor "$VENDOR"
	--mac-package-identifier "$RELEASE_MAC_PACKAGE_IDENTIFIER"
	# Omit --mac-sign: it is a boolean flag only (no "=false"); CI builds unsigned artifacts.
)

(
	cd "$JPACKAGE_ROOT"
	jpackage --type app-image "${common[@]}"
)

if [[ ! -d "$app_bundle" ]]; then
	echo "error: expected app bundle missing: $app_bundle" >&2
	exit 1
fi

ditto -c -k --sequesterRsrc --keepParent "$app_bundle" "$zip_path"

(
	cd "$JPACKAGE_ROOT"
	jpackage --type dmg --dest "$REPO_ROOT" "${common[@]}"
	jpackage --type pkg --dest "$REPO_ROOT" "${common[@]}"
)

for p in "$zip_path" "$dmg_path" "$pkg_path"; do
	if [[ ! -e "$p" ]]; then
		echo "error: expected output missing: $p" >&2
		exit 1
	fi
done
