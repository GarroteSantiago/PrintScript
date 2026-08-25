module org.prinstscript.application {
    requires org.printscript.common;
    requires org.printscript.syntax;
    requires org.printscript.semantics;
    requires org.printscript.interpreter;
    requires org.printscript.formatter;
    requires org.printscript.analyzer;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.dataformat.toml;

    exports org.printscript.application;
}
