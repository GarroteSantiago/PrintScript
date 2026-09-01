package org.printscript.application;

@FunctionalInterface
public interface ProgressReporter {
    ProgressReporter NONE = message -> {};

    void report(String message);
}
