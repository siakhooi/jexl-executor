#!/bin/bash
set -e

jar_file_path=target/jexl-executor.jar

rm -f  ~/.rpmmacros
rm -rf ~/rpmbuild
rpmdev-setuptree

working_directory=$(realpath target/rpmbuild)
mkdir -p "$working_directory"

readonly SOURCE=src

# Spec File
cp -v $SOURCE/RPMS/siakhooi-jexl-executor.spec ~/rpmbuild/SPECS

# Binary File
mkdir -p "$working_directory/usr/bin"
cp -vr $SOURCE/deb/usr/bin "$working_directory/usr"
chmod 755 "$working_directory/usr/bin/"*

# Jar file
mkdir -p "$working_directory/usr/lib/java/siakhooi"
cp -v "$jar_file_path" "$working_directory/usr/lib/java/siakhooi"

# License
cp -vf ./LICENSE "$working_directory"

# build rpm file
rpmlint ~/rpmbuild/SPECS/siakhooi-jexl-executor.spec
rpmbuild -bb -vv  --define "_working_directory $working_directory" ~/rpmbuild/SPECS/siakhooi-jexl-executor.spec
cp -vf ~/rpmbuild/RPMS/noarch/siakhooi-jexl-executor-*.rpm .

# query
tree ~/rpmbuild/
rpm -ql ~/rpmbuild/RPMS/noarch/siakhooi-jexl-executor-*.rpm

rpm_file=$(basename "$(ls ./siakhooi-jexl-executor-*.rpm)")

sha256sum "$rpm_file" >"$rpm_file.sha256sum"
sha512sum "$rpm_file" >"$rpm_file.sha512sum"
