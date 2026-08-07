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

The application layer should not contain language rules. It should compose the core modules.

Versioning should affect construction of the language pipeline.

```text
requested version
  -> versioned factory
  -> lexer/parser/semantic/interpreter/formatter/analyzer implementation
```

Future versions should be additive. A newer version may add syntax, types, built-ins, analyzer rules, or formatter options, but should not change the meaning of valid older-version programs.
