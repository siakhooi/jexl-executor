#!/usr/bin/env bash
#
# Description: Build a Debian package from the source files.
# Usage: ./build-deb.sh [options]
#

set -euo pipefail

# ===== Constants =====
readonly source_directory=src/deb
readonly target_directory=target/deb
readonly jar_file_path=target/jexl-executor.jar

# ===== Argument Parsing =====
parse_args() {
  while getopts "h" opt; do
    case "${opt}" in
    h)
      usage
      exit 0
      ;;
    *)
      usage
      exit 1
      ;;
    esac
  done
  shift $((OPTIND - 1))
}
# ===== Helper Functions =====
copy_control_files() {
  mkdir -p $target_directory

  cp -vr $source_directory/DEBIAN $target_directory
}
copy_binary_files() {
  cp -vr $source_directory/usr $target_directory
  chmod 755 $target_directory/usr/bin/*
}
copy_jar_file(){
  mkdir -p $target_directory/usr/lib/java/siakhooi
  cp -v "$jar_file_path" $target_directory/usr/lib/java/siakhooi
}
build_deb_package() {
  fakeroot dpkg-deb --build -Zxz $target_directory
}
rename_deb_package() {
  dpkg-name ${target_directory}.deb
}
generate_checksums(){
  debian_file_path=$(ls ./target/*.deb)

  sha256sum "$debian_file_path" >"$debian_file_path.sha256sum"
  sha512sum "$debian_file_path" >"$debian_file_path.sha512sum"
}
list_deb_contents(){
  debian_file_path=$(ls ./target/*.deb)
  dpkg --contents "$debian_file_path"
}
copy_to_target_directory(){
  debian_file_path=$(ls ./target/*.deb)
  cp "$debian_file_path" .
  cp "$debian_file_path".sha256sum .
  cp "$debian_file_path".sha512sum .
}
# ===== Main Logic =====
main() {
    parse_args "$@"

    copy_control_files
    copy_binary_files

    copy_jar_file

    build_deb_package
    rename_deb_package

    generate_checksums

    list_deb_contents
    copy_to_target_directory
}
# ===== Entrypoint =====
main "$@"
