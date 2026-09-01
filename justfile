# Display the list of available commands by just typing 'just'
deafult:
    @just --list

# --- DEVELOPMENT & EXECUTION ---

# Compile the entire project quickly (skipping tests and analysis)
build:
    ./gradlew assemble

# Run the CLI application (e.g., just run --args="main.ps")
run args:
    ./gradlew :cli:run --args="{{args}}"

# Usage: Send a file path relative to the repo root to interpret with the cli app
pisp-interpret file:
    just run "execute --source=/home/garro/dev/faculty/ingsis/printscript/{{ file }} --version=1.0"

# Usage: Send a file path relative to the repo root to analyze with the cli app
pisp-analyze file config:
    just run "analyze --version=1.0 --source=/home/garro/dev/faculty/ingsis/printscript/{{ file }} --config=/home/garro/dev/faculty/ingsis/printscript/{{ config }}"

# Usage: Send a file path relative to the repo root to format with the cli app
pisp-format file:
    just run "format --version=1.0 --source=/home/garro/dev/faculty/ingsis/printscript/{{ file }}"

# --- CODE QUALITY (LINT & FORMAT) ---

# Automatically format all code using Google Java Format
format:
    ./gradlew spotlessApply

# Run Checkstyle, PMD, and Spotless without running tests (Fast)
lint:
    ./gradlew spotlessCheck checkstyleMain pmdMain

# --- TESTING & COVERAGE ---

# Run all tests cleanly from scratch (Bypasses caching)
test:
    ./gradlew clean test

# Run tests and generate the visual coverage report (JaCoCo)
coverage:
    ./gradlew test jacocoTestReport
    @echo "Coverage report generated inside each module's build directory."
    # For Linux: xdg-open cli/build/reports/jacoco/test/html/index.html
    # For macOS: open cli/build/reports/jacoco/test/html/index.html

# --- CONTINUOUS INTEGRATION / VERIFICATION ---

# Run EVERYTHING (Compiles, tests, verifies style, and checks quality rules)
# Run this command right before pushing your code to the faculty repository!
check:
    ./gradlew clean check jacocoTestReport

# --- MAINTENANCE ---

# Delete all generated build/ directories
clean:
    ./gradlew clean

