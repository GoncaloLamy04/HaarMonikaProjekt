module com.example.haarmonikaprojekt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.haarmonikaprojekt to javafx.fxml;
    exports com.example.haarmonikaprojekt;
    exports ui;
    opens ui to javafx.fxml;
}