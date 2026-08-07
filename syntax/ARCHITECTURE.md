# Syntax Module

The syntax module owns source structure.

Responsibilities:

- lexer
- token model
- token trivia
- parser
- AST model
- concrete/lossless syntax representation when needed by formatting
- statement and expression dispatch protocols

Execution, validation, and analysis consume AST statements through a pull-based parser stream.

```text
SourceInput
  -> Lexer
  -> TokenStream
  -> Parser
  -> Statement
```

Formatting consumes concrete syntax or token trivia.

```text
SourceInput
  -> Lexer
  -> TokenStream with Trivia
  -> Lossless CST
```

Parser recovery is not part of the current design. All commands should stop at the first syntax error.

If recovery is needed later, semicolon should be the main synchronization point.

AST nodes should be immutable language objects. They own structural invariants and dispatch, but not tool-specific behavior.

The formatter needs access to syntax trivia. Comments and whitespace are trivia, not syntactic sugar.
