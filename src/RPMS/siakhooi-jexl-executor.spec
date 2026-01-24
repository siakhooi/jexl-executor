Name:           siakhooi-jexl-executor
Version:        1.0.0
Release:        1%{?dist}
Summary:        jexl scripts executor

License:        MIT
URL:            https://github.com/siakhooi/jexl-executor
Source0:        https://github.com/siakhooi/%{name}/archive/refs/tags/${version}.tar.gz
BuildArch:      noarch

Requires:       bash
Requires:       jre-21-headless

%description
semver utilities

%prep

%install
mkdir -p %{buildroot}%{_bindir}
mkdir -p %{buildroot}%{_libdir}/java/siakhooi
install -m 0755 %{_working_directory}/usr/bin/* %{buildroot}%{_bindir}
install -m 644 %{_working_directory}/usr/lib/java/siakhooi/* %{buildroot}%{_libdir}/java/siakhooi
install %{_working_directory}/LICENSE %{_builddir}

%files
%license LICENSE
%{_bindir}/jexl-executor
%{_libdir}/java/siakhooi/jexl-executor.jar

%changelog
* Sat Jan 24 2026 Siak Hooi <siakhooi@gmail.com> - 1.0.0
- initial release
