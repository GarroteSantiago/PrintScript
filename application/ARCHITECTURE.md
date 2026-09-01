# Application Module

The application module is the use-case boundary for PrintScript.

It exposes stable operations independent from CLI:

- execute source
- format source
- analyze source
- validate source

Responsibilities:

- wire version-specific factories
- pass immutable config objects to tools
- coordinate progress reporting
- collect diagnostics
- orchestrate syntax, semantics, interpreter, formatter, and analyzer
- own `CommandResult`, `LanguageVersion`, and `ProgressReporter` — these are orchestration-only
  concepts, used nowhere in the language core (lexer, syntax, semantics, interpreter, formatter,
  analyzer), so they belong to the module that actually uses them rather than a shared foundation
  module

The application layer should not contain language rules. It should compose the core modules.

This is also the composition root for the pull-based pipeline: it is the one place (besides
[testkit](../testkit/ARCHITECTURE.md), for tests) allowed to construct a concrete `lexer.Lexer` and
wire it into a `syntax.StatementSyntaxReader`. Every core module in between depends only on the
`tokens.TokenSource`/`syntax.StatementSource` ports, never on each other's concrete
implementation — `application` is where those ports get bound to real implementations.

Versioning should affect construction of the language pipeline.

```text
requested version
  -> versioned factory
  -> lexer/parser/semantic/interpreter/formatter/analyzer implementation
```

Future versions should be additive. A newer version may add syntax, types, built-ins, analyzer rules, or formatter options, but should not change the meaning of valid older-version programs.
