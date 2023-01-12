package MoThorApplication.EmployeePanel;

import MoThorApplication.EmployeeWindowHelper;
import MoThorApplication.PrimaryApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class EmployeeEmployeesController implements Initializable {

    @FXML
        private Button exitButton;
    @FXML
        private Button logoutButton;
    @FXML
        private Label loggedAsLabel;


     @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        EmployeeWindowHelper.setEmployeeLoggedAsLabel(loggedAsLabel);


    }


    public void exitButtonOnAction(ActionEvent actionEvent) {
        EmployeeWindowHelper.exit(exitButton);
    }

    public void logoutButtonOnAction(ActionEvent event){
        EmployeeWindowHelper.logout(logoutButton);
    }


}
