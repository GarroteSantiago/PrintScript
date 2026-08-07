package org.printscript.common;

public record Diagnostic(Severity severity, Phase phase, String message, SourceSpan span) {
    public static Diagnostic error(Phase phase, String message, SourceSpan span) {
        return new Diagnostic(Severity.ERROR, phase, message, span);
    }
}
