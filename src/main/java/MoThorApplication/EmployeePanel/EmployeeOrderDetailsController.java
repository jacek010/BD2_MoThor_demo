package MoThorApplication.EmployeePanel;

import MoThorApplication.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class EmployeeOrderDetailsController {

    public Label loggedAsLabel;
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
    public Button deleteOrderButton;
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
    private Boolean orderDateUpdated;
    private Boolean orderCarUpdated;

    public void getData(int orderID) throws SQLException
    {
        orderDetailsWindowHeaderLabel.setText("Order no."+orderID+" details");
        impOrderID=orderID;
        orderDateUpdated=false;
        orderCarUpdated=false;

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
        if(impEmployeeID!=0) {
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

    public void recalculateFullPrice()
    {
        float carPricePerDay= Float.parseFloat(carPricePerDayTextField.getText());

        long daysBetween = ChronoUnit.DAYS.between(startDatePicker.getValue(), endDatePicker.getValue());
        float fullCost=Math.toIntExact(daysBetween)*carPricePerDay;

        fullPriceTextField.setText(Float.toString(fullCost));
    }


    public void onEmployeeIDChanged(ActionEvent event) throws SQLException
    {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        Statement statement = connectDB.createStatement();
        String newFirstName="", newLastName="";
        int countID=0;
        Integer newEmployeeID=Integer.parseInt(employeeIDTextField.getText());

        //System.out.println(newEmployeeID);

        if(newEmployeeID==0){
            newEmployeeID=impEmployeeID;
            employeeIDTextField.setText(newEmployeeID.toString());
        }
        String employeeInfoQuery = "SELECT count(1), FirstName, LastName FROM Human WHERE (HumanID ='" + newEmployeeID + "')AND(HumanID in (SELECT EmployeeID FROM Employees))";
        ResultSet results = statement.executeQuery(employeeInfoQuery);
        while (results.next()){
            countID=results.getInt(1);
            newFirstName=results.getString("FirstName");
            newLastName=results.getString("LastName");
        }
        if(countID==0){
            submitStatusLabel.setText("Nie ma pracownika o takim ID!");
            newEmployeeID=impEmployeeID;
            employeeIDTextField.setText(newEmployeeID.toString());
        }
        else{
            employeeFirstNameTextField.setText(newFirstName);
            employeeLastNameTextField.setText(newLastName);
            submitStatusLabel.setText("Od teraz pracownik obsługujący to zamówienie to "+newFirstName+" "+newLastName);
        }



    }

    public void onCarIDChanged(ActionEvent event) throws SQLException
    {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        Statement statement = connectDB.createStatement();
        String newCarName="",newCarTypeName="", newCarManufacturer="",newCarColor="";
        int countID=0;
        Integer newCarID=Integer.parseInt(carIDTextField.getText()), newCarEnginePower=0;
        Float newCarPricePerDay= 0.0F;

        if(newCarID==0){
            newCarID=impCarID;
            carIDTextField.setText(impCarID.toString());
        }
        String carIDInfoQuery="SELECT count(1) FROM ClientView WHERE CarID="+newCarID;
            ResultSet resultsID = statement.executeQuery(carIDInfoQuery);
            while (resultsID.next()) {
                countID = resultsID.getInt(1);
            }
        if(countID==0){
            submitStatusLabel.setText("Nie ma samochodu o takim ID!");
        }
        else{

            String carInfoQuery="SELECT* FROM ClientView WHERE CarID="+newCarID;
            ResultSet results = statement.executeQuery(carInfoQuery);
            while (results.next()) {
                countID = results.getInt(1);
                newCarName = results.getString("CarModelName");
                newCarTypeName = results.getString("CarTypeName");
                newCarManufacturer = results.getString("ManufacturerName");
                newCarColor = results.getString("Color");
                newCarEnginePower = results.getInt("EnginePower");
                newCarPricePerDay = results.getFloat("DailyLendingPrice");
            }
            submitStatusLabel.setText("Zmieniono samochód z "+impCarID+"(ID) na "+newCarID+"(ID)");

            carNameTextField.setText(newCarName);
            carManufacturerTextField.setText(newCarManufacturer);
            carTypeTextField.setText(newCarTypeName);
            carColorTextField.setText(newCarColor);
            carEnginePowerTextField.setText(newCarEnginePower.toString());
            carPricePerDayTextField.setText(newCarPricePerDay.toString());

            recalculateFullPrice();
        }

    }

    public void onStartDateChanged(ActionEvent event)
    {

        if(endDatePicker.getValue().isBefore(startDatePicker.getValue().plusDays(5))) endDatePicker.setValue(startDatePicker.getValue().plusDays(5));

        recalculateFullPrice();
    }

    public void onEndDateChanged(ActionEvent event)
    {

        if(endDatePicker.getValue().isBefore(startDatePicker.getValue().minusDays(5))) startDatePicker.setValue(endDatePicker.getValue().minusDays(5));

        recalculateFullPrice();
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

        String tempStartDate=startDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String tempEndDate=endDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        if(!impStartDate.equals(tempStartDate)||!impEndDate.equals(tempEndDate)) orderDateUpdated=true;
        if(!carIDTextField.getText().equals(impCarID.toString())) orderCarUpdated=true;


        if(orderDateUpdated){
            //zapytanie aktualizujące datę wypożyczenia w zamówieniu
            String updateOrderDate="UPDATE Orders SET StartDate='"+tempStartDate+"', EndDate='"+tempEndDate+"', FullLendingPrice="+fullPriceTextField.getText()+" WHERE OrderID="+impOrderID;
            statement.executeQuery(updateOrderDate);
        }

        if(orderCarUpdated){
            //zapytanie aktualizujące samochód w wypożyczeniu
            String updateOrderCar="UPDATE Orders SET CarID="+carIDTextField.getText()+", FullLendingPrice="+fullPriceTextField.getText()+" WHERE OrderID="+impOrderID;
            statement.executeQuery(updateOrderCar);
        }

        Stage stage = (Stage) submitChanges.getScene().getWindow();
        stage.close();

    }



    public void setDeleteOrderButtonOnAction(ActionEvent event) throws SQLException {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        Statement statement = connectDB.createStatement();

        String deleteQuery="DELETE FROM Orders WHERE OrderID="+impOrderID;


        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        deleteAlert.setTitle("You are deleting order!");
        deleteAlert.setContentText("Please confirm that you want to delete order!");


        Optional<ButtonType>resultOfConfirmation=deleteAlert.showAndWait();
        if(resultOfConfirmation.get()==ButtonType.OK){
            //System.out.println(deleteQuery);
            statement.executeQuery(deleteQuery);
            Stage stage = (Stage) deleteOrderButton.getScene().getWindow();
            stage.close();
        }
        else{
            System.out.println("No delete! :)");
        }


    }


    public void exitButtonOnAction(ActionEvent event){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
