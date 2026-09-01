package org.printscript.interpreter;

@FunctionalInterface
public interface InputPort {
  String readLine(String prompt);
}
