module org.example.cli {
    requires org.prinstscript.application;
    requires org.printscript.common;
    requires info.picocli;

    opens org.example.cli to info.picocli;
}
