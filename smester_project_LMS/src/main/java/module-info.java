module com.example.smester_project_lms {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.jetbrains.annotations;
    requires java.desktop;


    opens com.example.smester_project_lms to javafx.fxml;
    exports com.example.smester_project_lms;
}