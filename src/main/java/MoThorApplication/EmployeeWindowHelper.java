package MoThorApplication;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class EmployeeWindowHelper {

    public static void logout(Button button)
    {
        try{
            Stage stage = (Stage) button.getScene().getWindow();
            stage.close();

            DatabaseConnection.accessLevel = null;

            Parent root = FXMLLoader.load(Objects.requireNonNull(PrimaryApplication.class.getResource("loginWindow.fxml")));
            Stage registerStage = new Stage();

            registerStage.initStyle(StageStyle.UNDECORATED);
            registerStage.setScene(new Scene(root, 750, 450));
            registerStage.show();
        } catch (Exception e)
        {
            e.printStackTrace();
            e.getCause();
        }
    }

    public static void exit(Button button)
    {
        Stage stage = (Stage) button.getScene().getWindow();
            stage.close();
    }

    public static void setLabel(Label label, String labelText)
    {
        label.setText(labelText);
    }

    public static void setEmployeeLoggedAsLabel(Label label)
    {
        String fullName="You are logged as: "+DatabaseConnection.firstName+" "+DatabaseConnection.lastName+"  |  ID: "+DatabaseConnection.loggedID;
        setLabel(label, fullName);
    }
}
