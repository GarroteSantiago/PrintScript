# Tokens Module

Owns the token contract shared by every stage that produces or consumes tokens, without owning
scanning itself.

Responsibilities:

- `Token` — raw lexer output.
- `SyntaxToken` — the immutable, trivia-carrying token shape the parser attaches to AST nodes.
- `TokenType` — the token kind enum.
- `TokenSource` — the pull-based port (`Token next()`) a token producer implements and a token
  consumer depends on. This is what lets [lexer](../lexer/ARCHITECTURE.md) and
  [syntax](../syntax/ARCHITECTURE.md) avoid depending on each other directly: `lexer` depends on
  `tokens` to implement `TokenSource`, `syntax` depends on `tokens` to consume it, and neither
  depends on the other.
- `SyntaxException` — the shared syntax-phase error type, since both the lexer's scanning errors
  and the parser's parse errors are surfaced the same way.

Design rules:

- Depends only on [diagnostics](../diagnostics/ARCHITECTURE.md) (`requires transitive`, since
  `SyntaxException` exposes `Diagnostic`, and `Token`/`SyntaxToken` expose `SourceSpan`
  transitively through it).
- Never depend on `lexer` or `syntax`. This module exists specifically so those two do not need to
  depend on each other.
