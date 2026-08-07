# Semantics Module

The semantics module owns language meaning and correctness.

Responsibilities:

- symbol resolution
- type checking
- built-in function resolution
- semantic validation
- shared semantic rules

Type errors, undeclared variables, invalid assignments, and invalid built-in calls belong here.

Style and policy checks belong in the analyzer module.

`println` should be parsed as a call by syntax and resolved as a built-in function by semantics.

Built-ins should be handled through a small registry from the start. The registry maps a callable name to its signature and behavior contract.

For version `1.0.0`, the registry only needs `println`, but the model should support more built-ins and future user-defined functions.

Variable declarations require explicit type annotations.
