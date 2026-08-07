package org.printscript.syntax;

import org.printscript.common.Diagnostic;

public final class SyntaxException extends RuntimeException {
    private final Diagnostic diagnostic;

    public SyntaxException(Diagnostic diagnostic) {
        super(diagnostic.message());
        this.diagnostic = diagnostic;
    }

    public Diagnostic diagnostic() {
        return diagnostic;
    }
}
