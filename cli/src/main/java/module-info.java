module org.example.cli {
  requires org.prinstscript.application;
  requires org.printscript.diagnostics;
  requires org.printscript.interpreter;
  requires info.picocli;

  opens org.example.cli to
      info.picocli;
}
