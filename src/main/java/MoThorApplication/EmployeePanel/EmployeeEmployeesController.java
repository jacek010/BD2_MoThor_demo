package MoThorApplication.EmployeePanel;

import MoThorApplication.*;
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
    private TableColumn<EmployeeEmployeesListModel, String> employeeJobNameTableColumn;
    @FXML
    private TableColumn<EmployeeEmployeesListModel, Integer> employeeLentCarsTableColumn;
    @FXML
    private TableColumn<EmployeeEmployeesListModel, String> employeeEditTableColumn;
    @FXML
    private TextField keywordsEmployeesTextField;

    EmployeeEmployeesListModel employeesRecord;
    ObservableList<EmployeeEmployeesListModel> employeeEmployeesListModelObservableList = FXCollections.observableArrayList();

    boolean showOrderButtons = false;

    @Override
    public void initialize(URL url, ResourceBundle resource){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        showOrderButtons=false;

        if (DatabaseConnection.accessLevel!=DatabaseConnection.AccessLevelEnum.MANAGER)
        {
            System.out.println("Access denied on Employees Tab - not a manager.");
            return;
        }

        showEmployeeView();
    }

    public void showEmployeeView()
    {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        employeesListModelTableView.setItems(null);
        employeeEmployeesListModelObservableList.clear();


        String employeeViewQuery="SELECT * FROM EmployeesDetailsView";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(employeeViewQuery);

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
            employeeJobNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("JobName"));
            employeeLentCarsTableColumn.setCellValueFactory(new PropertyValueFactory<>("LentCars"));


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
                else if (employeesListModel.getJobName().toLowerCase().contains(searchKeyword)) return true;
                else if (employeesListModel.getLentCars().toString().contains(searchKeyword)) return true;
                else return false;

            }));

            SortedList<EmployeeEmployeesListModel> sortedData = new SortedList<>(filteredData);

            sortedData.comparatorProperty().bind(employeesListModelTableView.comparatorProperty());

            employeesListModelTableView.setItems(sortedData);

            Callback<TableColumn<EmployeeEmployeesListModel, String>, TableCell<EmployeeEmployeesListModel, String>> cellFactory = (TableColumn<EmployeeEmployeesListModel, String> param) ->
            {
                // make cell containing buttons
                return new TableCell<EmployeeEmployeesListModel, String>()
                {
                    @Override
                    public void updateItem(String item, boolean empty)
                    {
                        super.updateItem(item, empty);
                        //that cell created only on non-empty rows
                        if (empty)
                        {
                            setGraphic(null);
                        }
                        else
                        {
                            Button editButton = new Button("Edit");
                            editButton.setStyle("-fx-background-color: #1aa3ff;" + "");

                            editButton.setOnAction(event ->
                            {
                                employeesListModelTableView.getSelectionModel().select(this.getIndex());
                                employeesRecord = employeesListModelTableView.getSelectionModel().getSelectedItem();

                                FXMLLoader loader = new FXMLLoader();
                                // Show new form/panel after button click
                                loader.setLocation(PrimaryApplication.class.getResource("changeEmployeeInformationWindow.fxml"));
                                try {
                                    loader.load();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                                ChangeEmployeeInformationController changeEmployeeInformationController = loader.getController();
                                try {
                                    changeEmployeeInformationController.setFields(employeesRecord.getEmployeeID());
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                                Parent parent = loader.getRoot();
                                Stage stage = new Stage();
                                stage.setScene(new Scene(parent));
                                stage.initStyle(StageStyle.UTILITY);
                                stage.show();

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

            employeeEditTableColumn.setCellFactory(cellFactory);
            employeesListModelTableView.setItems(sortedData);


        } catch(SQLException e){
            Logger.getLogger(ListOfCarsController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }
}


