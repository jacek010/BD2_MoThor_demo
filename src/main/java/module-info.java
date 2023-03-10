module MoThorApplication {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens MoThorApplication to javafx.fxml;
    exports MoThorApplication;
    exports MoThorApplication.Models;
    exports MoThorApplication.EmployeePanel;
    opens MoThorApplication.Models to javafx.fxml;
    opens MoThorApplication.EmployeePanel to javafx.fxml;
}