package org.printscript.common;

public record LanguageVersion(int major, int minor, int patch) {
    public static final LanguageVersion V1_0_0 = new LanguageVersion(1, 0, 0);

    public static LanguageVersion parse(String raw) {
        String[] parts = raw.split("\\.");
        if (parts.length == 2) {
            return new LanguageVersion(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), 0);
        }
        if (parts.length == 3) {
            return new LanguageVersion(
                    Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }
        throw new IllegalArgumentException("Unsupported version format: " + raw);
    }

    public boolean supportsV1() {
        return major == 1 && minor == 0;
    }
}
