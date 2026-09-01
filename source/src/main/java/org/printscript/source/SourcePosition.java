package org.printscript.source;

public record SourcePosition(int row, int column, int offset) {
  private static final int MIN_ROW = 1;
  private static final int MIN_COLUMN = 1;
  private static final int MIN_OFFSET = 0;

  public SourcePosition {
    if (row < MIN_ROW) throw new IllegalArgumentException("row must be >= 1");
    if (column < MIN_COLUMN) throw new IllegalArgumentException("column must be >= 1");
    if (offset < MIN_OFFSET) throw new IllegalArgumentException("offset must be >= 0");
  }
}
