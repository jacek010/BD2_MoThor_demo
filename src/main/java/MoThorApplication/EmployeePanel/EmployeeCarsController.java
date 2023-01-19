package MoThorApplication.EmployeePanel;

import MoThorApplication.DatabaseConnection;
import MoThorApplication.ListOfCarsController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class EmployeeCarsController {
    @FXML
    private TextField carIDTextField;
    @FXML
    private TextField carNameTextField;
    @FXML
    private TextField carTypeTextField;
    @FXML
    private TextField carManufacturerTextField;
    @FXML
    private TextField carColorTextField;
    @FXML
    private TextField carEnginePowerTextField;
    @FXML
    private TextField carPricePerDayTextField;
    @FXML
    private Button submitChanges;
    @FXML
    private Button exitButton;
    @FXML
    private Button deleteCarButton;
    @FXML
    private Label submitStatusLabel;
    @FXML
    private TextArea additionalInfoTextArea;
    @FXML
    private CheckBox maintenanceCheckBox;
    @FXML
    private CheckBox activeCheckBox;
    @FXML
    private Label carDetailsWindowHeaderLabel;

    @FXML
    private ComboBox carModelComboBox;
    @FXML
    private ComboBox carTypeComboBox;
    @FXML
    private ComboBox carManufacturerComboBox;
    @FXML
    private Label decomissionLabel;

    private String impCarName, impCarType, impCarManufacturer, impCarColor, impAdditionalInfo;
    private Integer impCarID, impCarEnginePower, impMaintenance, impActive, impDecomission;
    private Float impCarPricePerDay;

    public void onChanged(ActionEvent actionEvent) {
        //impCarName = carNameTextField.getText();
        //impCarType = carTypeTextField.getText();
        //impCarManufacturer = carManufacturerTextField.getText();
        //impCarColor = carColorTextField.getText();
        //impCarEnginePower = carEnginePowerTextField.getText();
        //impCarPricePerDay = carPricePerDayTextField.getText();
        //impMaintenance = ((maintenanceCheckBox.isSelected()) ? 1 : 0);
        //impAdditionalInfo = additionalInfoTextArea.getText();
        //impActive = activeCheckBox.toString();
    }

    public void submitButtonOnAction(ActionEvent actionEvent) throws SQLException {
        if(impDecomission == 0) {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();
            Statement statement = connectDB.createStatement();

            impMaintenance = ((maintenanceCheckBox.isSelected()) ? 1 : 0);
            impAdditionalInfo = additionalInfoTextArea.getText();

            String updateOrder="UPDATE Cars SET CarModel="+impCarName+"  WHERE OrderID="+impCarID;
            String updateQuery = "UPDATE Cars SET NeedMaintance="+impMaintenance+", AdditionalInfo ='"+impAdditionalInfo+"' WHERE CarID= "+impCarID;
            statement.executeQuery(updateQuery);

            Stage stage = (Stage) submitChanges.getScene().getWindow();
            stage.close();
        }
        else {
            Alert deleteAlert = new Alert(Alert.AlertType.ERROR);
            deleteAlert.setTitle("Car is decomissioned!");
            deleteAlert.setContentText("Unable to submit changes!");
            Optional<ButtonType> resultOfConfirmation=deleteAlert.showAndWait();
        }
    }

    public void setDecomissionCarButtonOnAction(ActionEvent actionEvent) throws SQLException {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        Statement statement = connectDB.createStatement();



        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        if(impDecomission == 1) {
            deleteAlert.setTitle("You are recommissioning car!");
            deleteAlert.setContentText("Please confirm that you want to recommission car!");
        }
        else {

            deleteAlert.setTitle("You are decommissioning car!");
            deleteAlert.setContentText("Please confirm that you want to decommission car!");
        }

        Optional<ButtonType> resultOfConfirmation=deleteAlert.showAndWait();
        if(resultOfConfirmation.get()==ButtonType.OK){
            //System.out.println(deleteQuery);
            impDecomission = ((impDecomission == 1)? 0 : 1 );
        String deleteQuery = "UPDATE Cars SET Commisioned="+impDecomission+" WHERE CarID="+impCarID;
            this.decomissionLabel.setText((impDecomission == 1) ? "Decomissioned" : "Active");
            statement.executeQuery(deleteQuery);
            Stage stage = (Stage) deleteCarButton.getScene().getWindow();
            stage.close();
        }
        else{
            System.out.println("No decomission!");
        }
    }

    public void getData() throws SQLException {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        Statement statement = connectDB.createStatement();

        this.carDetailsWindowHeaderLabel.setText("Car no."+impCarID+ " details");

        String dataQuery  = "SELECT * FROM ClientView WHERE CarID ="+impCarID;

        ResultSet results = statement.executeQuery(dataQuery);
        while(results.next()) {
            impCarName = results.getString("CarModelName");
            impCarManufacturer = results.getString("ManufacturerName");
            impCarType = results.getString("CarTypeName");
            impCarColor = results.getString("Color");
            impCarEnginePower = results.getInt("EnginePower");
            impCarPricePerDay = results.getFloat("DailyLendingPrice");
            impMaintenance = results.getInt("NeedMaintance");
            impActive = results.getInt("Active");
            impAdditionalInfo = results.getString("AdditionalInfo");
            impDecomission = results.getInt("Commisioned");
        }

        //this.carIDTextField.setText();
        this.carNameTextField.setText(impCarName);
        this.carTypeTextField.setText(impCarType);
        this.carManufacturerTextField.setText(impCarManufacturer);
        this.carColorTextField.setText(impCarColor);
        this.carEnginePowerTextField.setText(impCarEnginePower.toString());
        this.carPricePerDayTextField.setText(impCarPricePerDay.toString());
        this.maintenanceCheckBox.setSelected((impMaintenance == 0) ? false: true);
        //this.activeCheckBox.setSelected((impActive== 0) ? false: true);
        this.decomissionLabel.setText((impDecomission == 1) ? "Decomissioned" : "Active");
        this.additionalInfoTextArea.setText(impAdditionalInfo);

        if(impDecomission == 1) {
            this.deleteCarButton.setText("Recommission car");
            this.deleteCarButton.setStyle("-fx-background-color: #1aa3ff");
        }
        else {
            this.deleteCarButton.setText("Decommission car");
            this.deleteCarButton.setStyle("-fx-background-color: c43e3e");
        }
    }

    // Bez użycia - bo jednak na sztywno wpisujemy
    public void fillComboBoxes() throws SQLException {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        Statement statement = connectDB.createStatement();

        String modelQuery = "SELECT * FROM CarModels";
        String typeQuery = "SELECT * FROM CarTypes";
        String manufacturers = "SELECT * FROM CarManufacturers";

        ResultSet results = statement.executeQuery(modelQuery);

        while(results.next()) {
            carModelComboBox.getItems().add(results.getString("CarModelName"));
        }

        results = statement.executeQuery(typeQuery);
        while(results.next()) {
            carTypeComboBox.getItems().add(results.getString("CarTypeName"));
        }

        results = statement.executeQuery(manufacturers);
        while(results.next()) {
            carManufacturerComboBox.getItems().add(results.getString("ManufacturerName"));
        }

        carModelComboBox.getSelectionModel().select(0);
        carTypeComboBox.getSelectionModel().select(0);
        carManufacturerComboBox.getSelectionModel().select(0);
    }

    public void setFields(Integer carID){

        this.carDetailsWindowHeaderLabel.setText("Car no."+carID.toString() + " details");

        impCarID = carID;

        try {
            getData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void exitButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
