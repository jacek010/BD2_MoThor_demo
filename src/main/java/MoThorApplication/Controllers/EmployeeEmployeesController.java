package MoThorApplication.Controllers;

import MoThorApplication.DatabaseConnection;
import MoThorApplication.ListOfCarsController;
import MoThorApplication.MakeAReservationController;
import MoThorApplication.Models.EmployeeClientListModel;
import MoThorApplication.Models.EmployeeEmployeesListModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmployeeEmployeesController implements Initializable {
    @FXML
    private Button exitButton;
    @FXML
    private TableView<EmployeeEmployeesListModel> employeesListModelTableView;
    @FXML
    private TableColumn<EmployeeEmployeesListModel, Integer> employeeIDTableColumn;
    @FXML
    private TableColumn<EmployeeEmployeesListModel, String> employeeFirstNameTableColumn;
    @FXML
    private TableColumn<EmployeeEmployeesListModel, String> employeeLastNameTableColumn;
    @FXML
    private TableColumn<EmployeeEmployeesListModel, String> employeePhoneNumberTableColumn;
    @FXML
    private TableColumn<EmployeeEmployeesListModel, String> employeeEmailAddressTableColumn;
    @FXML
    private TableColumn<EmployeeEmployeesListModel, String> employeePromoteTableColumn;
    @FXML
    private TextField keywordsEmployeesTextField;

    EmployeeEmployeesListModel carRecord;

    ObservableList<EmployeeEmployeesListModel> employeeEmployeesListModelObservableList = FXCollections.observableArrayList();

    boolean showOrderButtons = false;

    @Override
    public void initialize(URL url, ResourceBundle resource){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        showOrderButtons=false;
        setEmployeeAccessLevel(connectDB);
        if (DatabaseConnection.accessLevel!=DatabaseConnection.AccessLevelEnum.MANAGER)
        {
            System.out.println("Access denied on Employees Tab - not a manager.");
            return;
        }
        String employeeViewQuery="SELECT * FROM EmployeesDetailsView WHERE JobName != 'Manager' AND JobName != 'Administrator'";

        showEmployeeView(connectDB, employeeViewQuery);
    }

    public void showEmployeeView(Connection connectDB,String query)
    {
        employeesListModelTableView.setItems(null);
        employeeEmployeesListModelObservableList.clear();

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            System.out.println(queryResult);

            while (queryResult.next()) {
                Integer queryEmployeeID = queryResult.getInt("EmployeeID");
                String queryEmployeeFirstName = queryResult.getString("EmployeeFirstName");
                String queryEmployeeLastName = queryResult.getString("EmployeeLastName");
                String queryEmployeePhoneNumber = queryResult.getString("EmployeePhoneNUmber");
                String queryEmployeeEmailAddress = queryResult.getString("EmployeeEmailAdress");
                String queryJobName = queryResult.getString("JobName");
                Integer queryLentCars = queryResult.getInt("LentCars");
                String queryAdditionalInfo = queryResult.getString("AdditionalInfo");
                employeeEmployeesListModelObservableList.add(new EmployeeEmployeesListModel(queryEmployeeID,
                                                                                            queryEmployeeFirstName,
                                                                                            queryEmployeeLastName,
                                                                                            queryEmployeePhoneNumber,
                                                                                            queryEmployeeEmailAddress,
                                                                                            queryJobName,
                                                                                            queryLentCars,
                                                                                            queryAdditionalInfo));
            }

            employeeIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
            employeeFirstNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("employeeFirstName"));
            employeeLastNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("employeeLastName"));
            employeePhoneNumberTableColumn.setCellValueFactory(new PropertyValueFactory<>("employeePhoneNumber"));
            employeeEmailAddressTableColumn.setCellValueFactory(new PropertyValueFactory<>("employeeEmailAddress"));

            //add cell of button edit

            employeesListModelTableView.setItems(employeeEmployeesListModelObservableList);

            FilteredList<EmployeeEmployeesListModel> filteredData = new FilteredList<>(employeeEmployeesListModelObservableList, b -> true);

            keywordsEmployeesTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(employeesListModel -> {


                //gdy nie ma żadnego słowa kluczowego wyświetla wszystkie dostępne rekordy
                if (newValue.isEmpty() || newValue.isBlank()) return true;

                String searchKeyword = newValue.toLowerCase();
                if (employeesListModel.getEmployeeID().toString().contains(searchKeyword)) return true;
                else if (employeesListModel.getEmployeeFirstName().toLowerCase().contains(searchKeyword)) return true;
                else if (employeesListModel.getEmployeeLastName().toLowerCase().contains(searchKeyword)) return true;
                else if (employeesListModel.getEmployeePhoneNumber().toLowerCase().contains(searchKeyword)) return true;
                else if (employeesListModel.getEmployeeEmailAddress().toLowerCase().contains(searchKeyword)) return true;
                else return false;

            }));

            SortedList<EmployeeEmployeesListModel> sortedData = new SortedList<>(filteredData);

            sortedData.comparatorProperty().bind(employeesListModelTableView.comparatorProperty());

            employeesListModelTableView.setItems(sortedData);

            Callback<TableColumn<EmployeeEmployeesListModel, String>, TableCell<EmployeeEmployeesListModel, String>> cellFactory = (TableColumn<EmployeeEmployeesListModel, String> param) -> {
                // make cell containing buttons

                return new TableCell<EmployeeEmployeesListModel, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        //that cell created only on non-empty rows
                        if (empty) {
                            setGraphic(null);

                        } else {

                            //FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.CAR);
                            Button editButton = new Button("Promote");
                            editButton.setStyle("-fx-background-color: #1aa3ff;" +
                                    "");
                            //editButton.setDisable(!showOrderButtons);

                            editButton.setOnMouseClicked((MouseEvent event) -> {
                                //promocja praownika do menedzera
                            });

                            HBox manageBtn = new HBox(editButton);
                            manageBtn.setStyle("-fx-alignment:center");
                            HBox.setMargin(editButton, new Insets(2, 3, 0, 2));

                            setGraphic(manageBtn);
                        }
                        setText(null);

                    }

                };
            };

            employeePromoteTableColumn.setCellFactory(cellFactory);
            employeesListModelTableView.setItems(sortedData);


        } catch(SQLException e){
            Logger.getLogger(ListOfCarsController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }


    }


    public void setEmployeeAccessLevel(Connection connectDB)
    {
        DatabaseConnection.accessLevel= DatabaseConnection.AccessLevelEnum.UNVERIFIED;
        String query = "select JobID from Employees where EmployeeID="+DatabaseConnection.loggedID;
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            while(queryResult.next())
            {
                if(queryResult.getInt(1)==4 || queryResult.getInt(1) == 5) DatabaseConnection.accessLevel= DatabaseConnection.AccessLevelEnum.MANAGER;
                else DatabaseConnection.accessLevel= DatabaseConnection.AccessLevelEnum.EMPLOYEE;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void exitButtonOnAction(ActionEvent ignoredEvent){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}


