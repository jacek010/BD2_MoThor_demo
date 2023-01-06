package MoThorApplication;

import MoThorApplication.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class LoginController {
    @FXML
    private Button registerButton1;
    @FXML
    private Button exitButton;

    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField loginUsernameField;
    @FXML
    private PasswordField loginPasswordField;


    public void loginButtonOnAction(ActionEvent e){

        if(!loginUsernameField.getText().isBlank() && !loginPasswordField.getText().isBlank()){
            loginMessageLabel.setText("Logging in... ");
            validateLogin("CLIENT");
        } else {
            loginMessageLabel.setText("Please enter username and password");
        }
    }
    public void exitButtonOnAction(ActionEvent e){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    public void registerButtonOnAction(ActionEvent e){
        createAccountForm();
    }

    public void validateLogin(String userType){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        String verifyLogin;
        //test new branch
        if(userType.equals("CLIENT")) {
            verifyLogin = "SELECT count(1), HumanID, FirstName, LastName FROM Human WHERE (Login ='" + loginUsernameField.getText() + "' and Password='" + loginPasswordField.getText() + "')AND(HumanID in (SELECT ClientID FROM Clients))";
        }
        else {
            verifyLogin = "SELECT count(1), HumanID, FirstName, LastName FROM Human WHERE (Login ='" + loginUsernameField.getText() + "' and Password='" + loginPasswordField.getText() + "')AND(HumanID in (SELECT EmployeeID FROM Employees))";
        }
        //String verifyLogin="SELECT count(1), HumanID, FirstName, LastName FROM Human where Login ='nunc' and Password='enim'";

        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);
            while(queryResult.next())
            {

                 if (queryResult.getInt(1)==1)
                 {

                     loginMessageLabel.setText("Welcome "+loginUsernameField.getText()+"!");

                     saveLoggedUserParameters(queryResult);
                     openClientView();
                 }
                 else
                 {
                     loginMessageLabel.setText("Wrong username or password!");
                 }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openClientView(){
        try{

            closeCurrentStage();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("listOfCars.fxml")));
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

    public void createAccountForm(){
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("registerWindow.fxml")));
            Stage registerStage = new Stage();

            registerStage.initStyle(StageStyle.UNDECORATED);
            registerStage.setScene(new Scene(root, 600, 400));
            registerStage.show();

        } catch (Exception e)
        {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void saveLoggedUserParameters(ResultSet queryResult) throws SQLException {
        System.out.println();
        DatabaseConnection.loggedID = queryResult.getInt(2);
        DatabaseConnection.firstName=queryResult.getString(3);
        DatabaseConnection.lastName= queryResult.getString(4);
    }

    public void closeCurrentStage()
    {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}