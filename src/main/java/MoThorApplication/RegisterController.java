package MoThorApplication;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class RegisterController {

    @FXML
    private Button exitButton;
    @FXML
    private Button createAccountButton;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField driverLicenseTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField secondPhoneTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;
    @FXML
    private Label registerStatusLabel;


    public void createAccountButtonOnAction(ActionEvent event)
    {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        if(validateRegisterData(connectDB)) registerAccount(connectDB);
    }

    public void exitButtonOnAction(ActionEvent event){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    public boolean validateRegisterData(Connection connectDB)
    {
        if(firstNameTextField.getText().isBlank()||lastNameTextField.getText().isBlank()||driverLicenseTextField.getText().isBlank()||phoneTextField.getText().isBlank()||usernameTextField.getText().isBlank()||passwordField.getText().isBlank()||repeatPasswordField.getText().isBlank()) {
            registerStatusLabel.setText("Please fill all red fields!");
            return false;
        } else if (!passwordField.getText().equals(repeatPasswordField.getText())) {
            registerStatusLabel.setText("Passwords must be the same!");
            return false;
        } else if(checkUsername(usernameTextField.getText(), connectDB)){
            registerStatusLabel.setText("This username is busy");
            return false;
        }

        else {
            registerStatusLabel.setText("You Have been succesfully registered");
            return true;
        }
    }



    public boolean checkUsername(String username, Connection connectDB)
    {
        String lookForUsername = "SELECT count(1) FROM Human WHERE Login='"+username+"'";
        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(lookForUsername);

            while(queryResult.next())
            {
                if (queryResult.getInt(1)==0) {
                    return false;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public void registerAccount(Connection connectDB)
    {
        int phoneID=getPhoneID(phoneTextField.getText(),connectDB);
        if(phoneID==0){
            registerStatusLabel.setText("Nie ma takiego telefonu");
        }
        if(phoneID!=0){
            registerStatusLabel.setText("Id tego numeru to:"+phoneID);
        }
    }
    public int getPhoneID(String phoneNumber, Connection connectDB)
    {
        String getPhoneNumberID = "SELECT PhoneID from Phones where PhoneNumber='"+phoneNumber+"'";
        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(getPhoneNumberID);

            while(queryResult.next())
            {
                if (queryResult.getInt(1)>0) {
                    return queryResult.getInt(1);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}
