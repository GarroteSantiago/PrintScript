# Common Module

Shared immutable types used across the system live here.

Responsibilities:

- source input abstractions
- source spans
- diagnostics
- command results
- version values
- progress events
- shared error categories

Design rules:

- Keep this module independent from language tools.
- Do not depend on syntax, semantics, interpreter, formatter, analyzer, application, or adapters.
- Prefer immutable value objects.
- User-code problems should be represented as diagnostics, not raw exceptions.

Diagnostics should include:

- severity
- message
- source span
- phase

Exceptions should be reserved for unexpected internal failures.
