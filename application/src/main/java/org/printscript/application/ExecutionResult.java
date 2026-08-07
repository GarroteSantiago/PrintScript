package org.printscript.application;

import java.util.List;

public record ExecutionResult(List<String> outputLines) {
    public ExecutionResult {
        outputLines = List.copyOf(outputLines);
    }
}
