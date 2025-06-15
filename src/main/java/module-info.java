module org.example.sysacademico {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires jakarta.persistence;

    requires org.hibernate.orm.core;
    requires java.sql;

    requires static lombok;

    opens org.example.sysacademico.model to org.hibernate.orm.core, jakarta.persistence;

    opens org.example.sysacademico to javafx.fxml;

    exports org.example.sysacademico;
    exports org.example.sysacademico.model;
}
