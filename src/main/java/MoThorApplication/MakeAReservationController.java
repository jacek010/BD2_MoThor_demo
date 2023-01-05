package MoThorApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.ResourceBundle;

public class MakeAReservationController{

    @FXML
        private TextField firstNameTextField;
    @FXML
        private TextField lastNameTextField;
    @FXML
        private TextField carIDTextField;
    @FXML
        private TextField carNameTextField;
    @FXML
        private TextField carManufacturerTextField;
    @FXML
        private TextField carTypeTextField;
    @FXML
        private TextField carColorTextField;
    @FXML
        private TextField enginePowerTextField;
    @FXML
        private TextField pricePerDayTextField;
    @FXML
        private TextField fullCostTextField;
    @FXML
        private Button acceptButton;
    @FXML
        private Button cancelButton;
    @FXML
        private DatePicker startDateField;
    @FXML
        private DatePicker endDateField;
    float fullCost;


    public void setFields(Integer carIDTextField, String carNameTextField, String carManufacturerTextField, String carTypeTextField, String carColorTextField, Integer enginePowerTextField, Float pricePerDayTextField, LocalDate startDateField, LocalDate endDateField) {
        long daysBetween = ChronoUnit.DAYS.between(startDateField, endDateField);
        fullCost=Math.toIntExact(daysBetween)*pricePerDayTextField;

        this.firstNameTextField.setText(DatabaseConnection.firstName);
        this.lastNameTextField.setText(DatabaseConnection.lastName);

        this.carIDTextField.setText(carIDTextField.toString());
        this.carNameTextField.setText(carNameTextField);
        this.carManufacturerTextField.setText(carManufacturerTextField);
        this.carTypeTextField.setText(carTypeTextField);
        this.carColorTextField.setText(carColorTextField);
        this.enginePowerTextField.setText(enginePowerTextField.toString());
        this.pricePerDayTextField.setText(pricePerDayTextField.toString());
        this.startDateField.setValue(startDateField);
        this.endDateField.setValue(endDateField);
        this.fullCostTextField.setText(Float.toString(fullCost));


    }

    public void acceptButtonOnAction(ActionEvent event){

        DatabaseConnection.loggedID=10;
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        //wysłąnie kwerendy INSERT do bazy danych
        String insertQuery="INSERT INTO Orders (ClientID, EmployeeID, CarID, StartDate, EndDate, OrderStatus, FullLendingPrice, AdditionalInfo) " +
                "VALUES ("+DatabaseConnection.loggedID
                +", null, "+carIDTextField.getText()
                +", '"+startDateField.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                +"', '"+endDateField.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                +"', 'reservation', "+fullCost+", null)";

        try{
            Statement statement = connectDB.createStatement();
            statement.executeQuery(insertQuery);
        } catch (SQLException e){
            e.printStackTrace();
        }
        //getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")
        cancelButtonOnAction(event);
    }
    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
