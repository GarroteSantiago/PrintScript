# App Module

The app module is the CLI adapter.

The CLI should only handle:

- argument parsing
- file, stdin, and stdout wiring
- command selection
- TOML config loading
- terminal-friendly progress rendering
- terminal-friendly diagnostic rendering

It should not contain language logic.

Commands:

- `execute`: validate and run source
- `format`: rewrite source formatting through the formatter
- `analyze`: run semantic validation plus analyzer rules
- `validate`: parse and run semantic validation only

Configuration files should use TOML. The CLI owns TOML loading and converts configuration files into immutable core config objects.

Progress must be reported through a port so the core remains independent from CLI.
