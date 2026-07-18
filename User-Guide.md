# User Guide

`jexl-executor` runs a chain of JEXL scripts and JSON merge steps against a shared JSON context. Each step reads the merged context from previous steps and writes its result back into the context for the next step.

There are two ways to run a flow:

1. **Positional mode** — pass the context file and script files on the command line.
2. **Config mode** — point at a YAML execution config with `-c` / `--config`.

These modes are mutually exclusive: use one or the other, not both.

## Positional mode

Pass an initial context JSON file, then one or more JEXL (`.jexl`) or JSON script files to run in order:

```bash
jexl-executor initial-context.json step1.jexl step2.json step3.jexl
```

Useful options in this mode:

| Option                           | Description                                                                                                                                                          |
| -------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `-F`, `--full`                   | Print the full merged context instead of only the final result                                                                                                       |
| `-r`, `--result-path=<template>` | Where each step stores its result in the context (default: `{name}`). Use `{name}` for the script basename, e.g. `output.{name}`                                     |
| `-e`, `--exit-code-expr=<expr>`  | JEXL expression on the final context; integral result becomes the process exit code. Inline JEXL or `@file:<path>` (paths relative to the current working directory) |
| `-j`, `--jarfile=<file>`         | File listing JAR paths (one per line) to load for JEXL scripts                                                                                                       |

Examples:

```bash
jexl-executor -F initial-context.json step1.jexl step2.json step3.jexl

jexl-executor -r 'output.{name}' initial-context.json step1.jexl step2.json step3.jexl

jexl-executor -e status initial-context.json step1.jexl
```

## Config mode

Use a YAML execution config when you want reusable flows, shared settings, or multiple named flows in one file.

```bash
jexl-executor -c ./execution-config.yaml
```

If you omit the path, the tool loads `execution-config.yaml` from the current working directory:

```bash
jexl-executor -c
```

Run a specific flow by id (default is `default`):

```bash
jexl-executor -c ./execution-config.yaml --id release
```

Paths in the YAML may be relative; they resolve against the **directory containing the YAML file**.

### Execution config YAML

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

Config-mode notes:

- `resultPathTemplate` and `jarListFile` are read from the YAML root. Do not combine YAML `jarListFile` with `-j` on the command line.
- Each entry under `flows` defines `contextFile`, `scriptFiles`, and optional `exitCodeExpr` for that flow.
- `exitCodeExpr` is the config-mode equivalent of `--exit-code-expr`. Values may be inline JEXL or `@file:path` (paths relative to the YAML directory).
- The exit-code expression runs on the **final merged context** and must evaluate to an integral number.

## JEXL context

Each script step runs against the merged JSON context plus two built-in bindings injected before evaluation:

| Binding  | Value                        |
| -------- | ---------------------------- |
| `stdout` | `System.out` (`PrintStream`) |
| `stderr` | `System.err` (`PrintStream`) |

Use them to print diagnostics without returning that output as the step result, for example:

```jexl
stdout.printf("total is %s%n", total)
```

These names are reserved: they are not loaded from the context file and always refer to the real JVM standard streams.

## Command-line reference

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
