package MoThorApplication;

import MoThorApplication.Models.EmployeeEmployeesListModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChangeEmployeeInformationController {

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
    private ComboBox jobNameComboBox;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField secondPhoneTextField;
    @FXML
    private Label submitStatusLabel;
    @FXML
    private TextArea additionalInfoTextArea;
    @FXML
    private Label clientInformationWindowHeaderLabel;

    private int phoneID;
    private int employeeID;
    private int jobID;
    private String oldFirstName;
    private String oldLastName;
    private String oldEmailAddress;
    private String oldPhoneNumber;
    private String oldSecondPhoneNumber;
    private String oldJobName;
    private String oldAdditionalInfo;
    private Boolean isLoggedAsEmployee=false;

    private ObservableList<String> jobList = FXCollections.observableArrayList();;
    public void setFields(Integer employeeID) throws SQLException {

        jobList.clear();
        populateJobNameList();
        jobNameComboBox.setItems(jobList);
        this.employeeID=employeeID;

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        String firstName="", lastName="",emailAddress="", phoneNumber="", backupPhoneNumber="";
        int phoneID=0;

        String getUserDataQuery="SELECT h.FirstName, h.LastName, h.EmailAddress, p.PhoneNumber, p.BackupPhoneNumber, e.JobID, h.AdditionalInfo FROM Human h, Employees e, Phones p WHERE h.humanID = "+employeeID+" AND h.humanID = e.EmployeeID AND h.PhoneID = p.PhoneID";
        //System.out.println(getUserDataQuery);

        Statement statement = connectDB.createStatement();
        ResultSet queryResult = statement.executeQuery(getUserDataQuery);

        while(queryResult.next()){
            firstName=queryResult.getString("FirstName");
            lastName=queryResult.getString("LastName");
            emailAddress=queryResult.getString("EmailAddress");
            phoneNumber=queryResult.getString("PhoneNumber");
            backupPhoneNumber=queryResult.getString("BackupPhoneNumber");
            jobID=queryResult.getInt("JobID");
            additionalInfoTextArea.setText(queryResult.getString("AdditionalInfo"));
        }

        firstNameTextField.setText(firstName);
        oldFirstName=firstName;
        lastNameTextField.setText(lastName);
        oldLastName=lastName;
        emailTextField.setText(emailAddress);
        oldEmailAddress=emailAddress;
        this.phoneID=phoneID;
        phoneTextField.setText(phoneNumber);
        oldPhoneNumber=phoneNumber;
        secondPhoneTextField.setText(backupPhoneNumber);
        oldSecondPhoneNumber=backupPhoneNumber;
        oldAdditionalInfo=additionalInfoTextArea.getText();
        jobNameComboBox.getSelectionModel().select(jobID-1);
        oldJobName = (String) jobNameComboBox.getValue();
    }

    public boolean checkChanges()//zwraca prawdę, były zmiany
    {
            return !firstNameTextField.getText().equals(oldFirstName) ||
                    !lastNameTextField.getText().equals(oldLastName) ||
                    !emailTextField.getText().equals(oldEmailAddress) ||
                    !phoneTextField.getText().equals(oldPhoneNumber) ||
                    !secondPhoneTextField.getText().equals(oldSecondPhoneNumber) ||
                    additionalInfoTextArea.getText()==null||
                    !jobNameComboBox.getValue().equals(oldJobName)||
                    !additionalInfoTextArea.getText().equals(oldAdditionalInfo);
    }

    public void submitChangesButtonOnAction(ActionEvent event) throws SQLException {
        if (firstNameTextField.getText().isBlank() || lastNameTextField.getText().isBlank() || phoneTextField.getText().isBlank()) {
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
                    statement.executeQuery("UPDATE Human SET PhoneID=" + statement.executeQuery("SELECT PhoneID FROM Phones WHERE PhoneNumber='" + newPhoneNumber + "'").getInt(1) + " WHERE HumanID=" + employeeID);
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
                        statement.executeQuery("UPDATE Human SET PhoneID=(SELECT PhoneID FROM Phones WHERE PhoneNumber='" + newPhoneNumber + "') WHERE HumanID=" + employeeID);
                    }
                }
            }


            String updateHumanQuery, updateEmployeesQuery;

            updateHumanQuery = "UPDATE Human SET FirstName='" + firstNameTextField.getText()
                    + "', LastName='" + lastNameTextField.getText()
                    + "', EmailAddress='" + emailTextField.getText()
                    + "', AdditionalInfo='"+additionalInfoTextArea.getText()+"' WHERE HumanID=" + employeeID;
            updateEmployeesQuery = "UPDATE Employees SET JobID = "+getJobIDFromName((String)jobNameComboBox.getValue())+" WHERE EmployeeID=" + employeeID;

            statement.executeQuery(updateHumanQuery);
            statement.executeQuery(updateEmployeesQuery);

            Stage stage = (Stage) submitChanges.getScene().getWindow();
            stage.close();
        }
    }

    public void exitButtonOnAction(ActionEvent event){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
    public void populateJobNameList() throws SQLException
    {
        try {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();

            String getJobsQuery = "SELECT JobName FROM Jobs";
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(getJobsQuery);

            while (queryResult.next()) {
                jobList.add(queryResult.getString("JobName"));
            }
        }catch(SQLException e){
            Logger.getLogger(ChangeEmployeeInformationController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }
    public int getJobIDFromName(String jobName) throws SQLException
    {
        try {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();

            String getJobsQuery = "SELECT JobID FROM Jobs WHERE JobName = '"+jobName+"'";
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(getJobsQuery);

            while(queryResult.next())
            {
                if (queryResult.getInt(1)>0) {
                    return queryResult.getInt(1);
                }
            }
        }catch(SQLException e){
            Logger.getLogger(ChangeEmployeeInformationController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
        return 0;
    }
}
