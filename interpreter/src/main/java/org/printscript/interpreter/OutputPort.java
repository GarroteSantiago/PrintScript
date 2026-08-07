package org.printscript.interpreter;

@FunctionalInterface
public interface OutputPort {
    void println(String text);
}
