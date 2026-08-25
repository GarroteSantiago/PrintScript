deafult:
    just --list
# Run using: execute <Relative file path from the repo root>
execute file:
    ./gradlew :cli:run --args="execute --version=1.0 --source=/home/garro/dev/faculty/ingsis/printscript/{{ file }}"
