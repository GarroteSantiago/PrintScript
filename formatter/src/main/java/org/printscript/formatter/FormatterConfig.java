package org.printscript.formatter;

public record FormatterConfig(
        int spacesBeforeSemicolon,
        int spacesAfterSemicolon,
        int spacesAroundAssignment,
        int spacesAroundOperators,
        int blankLinesBeforePrintln) {
    public static FormatterConfig defaults() {
        return new FormatterConfig(0, 0, 1, 1, 0);
    }

    public FormatterConfig {
        spacesBeforeSemicolon = clamp(spacesBeforeSemicolon, 0, 1);
        spacesAfterSemicolon = clamp(spacesAfterSemicolon, 0, 1);
        spacesAroundAssignment = clamp(spacesAroundAssignment, 0, 1);
        spacesAroundOperators = clamp(spacesAroundOperators, 0, 1);
        blankLinesBeforePrintln = clamp(blankLinesBeforePrintln, 0, 2);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
