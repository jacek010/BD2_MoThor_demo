package MoThorApplication;

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
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListOfClientsController implements Initializable {
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
    private TextField keywordsClientsTextField;

    // TODO: wyznaczyc odpowiednie miejsce, do wyswietlania jako kto jest zalogowanym (i jakie pracownicze stanowisko ma) - najlepiej by pasowało po prawej stronie tabów.
    @FXML
    private Label loggedAsLabel;

    EmployeeClientListModel carRecord;

    ObservableList<EmployeeClientListModel> employeeClientListModelObservableList = FXCollections.observableArrayList();

    boolean showOrderButtons = false;

    @Override
    public void initialize(URL url, ResourceBundle resource){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        showOrderButtons=false;
        setClientAccessLevel(connectDB);

        // TODO: to samo co wyżej do lable.
        //loggedAsLabel.setText("You are logged as: "+DatabaseConnection.firstName+" "+DatabaseConnection.lastName);

        String clientViewQuery="SELECT * FROM ClientDetailsView";

        showEmployeeView(connectDB, clientViewQuery);
    }

    public void showEmployeeView(Connection connectDB,String query)
    {
        clientsListModelTableView.setItems(null);
        employeeClientListModelObservableList.clear();

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            System.out.println(queryResult);

            while (queryResult.next()) {
                Integer queryVerified = queryResult.getInt("verified");
                if(queryVerified == 1) {
                    Integer queryClientID = queryResult.getInt("ClientID");
                    String queryClientFirstName = queryResult.getString("ClientFirstName");
                    String queryClientLastName = queryResult.getString("ClientLastName");
                    String queryClientDrivingLicense = queryResult.getString("ClientDrivingLicence");
                    String queryClientPhoneNumber = queryResult.getString("ClientPhoneNUmber");
                    String queryClientEmailAddress = queryResult.getString("ClientEmailAddress");
                    Integer queryPreviousOrders = queryResult.getInt("PreviousOrders");
                    String queryAdditionalInfo = queryResult.getString("additionalInfo");
                    employeeClientListModelObservableList.add(new EmployeeClientListModel(queryClientID,queryClientFirstName,queryClientLastName,queryClientDrivingLicense,queryClientPhoneNumber,queryClientEmailAddress,queryPreviousOrders,queryVerified,queryAdditionalInfo));
                }
            }

            clientIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("clientID"));
            clientFirstNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("clientFirstName"));
            clientLastNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("clientLastName"));
            clientDrivingLicenseTableColumn.setCellValueFactory(new PropertyValueFactory<>("clientDrivingLicense"));
            clientPhoneNumberTableColumn.setCellValueFactory(new PropertyValueFactory<>("clientPhoneNumber"));
            clientEmailAddressTableColumn.setCellValueFactory(new PropertyValueFactory<>("clientEmailAddress"));
            clientPreviousOrdersTableColumn.setCellValueFactory(new PropertyValueFactory<>("clientPreviousOrders"));

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
                            Button editButton = new Button("Orders");
                            editButton.setStyle("-fx-background-color: #1aa3ff;" +
                                    "");
                            editButton.setDisable(!showOrderButtons);

                            editButton.setOnMouseClicked((MouseEvent event) -> {
                                clientsListModelTableView.getSelectionModel().select(this.getIndex());
                                carRecord = clientsListModelTableView.getSelectionModel().getSelectedItem();

                                FXMLLoader loader = new FXMLLoader();
                                // Show new form/panel after button click
                                // loader.setLocation(getClass().getResource("makeAReservationWindow.fxml"));
                                //try {
                                //    loader.load();
                                //} catch (IOException ex) {
                                //    ex.printStackTrace();
                                //}

                                //MakeAReservationController makeAReservationController = loader.getController();
                                //makeAReservationController.setFields(carRecord.getCarID(), carRecord.getCarModelName(), carRecord.getManufacturerName(), carRecord.getCarTypeName(),
                                //        carRecord.getColor(), carRecord.getEnginePower(),carRecord.getDailyLendingPrice(),startDatePicker.getValue(),endDatePicker.getValue());
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

    public void setClientAccessLevel(Connection connectDB)
    {
        DatabaseConnection.accessLevel= DatabaseConnection.AccessLevelEnum.UNVERIFIED;
        String query = "select Verified from Clients where ClientID="+DatabaseConnection.loggedID;
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            while(queryResult.next())
            {
                if(queryResult.getInt(1)==1) DatabaseConnection.accessLevel= DatabaseConnection.AccessLevelEnum.VERIFIED;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void setChangeClientInformationButtonOnAction(ActionEvent event) throws SQLException {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        String firstName="", lastName="",emailAddress="", clientDrivingLicense="", phoneNumber="", backupPhoneNumber="";
        int phoneID=0;

        String getUserDataQuery="SELECT h.FirstName, h.LastName, h.EmailAddress, c.ClientDrivingLicense,h.PhoneID, p.PhoneNumber, p.BackupPhoneNumber " +
                "FROM Human h, Clients c, Phones p " +
                "WHERE h.HumanID="+DatabaseConnection.loggedID+" AND h.HumanID=c.ClientID AND h.PhoneID=p.PhoneID";

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
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("changeClientInformationWindow.fxml"));
        try {
            loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ChangeClientInformationController changeClientInformationController = loader.getController();
        changeClientInformationController.setFields(firstName, lastName,emailAddress,clientDrivingLicense,phoneID, phoneNumber,backupPhoneNumber);
        Parent parent = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.initStyle(StageStyle.UTILITY);
        stage.show();
    }
    public void exitButtonOnAction(ActionEvent event){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}