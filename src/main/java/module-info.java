module com.example.doctorhibernate {
    requires java.naming;
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.slf4j;
    requires mysql.connector.j;
    requires org.hibernate.orm.core;
    requires static lombok;
    requires jbcrypt;
    requires java.desktop;



    opens com.example.doctorhibernate.controller to javafx.fxml;
    opens com.example.doctorhibernate to javafx.fxml;
    opens com.example.doctorhibernate.entities to org.hibernate.orm.core;
    exports com.example.doctorhibernate;
    exports com.example.doctorhibernate.exception;
    opens com.example.doctorhibernate.exception to javafx.fxml;
}