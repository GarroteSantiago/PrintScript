package org.printscript.application;

public record LanguageVersion(int major, int minor, int patch) {
  private static final int MAJOR_MINOR_PARTS = 2;
  private static final int MAJOR_MINOR_PATCH_PARTS = 3;

  public static final LanguageVersion V1_0_0 = new LanguageVersion(1, 0, 0);
  public static final LanguageVersion V1_1_0 = new LanguageVersion(1, 1, 0);

  public static LanguageVersion parse(String raw) {
    String[] parts = raw.split("\\.");
    if (parts.length == MAJOR_MINOR_PARTS) {
      return new LanguageVersion(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), 0);
    }
    if (parts.length == MAJOR_MINOR_PATCH_PARTS) {
      return new LanguageVersion(
          Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }
    throw new IllegalArgumentException("Unsupported version format: " + raw);
  }

  public boolean supportsV1() {
    return major == 1 && minor == 0;
  }

  public boolean supportsV1_1() {
    return major == 1 && minor == 1;
  }
}
