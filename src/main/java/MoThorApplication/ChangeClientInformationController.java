package MoThorApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ChangeClientInformationController {

    @FXML
    private Button exitButton;
    @FXML
    private Button submitChanges;
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
    private Label submitStatusLabel;

    private int phoneID;
    private String oldFirstName;
    private String oldLastName;
    private String oldEmailAddress;
    private String oldClientDrivingLicense;
    private String oldPhoneNumber;
    private String oldSecondPhoneNumber;

    public void setFields(String firstName, String lastName, String emailAddress, String clientDrivingLicense,Integer phoneID, String phoneNumber, String backupPhoneNumber)
    {
        this.firstNameTextField.setText(firstName);
        oldFirstName=firstName;
        this.lastNameTextField.setText(lastName);
        oldLastName=lastName;
        this.emailTextField.setText(emailAddress);
        oldEmailAddress=emailAddress;
        this.driverLicenseTextField.setText(clientDrivingLicense);
        oldEmailAddress=emailAddress;
        this.phoneID=phoneID;
        this.phoneTextField.setText(phoneNumber);
        oldPhoneNumber=phoneNumber;
        this.secondPhoneTextField.setText(backupPhoneNumber);
        oldSecondPhoneNumber=backupPhoneNumber;


    }

    public boolean checkChanges()//zwraca prawdę, były zmiany
    {
        return !firstNameTextField.getText().equals(oldFirstName) || !lastNameTextField.getText().equals(oldLastName) || !emailTextField.getText().equals(oldEmailAddress) || !driverLicenseTextField.getText().equals(oldClientDrivingLicense)
                || !phoneTextField.getText().equals(oldPhoneNumber) || !secondPhoneTextField.getText().equals(oldSecondPhoneNumber);
    }

    public void submitChangesButtonOnAction(ActionEvent event) throws SQLException {
        if (firstNameTextField.getText().isBlank() || lastNameTextField.getText().isBlank() || driverLicenseTextField.getText().isBlank() || phoneTextField.getText().isBlank()) {
            submitStatusLabel.setText("Please fill all red fields!");
        } else {
            if(!checkChanges()){
                Stage stage = (Stage) exitButton.getScene().getWindow();
                stage.close();
                return;
            }
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();
            Statement statement = connectDB.createStatement();

            if (!oldPhoneNumber.equals(phoneTextField.getText())) {
                String newPhoneNumber=phoneTextField.getText();

                //sprawdzamy czy nowy numer telefonu jest już w naszej tabeli PHONES - gdy tak, to po prostu przypisujemy osobie nowe PhoneID
                ResultSet result1 = statement.executeQuery("SELECT PhoneID FROM Phones WHERE PhoneNumber='"+newPhoneNumber+"'");
                int phoneIDCount=0;
                while(result1.next()) phoneIDCount=result1.getInt("PhoneID");
                if(phoneIDCount>0){
                    System.out.println("NewPhoneIndex: "+statement.executeQuery("SELECT PhoneID FROM Phones WHERE PhoneNumber='" + newPhoneNumber+"'").getInt(1));
                    statement.executeQuery("UPDATE Human SET PhoneID="+statement.executeQuery("SELECT PhoneID FROM Phones WHERE PhoneNumber='" + newPhoneNumber+"'").getInt(1)+" WHERE HumanID="+DatabaseConnection.loggedID);
                }else {
                    //sprawdzamy czy tylko ta osoba ma ten numer telefonu jako główny - gdy tak, to po prostu zmieniamy numer w tabeli PHONES pod odpowiednim PhoneID
                    ResultSet result2 = statement.executeQuery("SELECT count(1) FROM Human WHERE PhoneID=" + phoneID);
                    int phoneIDCount2 = 0;
                    while (result2.next()) phoneIDCount2 = result2.getInt(1);
                    if (phoneIDCount2 == 1) {
                        statement.executeQuery("UPDATE Phones SET PhoneNumber='" + newPhoneNumber + "', BackupPhoneNumber='" + secondPhoneTextField.getText() + "' WHERE PhoneID=" + phoneID);
                    }
                    //musimy stworzyć nowy rekord w tabeli PHONES i jego PhoneID przypisać do PhoneID osoby
                    else {
                        statement.executeQuery("INSERT INTO Phones (PhoneNumber, BackupPhoneNumber) VALUES('" + newPhoneNumber + "', '" + secondPhoneTextField.getText() + "')");
                        statement.executeQuery("UPDATE Human SET PhoneID=(SELECT PhoneID FROM Phones WHERE PhoneNumber='" + newPhoneNumber + "') WHERE HumanID=" + DatabaseConnection.loggedID);
                    }
                }
            }

                    String updateHumanQuery = "UPDATE Human SET FirstName='" + firstNameTextField.getText()
                            + "', LastName='" + lastNameTextField.getText()
                            + "', EmailAddress='"+emailTextField.getText()+"' WHERE HumanID="+DatabaseConnection.loggedID;
                    String updateClientQuery="UPDATE Clients SET ClientDrivingLicense='"+driverLicenseTextField.getText()+"', Verified=0 WHERE ClientID="+DatabaseConnection.loggedID;

                    statement.executeQuery(updateHumanQuery);
                    statement.executeQuery(updateClientQuery);
        }
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    public void exitButtonOnAction(ActionEvent event){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
