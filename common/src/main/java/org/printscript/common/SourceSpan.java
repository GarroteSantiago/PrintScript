package org.printscript.common;

public record SourceSpan(SourcePosition start, SourcePosition end) {
    public static SourceSpan at(SourcePosition position) {
        return new SourceSpan(position, position);
    }
}
