package MoThorApplication.EmployeePanel;

import MoThorApplication.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
    public RadioButton startedRadioButton;

    public RadioButton finishedRadioButton;

    public Button exitButton;
    public Label orderDetailsWindowHeaderLabel;
    public DatePicker startDatePicker;
    public DatePicker endDatePicker;

    private Integer impClientID, impEmployeeID, impCarID;
    private String  impClientFirstName, impClientLastName, impEmployeeFirstName, impEmployeeLastName, impCarName, impCarType, impCarManufacturer, impOrderStatus, impStartDate, impEndDate, impAdditionaInfo;

    public void setFields(int orderID) throws SQLException
    {
        orderDetailsWindowHeaderLabel.setText("Order no."+orderID+" details");

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
            impEmployeeFirstName = "not in view";
            impEmployeeLastName = "not in view";

            impCarName=results.getString("CarModelName");
            impCarType=results.getString("CarTypeName");
            impCarManufacturer=results.getString("ManufacturerName");

            impOrderStatus="Active";

            impStartDate=results.getString("StartDate");
            impEndDate=results.getString("EndDate");

            impAdditionaInfo= results.getString("AdditionalInfo");

        }

        clientIDTextField.setText(impClientID.toString());
        employeeIDTextField.setText(impEmployeeID.toString());
        carIDTextField.setText(impCarID.toString());

        clientFirstNameTextField.setText(impClientFirstName);
        clientLastNameTextField.setText(impClientLastName);

        employeeFirstNameTextField.setText(impEmployeeFirstName);
        employeeLastNameTextField.setText(impEmployeeLastName);

        carNameTextField.setText(impCarName);
        carTypeTextField.setText(impCarType);
        carManufacturerTextField.setText(impCarManufacturer);

        startDatePicker.setValue(LocalDate.parse(LocalDate.parse(impStartDate).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        endDatePicker.setValue(LocalDate.parse(LocalDate.parse(impEndDate).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        additionalInfoTextArea.setText(impAdditionaInfo);
    }


    public void exitButtonOnAction(ActionEvent event){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
