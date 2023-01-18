package MoThorApplication.EmployeePanel;

import MoThorApplication.*;
import MoThorApplication.Models.EmployeeClientListModel;
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

public class EmployeeClientsController implements Initializable {
    @FXML
    private Button exitButton;
    @FXML
    private TableView<EmployeeClientListModel> clientsListModelTableView;
    @FXML
    private TableColumn<EmployeeClientListModel, Integer> clientIDTableColumn;
    @FXML
    private TableColumn<EmployeeClientListModel, String> clientFirstNameTableColumn;
    @FXML
    private TableColumn<EmployeeClientListModel, String> clientLastNameTableColumn;
    @FXML
    private TableColumn<EmployeeClientListModel, String> clientDrivingLicenseTableColumn;
    @FXML
    private TableColumn<EmployeeClientListModel, String> clientPhoneNumberTableColumn;
    @FXML
    private TableColumn<EmployeeClientListModel, String> clientEmailAddressTableColumn;
    @FXML
    private TableColumn<EmployeeClientListModel, String> clientPreviousOrdersTableColumn;
    @FXML
    private TableColumn<EmployeeClientListModel, String> clientViewOrdersTableColumn;
    @FXML
    private TableColumn<EmployeeClientListModel, Integer> clientVerifiedStatusTableColumn;
    @FXML
    private TextField keywordsClientsTextField;

    EmployeeClientListModel clientsRecord;

    ObservableList<EmployeeClientListModel> employeeClientListModelObservableList = FXCollections.observableArrayList();

    boolean showOrderButtons = false;

    @Override
    public void initialize(URL url, ResourceBundle resource){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        showOrderButtons=false;
        setEmployeeAccessLevel(connectDB);
        showEmployeeView();
    }

    public void showEmployeeView()
    {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        clientsListModelTableView.setItems(null);
        employeeClientListModelObservableList.clear();


        String clientViewQuery="SELECT * FROM ClientDetailsView";
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(clientViewQuery);

            System.out.println(queryResult + "HERE");

            while (queryResult.next()) {

                    Integer queryClientID = queryResult.getInt("ClientID");
                    String queryClientFirstName = queryResult.getString("ClientFirstName");
                    String queryClientLastName = queryResult.getString("ClientLastName");
                    String queryClientDrivingLicense = queryResult.getString("ClientDrivingLicence");
                    String queryClientPhoneNumber = queryResult.getString("ClientPhoneNUmber");
                    String queryClientEmailAddress = queryResult.getString("ClientEmailAddress");
                    Integer queryPreviousOrders = queryResult.getInt("PreviousOrders");
                    Integer queryVerified = queryResult.getInt("verified");
                    String queryAdditionalInfo = queryResult.getString("additionalInfo");
                    employeeClientListModelObservableList.add(new EmployeeClientListModel(queryClientID,queryClientFirstName,queryClientLastName,queryClientDrivingLicense,queryClientPhoneNumber,queryClientEmailAddress,queryPreviousOrders,queryVerified,queryAdditionalInfo));

            }

            clientIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("clientID"));
            clientFirstNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("clientFirstName"));
            clientLastNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("clientLastName"));
            clientDrivingLicenseTableColumn.setCellValueFactory(new PropertyValueFactory<>("clientDrivingLicense"));
            clientPhoneNumberTableColumn.setCellValueFactory(new PropertyValueFactory<>("clientPhoneNumber"));
            clientEmailAddressTableColumn.setCellValueFactory(new PropertyValueFactory<>("clientEmailAddress"));
            clientVerifiedStatusTableColumn.setCellValueFactory(new PropertyValueFactory<>("verified"));
            clientPreviousOrdersTableColumn.setCellValueFactory(new PropertyValueFactory<>("previousOrders"));

            //add cell of button edit

            clientsListModelTableView.setItems(employeeClientListModelObservableList);

            FilteredList<EmployeeClientListModel> filteredData = new FilteredList<>(employeeClientListModelObservableList, b -> true);

            keywordsClientsTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(clientsListModel -> {


                //gdy nie ma żadnego słowa kluczowego wyświetla wszystkie dostępne rekordy
                if (newValue.isEmpty() || newValue.isBlank()) return true;

                String searchKeyword = newValue.toLowerCase();
                if (clientsListModel.getClientID().toString().contains(searchKeyword)) return true;
                else if (clientsListModel.getClientFirstName().toLowerCase().contains(searchKeyword)) return true;
                else if (clientsListModel.getClientLastName().toLowerCase().contains(searchKeyword)) return true;
                else if (clientsListModel.getClientDrivingLicense().toLowerCase().contains(searchKeyword)) return true;
                else if (clientsListModel.getClientPhoneNumber().toLowerCase().contains(searchKeyword)) return true;
                else if (clientsListModel.getClientEmailAddress().toLowerCase().contains(searchKeyword)) return true;
                else if (clientsListModel.getPreviousOrders().toString().contains(searchKeyword)) return true;
                else if (clientsListModel.getVerified().toString().contains(searchKeyword)) return true;
                else return false;

            }));

            SortedList<EmployeeClientListModel> sortedData = new SortedList<>(filteredData);

            sortedData.comparatorProperty().bind(clientsListModelTableView.comparatorProperty());

            clientsListModelTableView.setItems(sortedData);

            Callback<TableColumn<EmployeeClientListModel, String>, TableCell<EmployeeClientListModel, String>> cellFactory = (TableColumn<EmployeeClientListModel, String> param) -> {
                // make cell containing buttons

                return new TableCell<EmployeeClientListModel, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        //that cell created only on non-empty rows
                        if (empty) {
                            setGraphic(null);

                        } else {

                            //FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.CAR);
                            Button editButton = new Button("Edit");
                            editButton.setStyle("-fx-background-color: #1aa3ff;" +
                                    "");
                            //editButton.setDisable(!showOrderButtons);

                            editButton.setOnMouseClicked((MouseEvent event) -> {
                                clientsListModelTableView.getSelectionModel().select(this.getIndex());
                                clientsRecord = clientsListModelTableView.getSelectionModel().getSelectedItem();

                                FXMLLoader loader = new FXMLLoader();
                                // Show new form/panel after button click
                                loader.setLocation(PrimaryApplication.class.getResource("changeClientInformationWindow.fxml"));
                                try {
                                    loader.load();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                                ChangeClientInformationController changeClientInformationController = loader.getController();
                                try {
                                    changeClientInformationController.setFields(clientsRecord.getClientID());
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
            clientViewOrdersTableColumn.setCellFactory(cellFactory);
            clientsListModelTableView.setItems(sortedData);


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
