# jexl-executor

jexl scripts executor

## Usage

There are 2 ways to run JEXL scripts,
- in a chain against a shared JSON context. Pass files on the command line, or
- use a YAML config file.

**Command line** — context file, then scripts:

```bash
jexl-executor initial-context.json step1.jexl step2.json step3.jexl
```

**Config file** — execution config YAML (`-c` alone loads `execution-config.yaml` in the current directory):

```bash
jexl-executor -c ./execution-config.yaml
jexl-executor -c
```

See the [User Guide](User-Guide.md) for options, execution config format, JEXL context bindings, and the full CLI reference.

## Installation

See [Installation.md](Installation.md) for Homebrew, Linux packages, Windows winget, and manual binary installs.

## Quality

- Qlty.sh: <https://qlty.sh/gh/siakhooi/projects/jexl-executor>
- SonarCloud: <https://sonarcloud.io/project/overview?id=siakhooi_jexl-executor>

## Badges

![GitHub](https://img.shields.io/github/license/siakhooi/jexl-executor?logo=github)
![GitHub last commit](https://img.shields.io/github/last-commit/siakhooi/jexl-executor?logo=github)
![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/siakhooi/jexl-executor?logo=github)
![GitHub issues](https://img.shields.io/github/issues/siakhooi/jexl-executor?logo=github)
![GitHub closed issues](https://img.shields.io/github/issues-closed/siakhooi/jexl-executor?logo=github)
![GitHub pull requests](https://img.shields.io/github/issues-pr-raw/siakhooi/jexl-executor?logo=github)
![GitHub closed pull requests](https://img.shields.io/github/issues-pr-closed-raw/siakhooi/jexl-executor?logo=github)
![GitHub top language](https://img.shields.io/github/languages/top/siakhooi/jexl-executor?logo=github)
![GitHub language count](https://img.shields.io/github/languages/count/siakhooi/jexl-executor?logo=github)
![GitHub repo size](https://img.shields.io/github/repo-size/siakhooi/jexl-executor?logo=github)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/siakhooi/jexl-executor?logo=github)
![Workflow](https://img.shields.io/badge/Workflow-github-purple)
![workflow](https://github.com/siakhooi/jexl-executor/actions/workflows/workflow-build-with-quality-checks.yml/badge.svg)
![workflow](https://github.com/siakhooi/jexl-executor/actions/workflows/workflow-deployments.yml/badge.svg)

![Release](https://img.shields.io/badge/Release-github-purple)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/siakhooi/jexl-executor?label=GPR%20release&logo=github)
![GitHub all releases](https://img.shields.io/github/downloads/siakhooi/jexl-executor/total?color=33cb56&logo=github)
![GitHub Release Date](https://img.shields.io/github/release-date/siakhooi/jexl-executor?logo=github)

![Quality-Qlty](https://img.shields.io/badge/Quality-Qlty-purple)
[![Maintainability](https://qlty.sh/gh/siakhooi/projects/jexl-executor/maintainability.svg)](https://qlty.sh/gh/siakhooi/projects/jexl-executor)
[![Code Coverage](https://qlty.sh/gh/siakhooi/projects/jexl-executor/coverage.svg)](https://qlty.sh/gh/siakhooi/projects/jexl-executor)

![Quality-Sonar](https://img.shields.io/badge/Quality-SonarCloud-purple)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_jexl-executor&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=siakhooi_jexl-executor)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_jexl-executor&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=siakhooi_jexl-executor)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_jexl-executor&metric=bugs)](https://sonarcloud.io/summary/new_code?id=siakhooi_jexl-executor)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_jexl-executor&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=siakhooi_jexl-executor)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_jexl-executor&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=siakhooi_jexl-executor)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_jexl-executor&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=siakhooi_jexl-executor)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_jexl-executor&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=siakhooi_jexl-executor)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_jexl-executor&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=siakhooi_jexl-executor)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_jexl-executor&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=siakhooi_jexl-executor)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_jexl-executor&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=siakhooi_jexl-executor)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=siakhooi_jexl-executor&metric=coverage)](https://sonarcloud.io/summary/new_code?id=siakhooi_jexl-executor)
![Sonar Violations (short format)](https://img.shields.io/sonar/violations/siakhooi_jexl-executor?server=https%3A%2F%2Fsonarcloud.io)
![Sonar Violations (short format)](https://img.shields.io/sonar/blocker_violations/siakhooi_jexl-executor?server=https%3A%2F%2Fsonarcloud.io)
![Sonar Violations (short format)](https://img.shields.io/sonar/critical_violations/siakhooi_jexl-executor?server=https%3A%2F%2Fsonarcloud.io)
![Sonar Violations (short format)](https://img.shields.io/sonar/major_violations/siakhooi_jexl-executor?server=https%3A%2F%2Fsonarcloud.io)
![Sonar Violations (short format)](https://img.shields.io/sonar/minor_violations/siakhooi_jexl-executor?server=https%3A%2F%2Fsonarcloud.io)
![Sonar Violations (short format)](https://img.shields.io/sonar/info_violations/siakhooi_jexl-executor?server=https%3A%2F%2Fsonarcloud.io)
![Sonar Violations (long format)](https://img.shields.io/sonar/violations/siakhooi_jexl-executor?format=long&server=http%3A%2F%2Fsonarcloud.io)

[![Wise](https://img.shields.io/badge/Funding-Wise-33cb56.svg?logo=wise)](https://wise.com/pay/me/siakn3)
![visitors](https://hit-tztugwlsja-uc.a.run.app/?outputtype=badge&counter=ghmd-jexl-executor)

