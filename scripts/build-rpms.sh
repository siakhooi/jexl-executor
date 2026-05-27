#!/usr/bin/env bash
#
# Description: Build an RPM package from the source files.
# Usage: ./build-rpms.sh [options]
#

set -euo pipefail

if [[ ! -f ./build.env ]]; then
  echo "Error: build.env file not found. Please create it with the necessary variables."
  exit 1
fi
# shellcheck disable=SC1091
source ./build.env
if [[ -z "${PACKAGE_NAME:-}" ]]; then
  echo "Error: PACKAGE_NAME variable not set in build.env."
  exit 1
fi

# ===== Constants =====
readonly jar_file_path=target/jexl-executor.jar
readonly SOURCE=src
working_directory=$(realpath target/rpmbuild)

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
clean_rpmbuild() {
	rm -rf ~/rpmbuild
  rm -f  ~/.rpmmacros
}
setup_rpmbuild_tree() {
	rpmdev-setuptree
}
copy_spec_file() {
  cp -v $SOURCE/RPMS/"${PACKAGE_NAME}.spec" ~/rpmbuild/SPECS
}
prepare_working_directory() {
  mkdir -p "$working_directory"
}
copy_binary_files() {
  mkdir -p "$working_directory/usr/bin"
  cp -vr $SOURCE/deb/usr/bin "$working_directory/usr"
  chmod 755 "$working_directory/usr/bin/"*
}
copy_jar_file(){
  mkdir -p "$working_directory/usr/lib/java/siakhooi"
  cp -v "$jar_file_path" "$working_directory/usr/lib/java/siakhooi"
}
copy_license_file(){
  cp -vf ./LICENSE "$working_directory"
}
build_rpm_package(){

  rpmlint ~/rpmbuild/SPECS/"${PACKAGE_NAME}".spec
  rpmbuild -bb -vv  --define "_working_directory $working_directory" ~/rpmbuild/SPECS/"${PACKAGE_NAME}".spec
  cp -vf ~/rpmbuild/RPMS/noarch/"${PACKAGE_NAME}"-*.rpm .

}
query_rpm_package() {
  tree ~/rpmbuild/
  rpm -ql ~/rpmbuild/RPMS/noarch/"${PACKAGE_NAME}"-*.rpm
}
generate_rpm_checksums(){
  rpm_file=$(basename "$(ls ./"${PACKAGE_NAME}"-*.rpm)")

  sha256sum "$rpm_file" >"$rpm_file.sha256sum"
  sha512sum "$rpm_file" >"$rpm_file.sha512sum"
}
# ===== Main Logic =====
main() {

  parse_args "$@"

  clean_rpmbuild
  setup_rpmbuild_tree

  prepare_working_directory
  copy_spec_file

  copy_binary_files
  copy_jar_file

  copy_license_file

  build_rpm_package
  query_rpm_package

  generate_rpm_checksums
}

# ===== Entrypoint =====
main "$@"
