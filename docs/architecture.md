# PrintScript Architecture

This document is the entrypoint for the architecture notes.

PrintScript should be built as a small language core surrounded by replaceable interaction adapters. The current adapter is the CLI, but the core must not depend on CLI concepts. Future adapters could be a REST API, editor plugin, web UI, Gradle plugin, or language server.

## Architecture Notes

- [Common Module](../common/ARCHITECTURE.md)
- [Syntax Module](../syntax/ARCHITECTURE.md)
- [Semantics Module](../semantics/ARCHITECTURE.md)
- [Interpreter Module](../interpreter/ARCHITECTURE.md)
- [Formatter Module](../formatter/ARCHITECTURE.md)
- [Analyzer Module](../analyzer/ARCHITECTURE.md)
- [Application Module](../application/ARCHITECTURE.md)
- [CLI App Module](../app/ARCHITECTURE.md)
- [Testkit Module](../testkit/ARCHITECTURE.md)

## High-Level Structure

```text
adapters
  └── cli

application
  └── PrintScript facade

core
  ├── common
  ├── syntax
  ├── semantics
  ├── interpreter
  ├── formatter
  └── analyzer
```

## Main Direction

- Keep the language core independent from interaction layers.
- Process source code statement by statement.
- Use immutable values wherever practical.
- Surface user-code problems as structured diagnostics.
- Keep side effects behind ports.
- Route version-specific behavior through factories.

## Design Rules

- AST is for language meaning.
- Lossless CST/token trivia is for formatting and comment preservation.
- Parser recovery is not part of the current design; commands stop at the first syntax error.
- Runtime errors stop execution immediately.
- Variables require explicit type annotations.
- Numbers use decimal semantics.
- Config files use TOML.
