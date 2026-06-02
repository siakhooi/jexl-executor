# jexl-executor

jexl scripts executor

## Installation

### Ubuntu/Debian

```bash
$ sudo curl -L https://siakhooi.github.io/apt/siakhooi-apt.list | sudo tee /etc/apt/sources.list.d/siakhooi-apt.list > /dev/null
$ sudo curl -L https://siakhooi.github.io/apt/siakhooi-apt.gpg  | sudo tee /usr/share/keyrings/siakhooi-apt.gpg > /dev/null
$ sudo apt update

$ sudo apt install siakhooi-jexl-executor
```

### Fedora/Red Hat

```bash
$ sudo curl -L https://siakhooi.github.io/rpms/siakhooi-rpms.repo | sudo tee /etc/yum.repos.d/siakhooi-rpms.repo > /dev/null

$ sudo dnf install siakhooi-jexl-executor
# or
$ sudo yum install siakhooi-jexl-executor

```

## Usage

```
$ jexl-executor -h
Usage: jexl-executor [-FhV] [--debug] [--jexl-debug] [-c[=[<execution-config.yaml>]]] [-e=<expr>] [--id=<id>]
                     [-j=<jarListFile>] [--log-level=<level>] [-r=<resultPathTemplate>] [<contextFile>]
                     [<scriptFiles>...]
Execute JEXL scripts with JSON context in a chain
      [<contextFile>]       Initial context JSON file (required unless --config/-c is set)
      [<scriptFiles>...]    JEXL script or JSON files to execute in sequence (required unless --config/-c is set)
  -c, --config[=[<execution-config.yaml>]]
                            YAML execution config: global resultPathTemplate, jarListFile, and a flows map (each flow:
                              contextFile, scriptFiles, optional exitCodeExpr). If -c or --config is given without a
                              path, uses execution-config.yaml in the current working directory. Mutually exclusive
                              with positional arguments; relative paths resolve against the YAML file's directory
      --debug               Shorthand for --log-level debug
  -e, --exit-code-expr=<expr>
                            Positional mode only: JEXL expression on the final merged context, or @file:<path> to load
                              JEXL from a file (relative paths use the current working directory). Integral numeric
                              result becomes the process exit code. Not allowed with --config/-c (use exitCodeExpr
                              in the execution config YAML instead).
  -F, --full                Print full context instead of result
  -h, --help                Show this help message and exit.
      --id=<id>             With --config/-c only: which flow id under 'flows' to run (default: default). Not
                              allowed without -c
  -j, --jarfile=<jarListFile>
                            File containing JAR paths (one per line) to load for JEXL scripts (mutually exclusive with
                              jarListFile in an execution config YAML file)
      --jexl-debug          Enable Apache Commons JEXL engine debug mode for richer diagnostics when a script fails
                              (independent of --log-level)
      --log-level=<level>   Root log level: trace, debug, info, warn, error, off, all (=trace) (default: info)
  -r, --result-path=<resultPathTemplate>
                            Path template for results. Use {name} as placeholder for script basename (default: {name}).
                              Examples: {name}, output.{name}, results.{name}.data
  -V, --version             Print version information and exit.
```

Either pass **positional** arguments (`<contextFile> <scriptFiles>...`) or an **execution config** YAML with `-c` / `--config` (not both). **`--config` or `-c` alone** loads **`execution-config.yaml`** from the current working directory; otherwise pass the file path (for example `-c ./my.yaml`). With `-c`, `resultPathTemplate` and `jarListFile` are read from the YAML root; **`flows.<id>`** holds each flow's `contextFile`, `scriptFiles`, and optional `exitCodeExpr`. Without **`--id`**, the flow id **`default`** is used (it must exist in the file unless you always pass **`--id`**). **`--id`** is only valid with **`-c`**; positional mode does not use flow ids. **`--result-path` / `-r`** applies only to positional mode. Optional **`jarListFile`** in the YAML is the same kind of file as **`--jarfile` / `-j`**; you must not set both the YAML field and `-j` at the same time. Exit code from JEXL: **`--exit-code-expr` / `-e`** is only for **positional** mode; with **`-c`**, set optional **`exitCodeExpr`** under the chosen flow (not with `-e`). Values may be inline JEXL or **`@file:path`** (YAML: paths relative to the YAML directory; CLI: relative to the current working directory). The expression runs on the **final merged context** and must evaluate to an integral number (non-numeric results are errors).

### Examples

```
$ jexl-executor initial-context.json step1.jexl step2.json step3.jexl

$ jexl-executor -F initial-context.json step1.jexl step2.json step3.jexl

$ jexl-executor -r 'output.{name}' initial-context.json step1.jexl step2.json step3.jexl

$ jexl-executor -c ./execution-config.yaml

$ jexl-executor -c

$ jexl-executor -c ./execution-config.yaml --id release
```

### Execution config YAML (`-c` / `--config`)

Paths in the YAML file may be relative; they are resolved against the **directory containing the YAML file**.

```yaml
jarListFile: jars.txt               # optional; same as --jarfile (do not use with -j)
resultPathTemplate: "output.{name}" # optional; defaults to {name}
flows:
  default:                          # used when --id is omitted
    contextFile: initial-context.json
    scriptFiles:
      - step1.jexl
      - step2.json
      - step3.jexl
    exitCodeExpr: "status"          # optional; inline JEXL (do not use --exit-code-expr/-e with -c)
  release:                          # optional second flow; run with --id release
    contextFile: initial-context.json
    scriptFiles:
      - step1.jexl
    exitCodeExpr: "@file:exit-code.jexl"
```

## URL

### Quality

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

