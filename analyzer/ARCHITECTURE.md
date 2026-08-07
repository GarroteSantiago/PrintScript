# Analyzer Module

The analyzer runs configurable static analysis rules.

Responsibilities:

- style diagnostics
- policy diagnostics
- configurable analyzer rules
- exact source positions for findings

Semantic correctness belongs in the semantics module. The analyzer should focus on style and policy.

Example rules:

- identifier naming style
- invalid `println` argument shape
- enabled or disabled rules based on config

Analyzer rules should receive AST messages through focused receivers where useful.

Analyzer configuration should cover:

- enabled rules
- identifier naming style
- restrictions for built-in calls such as `println`
