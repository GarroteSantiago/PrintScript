package org.printscript.common;

public record SourcePosition(int row, int column, int offset) {
    public SourcePosition {
        if (row < 1) throw new IllegalArgumentException("row must be >= 1");
        if (column < 1) throw new IllegalArgumentException("column must be >= 1");
        if (offset < 0) throw new IllegalArgumentException("offset must be >= 0");
    }
}
