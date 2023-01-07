module MoThorApplication {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens MoThorApplication to javafx.fxml;
    exports MoThorApplication;
    exports MoThorApplication.Models;
    exports MoThorApplication.Controllers;
    opens MoThorApplication.Models to javafx.fxml;
    opens MoThorApplication.Controllers to javafx.fxml;
}