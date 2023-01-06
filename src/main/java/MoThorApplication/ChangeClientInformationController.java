package MoThorApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
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
    @FXML
    private TextArea additionalInfoTextArea;
    @FXML
    private CheckBox isVerifiedCheckBox;
    @FXML
    private AnchorPane employeeOptionsPane;
    @FXML
    private Label clientInformationWindowHeaderLabel;

    private int phoneID;
    private int clientID;
    private String oldFirstName;
    private String oldLastName;
    private String oldEmailAddress;
    private String oldClientDrivingLicense;
    private String oldPhoneNumber;
    private String oldSecondPhoneNumber;
    private String oldAdditionalInfo;
    private Boolean oldIsVerified;
    private Boolean isLoggedAsEmployee=false;

    public void setFields(Integer clientID) throws SQLException {

        this.clientID=clientID;
        isLoggedAsEmployee = ((DatabaseConnection.accessLevel== DatabaseConnection.AccessLevelEnum.EMPLOYEE) || (DatabaseConnection.accessLevel== DatabaseConnection.AccessLevelEnum.MANAGER));

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        String firstName="", lastName="",emailAddress="", clientDrivingLicense="", phoneNumber="", backupPhoneNumber="";
        int phoneID=0;

        String getUserDataQuery="SELECT h.FirstName, h.LastName, h.EmailAddress, c.ClientDrivingLicense,h.PhoneID, p.PhoneNumber, p.BackupPhoneNumber " +
                "FROM Human h, Clients c, Phones p " +
                "WHERE h.HumanID="+clientID+" AND h.HumanID=c.ClientID AND h.PhoneID=p.PhoneID";
        //System.out.println(getUserDataQuery);

        Statement statement = connectDB.createStatement();
        ResultSet queryResult = statement.executeQuery(getUserDataQuery);

        while(queryResult.next()){
            firstName=queryResult.getString("FirstName");
            lastName=queryResult.getString("LastName");
            emailAddress=queryResult.getString("EmailAddress");
            clientDrivingLicense=queryResult.getString("ClientDrivingLicense");
            phoneID=queryResult.getInt("PhoneID");
            phoneNumber=queryResult.getString("PhoneNumber");
            backupPhoneNumber=queryResult.getString("BackupPhoneNumber");
        }

        firstNameTextField.setText(firstName);
        oldFirstName=firstName;
        lastNameTextField.setText(lastName);
        oldLastName=lastName;
        emailTextField.setText(emailAddress);
        oldEmailAddress=emailAddress;
        driverLicenseTextField.setText(clientDrivingLicense);
        oldClientDrivingLicense=clientDrivingLicense;
        this.phoneID=phoneID;
        phoneTextField.setText(phoneNumber);
        oldPhoneNumber=phoneNumber;
        secondPhoneTextField.setText(backupPhoneNumber);
        oldSecondPhoneNumber=backupPhoneNumber;



        //dodatkowe opcje edycji danych użytkownika pojawią się tylko, gdy będzie zalogowany pracownik
        if(isLoggedAsEmployee) {
            clientInformationWindowHeaderLabel.setText(oldFirstName+" "+oldLastName+" Account Information");
            String employeeViewQuery="SELECT c.Verified, h.AdditionalInfo FROM Clients c, Human h WHERE h.HumanID="+clientID+" AND c.ClientID=h.HumanID";
            queryResult= statement.executeQuery(employeeViewQuery);

            while (queryResult.next()){
                additionalInfoTextArea.setText(queryResult.getString("AdditionalInfo"));
                oldAdditionalInfo=additionalInfoTextArea.getText();
                isVerifiedCheckBox.setSelected(queryResult.getInt("Verified") == 1);
                oldIsVerified=isVerifiedCheckBox.isSelected();
            }

            employeeOptionsPane.setVisible(true);
        }
        else employeeOptionsPane.setVisible(false);



    }

    public boolean checkChanges()//zwraca prawdę, były zmiany
    {
        if(isLoggedAsEmployee) {
            return !firstNameTextField.getText().equals(oldFirstName) || !lastNameTextField.getText().equals(oldLastName) || !emailTextField.getText().equals(oldEmailAddress) || !driverLicenseTextField.getText().equals(oldClientDrivingLicense)
                    || !phoneTextField.getText().equals(oldPhoneNumber) || !secondPhoneTextField.getText().equals(oldSecondPhoneNumber) || !additionalInfoTextArea.getText().equals(oldAdditionalInfo)||!(isVerifiedCheckBox.isSelected()==oldIsVerified);
        }
        else{
            return !firstNameTextField.getText().equals(oldFirstName) || !lastNameTextField.getText().equals(oldLastName) || !emailTextField.getText().equals(oldEmailAddress) || !driverLicenseTextField.getText().equals(oldClientDrivingLicense)
                    || !phoneTextField.getText().equals(oldPhoneNumber) || !secondPhoneTextField.getText().equals(oldSecondPhoneNumber);
        }
    }

    public void submitChangesButtonOnAction(ActionEvent event) throws SQLException {
        if (firstNameTextField.getText().isBlank() || lastNameTextField.getText().isBlank() || driverLicenseTextField.getText().isBlank() || phoneTextField.getText().isBlank()) {
            submitStatusLabel.setText("Please fill all red fields!");
        } else {
            if (!checkChanges()) {
                Stage stage = (Stage) exitButton.getScene().getWindow();
                stage.close();
                return;
            }
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();
            Statement statement = connectDB.createStatement();

            if (!oldPhoneNumber.equals(phoneTextField.getText())) {
                String newPhoneNumber = phoneTextField.getText();

                //sprawdzamy czy nowy numer telefonu jest już w naszej tabeli PHONES - gdy tak, to po prostu przypisujemy osobie nowe PhoneID
                ResultSet result1 = statement.executeQuery("SELECT PhoneID FROM Phones WHERE PhoneNumber='" + newPhoneNumber + "'");
                int phoneIDCount = 0;
                while (result1.next()) phoneIDCount = result1.getInt("PhoneID");
                if (phoneIDCount > 0) {
                    System.out.println("NewPhoneIndex: " + statement.executeQuery("SELECT PhoneID FROM Phones WHERE PhoneNumber='" + newPhoneNumber + "'").getInt(1));
                    statement.executeQuery("UPDATE Human SET PhoneID=" + statement.executeQuery("SELECT PhoneID FROM Phones WHERE PhoneNumber='" + newPhoneNumber + "'").getInt(1) + " WHERE HumanID=" + clientID);
                } else {
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
                        statement.executeQuery("UPDATE Human SET PhoneID=(SELECT PhoneID FROM Phones WHERE PhoneNumber='" + newPhoneNumber + "') WHERE HumanID=" + clientID);
                    }
                }
            }


            String updateHumanQuery, updateClientQuery;


            if(isLoggedAsEmployee){
                updateHumanQuery = "UPDATE Human SET FirstName='" + firstNameTextField.getText()
                        + "', LastName='" + lastNameTextField.getText()
                        + "', EmailAddress='" + emailTextField.getText()
                        + "', AdditionalInfo='"+additionalInfoTextArea.getText()+"' WHERE HumanID=" + clientID;
                int verifiedBox=isVerifiedCheckBox.isSelected() ? 1:0;
                updateClientQuery = "UPDATE Clients SET ClientDrivingLicense='" + driverLicenseTextField.getText()
                        + "', Verified="+verifiedBox+" WHERE ClientID=" + clientID;
            }
            else{
                updateHumanQuery = "UPDATE Human SET FirstName='" + firstNameTextField.getText()
                        + "', LastName='" + lastNameTextField.getText()
                        + "', EmailAddress='" + emailTextField.getText() + "' WHERE HumanID=" + clientID;
                updateClientQuery = "UPDATE Clients SET ClientDrivingLicense='" + driverLicenseTextField.getText() + "', Verified=0 WHERE ClientID=" + clientID;
            }

            statement.executeQuery(updateHumanQuery);
            statement.executeQuery(updateClientQuery);

            Stage stage = (Stage) submitChanges.getScene().getWindow();
            stage.close();
        }
    }

    public void exitButtonOnAction(ActionEvent event){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
