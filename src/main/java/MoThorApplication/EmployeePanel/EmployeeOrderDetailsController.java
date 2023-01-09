package MoThorApplication.EmployeePanel;

import MoThorApplication.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EmployeeOrderDetailsController {

    public TextField clientIDTextField;
    public TextField employeeIDTextField;
    public TextField clientFirstNameTextField;
    public TextField clientLastNameTextField;
    public TextField employeeFirstNameTextField;
    public TextField employeeLastNameTextField;
    public TextField carIDTextField;
    public TextField carNameTextField;
    public TextField carTypeTextField;
    public TextField carManufacturerTextField;
    public Button submitChanges;
    public Label submitStatusLabel;
    public TextArea additionalInfoTextArea;
    public ToggleGroup orderStatus;
    public RadioButton reservationRadioButton;
    public RadioButton activeRadioButton;

    public RadioButton finishedRadioButton;

    public Button exitButton;
    public Label orderDetailsWindowHeaderLabel;
    public DatePicker startDatePicker;
    public DatePicker endDatePicker;
    public TextField clientDrivingLicenseTextField;
    public TextField clientPhoneNumberTextField;
    public TextField clientEmailAddressTextField;
    public TextField carColorTextField;
    public TextField fullPriceTextField;
    public TextField carEnginePowerTextField;
    public TextField carPricePerDayTextField;

    private Integer impOrderID, impClientID, impEmployeeID, impCarID, impCarEnginePower;
    private Float impCarPricePerDay, impFullPrice;
    private String  impClientFirstName, impClientLastName, impClientDrivingLicense, impClientPhoneNumber, impClientEmailAddress, impEmployeeFirstName, impEmployeeLastName,
            impCarName, impCarType, impCarManufacturer, impCarColor, impOrderStatus, impStartDate, impEndDate, impAdditionalInfo;

    public void getData(int orderID) throws SQLException
    {
        orderDetailsWindowHeaderLabel.setText("Order no."+orderID+" details");
        impOrderID=orderID;

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        Statement statement = connectDB.createStatement();

        String setFieldsQuery="SELECT * FROM OrdersDetailsView WHERE OrderID="+orderID;

        ResultSet results = statement.executeQuery(setFieldsQuery);

        while(results.next()){
            impClientID=results.getInt("ClientID");
            impEmployeeID= results.getInt("EmployeeID");
            impCarID= results.getInt("CarID");

            impClientFirstName=results.getString("ClientFirstName");
            impClientLastName=results.getString("ClientLastName");
            impClientDrivingLicense = results.getString("ClientDrivingLicence");
            impClientEmailAddress = results.getString("ClientEmailAddress");
            impClientPhoneNumber = results.getString("ClientPhoneNumber");


            impCarName=results.getString("CarModelName");
            impCarType=results.getString("CarTypeName");
            impCarManufacturer=results.getString("ManufacturerName");
            impCarColor = results.getString("Color");
            impCarEnginePower = results.getInt("EnginePower");
            impCarPricePerDay = results.getFloat("DailyLendingPrice");

            impOrderStatus=results.getString("OrderStatus");

            impStartDate=results.getString("StartDate");
            impEndDate=results.getString("EndDate");

            impFullPrice=results.getFloat("FullLendingPrice");

            impAdditionalInfo = results.getString("AdditionalInfo");

        }

        // w momencie, gdy klient składa rezerwację, id pracownika jest puste, więc przypisane zostanie id tego, który to zamówienie zrealizuje
        if(impEmployeeID!=null) {
            String employeeInfoQuery = "SELECT FirstName, LastName FROM Human WHERE HumanID=" + impEmployeeID;
            results = statement.executeQuery(employeeInfoQuery);

            while (results.next()) {
                impEmployeeFirstName = results.getString("FirstName");
                impEmployeeLastName = results.getString("LastName");
            }
        }
        else{
            impEmployeeID=DatabaseConnection.loggedID;
            impEmployeeFirstName=DatabaseConnection.firstName;
            impEmployeeLastName=DatabaseConnection.lastName;
        }

        setFields();

    }

    public void setFields()
    {
        clientIDTextField.setText(impClientID.toString());
        employeeIDTextField.setText(impEmployeeID.toString());
        carIDTextField.setText(impCarID.toString());

        clientFirstNameTextField.setText(impClientFirstName);
        clientLastNameTextField.setText(impClientLastName);
        clientDrivingLicenseTextField.setText(impClientDrivingLicense);
        clientEmailAddressTextField.setText(impClientEmailAddress);
        clientPhoneNumberTextField.setText(impClientPhoneNumber);

        employeeFirstNameTextField.setText(impEmployeeFirstName);
        employeeLastNameTextField.setText(impEmployeeLastName);

        carNameTextField.setText(impCarName);
        carTypeTextField.setText(impCarType);
        carManufacturerTextField.setText(impCarManufacturer);
        carColorTextField.setText(impCarColor);
        carEnginePowerTextField.setText(impCarEnginePower.toString());
        carPricePerDayTextField.setText(impCarPricePerDay.toString());

        startDatePicker.setValue(LocalDate.parse(LocalDate.parse(impStartDate).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        endDatePicker.setValue(LocalDate.parse(LocalDate.parse(impEndDate).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        fullPriceTextField.setText(impFullPrice.toString());

        additionalInfoTextArea.setText(impAdditionalInfo);

        switch (impOrderStatus.toLowerCase()) {
            case "reservation" -> reservationRadioButton.setSelected(true);
            case "active" -> activeRadioButton.setSelected(true);
            case "finished" -> finishedRadioButton.setSelected(true);
        }
    }

    public void submitButtonOnAction(ActionEvent event) throws SQLException {
        String orderStatusAfter="";
        if(reservationRadioButton.isSelected()) orderStatusAfter="Reservation";
        else if (activeRadioButton.isSelected()) orderStatusAfter="Active";
        else if(finishedRadioButton.isSelected()) orderStatusAfter="Finished";

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        Statement statement = connectDB.createStatement();

        String updateOrder="UPDATE Orders SET OrderStatus='"+orderStatusAfter+"', EmployeeID="+Integer.valueOf(employeeIDTextField.getText())+", AdditionalInfo='"+additionalInfoTextArea.getText()+"' WHERE OrderID="+impOrderID;

        statement.executeQuery(updateOrder);

        Stage stage = (Stage) submitChanges.getScene().getWindow();
        stage.close();

    }




    public void exitButtonOnAction(ActionEvent event){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
