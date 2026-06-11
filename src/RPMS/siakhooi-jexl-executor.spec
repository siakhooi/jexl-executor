Name:           siakhooi-jexl-executor
Version:        1.6.2
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

* Thu Jun 11 2026 Siak Hooi <siakhooi@gmail.com> - 1.6.2
- remove wrapper, add exe and msi

* Thu Jun 11 2026 Siak Hooi <siakhooi@gmail.com> - 1.6.1
- fix: add wrapper to Windows zip package

* Wed Jun 3 2026 Siak Hooi <siakhooi@gmail.com> - 1.6.0
- enhancement: add YAML execution config (-c/--config) with flows map, optional root jarListFile, and --id to choose a flow (default id: default)
- enhancement: when -c/--config is given without a path, load execution-config.yaml from the current working directory
- enhancement: add --exit-code-expr/-e for positional mode; optional per-flow exitCodeExpr in YAML (inline or @file:)
- enhancement: set default JEXL engine permissions to unrestricted
- enhancement: replace Makefile with justfile; harden shell scripts used in build/release
- enhancement: add Windows zip build and publish flows; use publish-to-repo-action for apt/rpm publishing
- enhancement: bump dependencies (Logback, Jackson, SLF4J, JUnit, Surefire, publish-to-repo-action)

* Mon Apr 20 2026 Siak Hooi <siakhooi@gmail.com> - 1.5.0
- fix sonar issues
- code refactor and cleanup
- unit test coverage
- enhancement: add --jexl-debug option to enable JEXL script engine debug
- enhancement: add --log-level option to set root log level
- enhancement: show script file when jexl error
- enhancement: continue if context file empty

* Mon Mar 16 2026 Siak Hooi <siakhooi@gmail.com> - 1.4.0
- add stdout, stderr to the execution context

* Tue Mar 10 2026 Siak Hooi <siakhooi@gmail.com> - 1.3.0
- code refactor and cleanup
- bug fixes

* Mon Mar 9 2026 Siak Hooi <siakhooi@gmail.com> - 1.2.0
- support json
- new option: --full, -F to print full context instead of result
- debug log with json format

* Tue Feb 24 2026 Siak Hooi <siakhooi@gmail.com> - 1.1.0
- new option: --debug
- code refactors
- bug fixes

* Sat Jan 24 2026 Siak Hooi <siakhooi@gmail.com> - 1.0.0
- initial release
