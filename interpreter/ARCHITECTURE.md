# Interpreter Module

The interpreter executes validated statements.

Responsibilities:

- statement execution
- expression evaluation
- runtime values
- immutable runtime environment
- output port integration
- runtime diagnostics

Execution should follow this flow:

```text
parse statement
  -> validate statement
  -> execute statement
  -> continue
```

Runtime state should be immutable. Assignment returns a new environment with the updated binding.

This means PrintScript variables may still be reassigned, but the interpreter state object is replaced instead of mutated in place.

Runtime errors should stop execution immediately.

Numbers should use decimal semantics to avoid floating-point surprises.

Callable statements should return values. When no meaningful value exists, the result should be equivalent to Kotlin's `Unit`.

`println` returns the unit value after writing to the output port.
