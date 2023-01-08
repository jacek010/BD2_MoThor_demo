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
        if(validateRegisterData(connectDB))
        {
            registerAccount(connectDB);
        }
    }

    public void exitButtonOnAction(ActionEvent event){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    public boolean validateRegisterData(Connection connectDB)
    {
        if (firstNameTextField.getText().isBlank()||
            lastNameTextField.getText().isBlank()||
            driverLicenseTextField.getText().isBlank()||
            phoneTextField.getText().isBlank()||
            usernameTextField.getText().isBlank()||
            passwordField.getText().isBlank()||
            repeatPasswordField.getText().isBlank())
        {
                registerStatusLabel.setText("Please fill all the red fields!");
                return false;
        }
        else if (!passwordField.getText().equals(repeatPasswordField.getText()))
        {
            registerStatusLabel.setText("Passwords must be the same!");
            return false;
        }
        else if (checkUsername(usernameTextField.getText(), connectDB))
        {
            registerStatusLabel.setText("This username is not available");
            return false;
        }
        //jesli wpisano email to musi byc odpowiedniego formatu
        else if ((!checkEmailFormat(emailTextField.getText(),connectDB))&&!(emailTextField.getText().isBlank()))
        {
            registerStatusLabel.setText("Wrong email address");
            return false;
        }
        else
        {
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
        //1. dodac/znalezc rekord w tabeli phones
        int PhoneID = getPhoneID(phoneTextField.getText(), connectDB);
        if (PhoneID==0)
        {
            PhoneID = addNewPhoneNumber(phoneTextField.getText(), secondPhoneTextField.getText(), connectDB);
        }
        //2. dodac rekord w tabeli human
        int HumanID = addNewHuman(firstNameTextField.getText(),lastNameTextField.getText(),emailTextField.getText(),PhoneID,usernameTextField.getText(), passwordField.getText(), connectDB);
        //3. dodac rekord w tabeli clients
        int ClientID = addNewClient(HumanID,driverLicenseTextField.getText(),connectDB);
        registerStatusLabel.setText("You have been succesfully registered. ClientID ="+ClientID);
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

    public boolean checkEmailFormat(String email, Connection connectDB)
    {
        //email musi zawierac @
        int atIndex = email.indexOf("@");
        if (atIndex == -1)
        {
            return false;
        }
        return true;
    }
    public int addNewPhoneNumber(String phoneNumber, String bPhoneNumber, Connection connectDB)
    {
        //Dodaje nowy numer telefonu przy pomocy procedury AddNewPhone
        //Zwraca PhoneID nowej pary numerow

        String InsertPhoneNumber = "CALL `AddNewPhone` ('" + phoneNumber + "','" + bPhoneNumber +"')";
        String getPhoneNumberID = "SELECT PhoneID from Phones where PhoneNumber='"+phoneNumber+"' AND BackupPhoneNumber='"+ bPhoneNumber +"'";
        try
        {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(InsertPhoneNumber);
            queryResult = statement.executeQuery(getPhoneNumberID);

            while(queryResult.next())
            {
                if (queryResult.getInt(1)>0) {
                    return queryResult.getInt(1);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }
    public int addNewHuman(String FirstName, String LastName, String EmailAddress, int PhoneID, String Login, String Password, Connection connectDB)
    {
        String addNewHuman = "INSERT INTO Human (FirstName, LastName, EmailAddress, PhoneID, Login, Password) " +
                "VALUES ('"+FirstName+"','"+LastName+"','"+EmailAddress+"',"+PhoneID+",'"+Login+"','"+Password+"')";
        //bazujac na tym ze login jest unikalny, wystarczy wyszukac nowy rekord po loginie, jesli nie trzeba to wyzej przepisywac xd
        String getHumanID = "SELECT HumanID from Human where Login = '"+Login+"'";
        try
        {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(addNewHuman);
            queryResult = statement.executeQuery(getHumanID);

            while(queryResult.next())
            {
                if (queryResult.getInt(1)>0) {
                    return queryResult.getInt(1);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }
    public int addNewClient(int ClientID, String ClientDrivingLicense, Connection connectDB)
    {
        String addNewClient = "INSERT INTO Clients (ClientID, ClientDrivingLicense, PreviousOrders, Verified) " +
                "VALUES ("+ClientID+",'"+ClientDrivingLicense+"',"+0+","+0+")";
        String getClientID = "SELECT ClientID from Clients where ClientID = '"+ClientID+"'";
        try
        {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(addNewClient);
            queryResult = statement.executeQuery(getClientID);

            while(queryResult.next())
            {
                if (queryResult.getInt(1)>0) {
                    return queryResult.getInt(1);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }
}
