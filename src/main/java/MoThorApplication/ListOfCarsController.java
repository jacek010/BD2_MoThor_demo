package MoThorApplication;

import MoThorApplication.Models.ClientCarListModel;
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

public class ListOfCarsController implements Initializable {
    @FXML
    private Button exitButton;
    @FXML
    private TableView<ClientCarListModel> carListModelTableView;
    @FXML
    private TableColumn<ClientCarListModel,Integer>carIDTableColumn;
    @FXML
    private TableColumn<ClientCarListModel, String> carModelTableColumn;
    @FXML
    private TableColumn<ClientCarListModel, String> manufacturerTableColumn;
    @FXML
    private TableColumn<ClientCarListModel, String> carTypeTableColumn;
    @FXML
    private TableColumn<ClientCarListModel, String> colorTableColumn;
    @FXML
    private TableColumn<ClientCarListModel, Integer> enginePowerTableColumn;
    @FXML
    private TableColumn<ClientCarListModel, Float> costPerDayTableColumn;
    @FXML
    private TableColumn<ClientCarListModel, String> reservationTableColumn;
    @FXML
    private TextField keywordsTextField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Label loggedAsLabel;
    @FXML
    private Button changeClientInformationButton;

    ClientCarListModel carRecord;

    ObservableList<ClientCarListModel> clientCarListModelObservableList = FXCollections.observableArrayList();

    boolean showOrderButtons = false;

    @Override
    public void initialize(URL url, ResourceBundle resource){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        showOrderButtons=false;
        setClientAccessLevel(connectDB);

        loggedAsLabel.setText("You are logged as: "+DatabaseConnection.firstName+" "+DatabaseConnection.lastName);

        String clientViewQuery="SELECT * FROM ClientView";

        showCarList(connectDB, clientViewQuery);

        startDatePicker.valueProperty().addListener((observable, oldValue, newValue)->{
            if(endDatePicker.getValue()==null) endDatePicker.setValue(newValue.plusDays(5));
            else if(endDatePicker.getValue().isBefore(newValue.plusDays(5))) endDatePicker.setValue(newValue.plusDays(5));
            String startDate = startDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endDate = endDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String clientViewQuery2="select * from ClientView where CarID not IN (select CarID from Orders where (StartDate between '"+startDate+"' AND'"+endDate+"')or(EndDate between '"+startDate+"'and'"+endDate+"'))";
            showCarList(connectDB, clientViewQuery2);

            //do momentu rozwiązania logowania
            showOrderButtons=true;
            //if(DatabaseConnection.accessLevel== DatabaseConnection.AccessLevelEnum.VERIFIED)showOrderButtons=true;
        });
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue)->{
            if(startDatePicker.getValue()==null)startDatePicker.setValue(newValue.minusDays(5));
            else if(newValue.isBefore(startDatePicker.getValue().plusDays(5))) startDatePicker.setValue(newValue.minusDays(5));
            String startDate = startDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endDate = endDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String clientViewQuery2="select * from ClientView where CarID not IN (select CarID from Orders where (StartDate between '"+startDate+"' AND'"+endDate+"')or(EndDate between '"+startDate+"'and'"+endDate+"'))";
            showCarList(connectDB, clientViewQuery2);

            //do momentu rozwiązania logowania
            showOrderButtons=true;
            //if(DatabaseConnection.accessLevel== DatabaseConnection.AccessLevelEnum.VERIFIED)showOrderButtons=true;
        });

    }

    public void showCarList(Connection connectDB,String query)
    {
        carListModelTableView.setItems(null);
        clientCarListModelObservableList.clear();
        //System.out.println(query);
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            while (queryResult.next()) {
                Integer queryCarID = queryResult.getInt("CarID");
                String queryCarModelName = queryResult.getString("CarModelName");
                String queryManufacturerName = queryResult.getString("ManufacturerName");
                String queryCarTypeName = queryResult.getString("CarTypeName");
                String queryColor = queryResult.getString("Color");
                Integer queryEnginePower = queryResult.getInt("EnginePower");
                Float queryDailyLendingPrice = queryResult.getFloat("DailyLendingPrice");


                clientCarListModelObservableList.add(new ClientCarListModel(queryCarID, queryCarModelName, queryManufacturerName, queryCarTypeName, queryColor, queryEnginePower, queryDailyLendingPrice));
            }

            carIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("carID"));
            carModelTableColumn.setCellValueFactory(new PropertyValueFactory<>("carModelName"));
            manufacturerTableColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturerName"));
            carTypeTableColumn.setCellValueFactory(new PropertyValueFactory<>("carTypeName"));
            colorTableColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
            enginePowerTableColumn.setCellValueFactory(new PropertyValueFactory<>("enginePower"));
            costPerDayTableColumn.setCellValueFactory(new PropertyValueFactory<>("dailyLendingPrice"));

            //add cell of button edit


            carListModelTableView.setItems(clientCarListModelObservableList);

            FilteredList<ClientCarListModel> filteredData = new FilteredList<>(clientCarListModelObservableList, b -> true);

            keywordsTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(clientCarListModel -> {

                //gdy nie ma żadnego słowa kluczowego wyświetla wszystkie dostępne rekordy
                if (newValue.isEmpty() || newValue.isBlank()) return true;

                String searchKeyword = newValue.toLowerCase();
                if (clientCarListModel.getCarID().toString().contains(searchKeyword)) return true;
                else if (clientCarListModel.getCarModelName().toLowerCase().contains(searchKeyword)) return true;
                else if (clientCarListModel.getManufacturerName().toLowerCase().contains(searchKeyword)) return true;
                else if (clientCarListModel.getCarTypeName().toLowerCase().contains(searchKeyword)) return true;
                else if (clientCarListModel.getColor().toLowerCase().contains(searchKeyword)) return true;
                else if (clientCarListModel.getEnginePower().toString().contains(searchKeyword)) return true;
                else if (clientCarListModel.getDailyLendingPrice().toString().contains(searchKeyword)) return true;
                else return false;

            }));

            SortedList<ClientCarListModel> sortedData = new SortedList<>(filteredData);

            sortedData.comparatorProperty().bind(carListModelTableView.comparatorProperty());

            carListModelTableView.setItems(sortedData);


                Callback<TableColumn<ClientCarListModel, String>, TableCell<ClientCarListModel, String>> cellFactory = (TableColumn<ClientCarListModel, String> param) -> {
                    // make cell containing buttons

                    return new TableCell<ClientCarListModel, String>() {
                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            //that cell created only on non-empty rows
                            if (empty) {
                                setGraphic(null);

                            } else {

                                //FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.CAR);
                                Button editButton = new Button("Order");
                                editButton.setStyle("-fx-background-color: #1aa3ff;" +
                                        "");
                                editButton.setDisable(!showOrderButtons);

                                editButton.setOnMouseClicked((MouseEvent event) -> {
                                    carListModelTableView.getSelectionModel().select(this.getIndex());
                                    carRecord = carListModelTableView.getSelectionModel().getSelectedItem();

                                    FXMLLoader loader = new FXMLLoader();
                                    loader.setLocation(getClass().getResource("makeAReservationWindow.fxml"));
                                    try {
                                        loader.load();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }

                                    MakeAReservationController makeAReservationController = loader.getController();
                                    makeAReservationController.setFields(carRecord.getCarID(), carRecord.getCarModelName(), carRecord.getManufacturerName(), carRecord.getCarTypeName(),
                                            carRecord.getColor(), carRecord.getEnginePower(),carRecord.getDailyLendingPrice(),startDatePicker.getValue(),endDatePicker.getValue());
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
                reservationTableColumn.setCellFactory(cellFactory);
                carListModelTableView.setItems(sortedData);


            } catch(SQLException e){
                Logger.getLogger(ListOfCarsController.class.getName()).log(Level.SEVERE, null, e);
                e.printStackTrace();
            }


    }

    public void setClientAccessLevel(Connection connectDB)
    {
        String query = "select Verified from Clients where ClientID="+DatabaseConnection.loggedID;
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            while(queryResult.next())
            {
                if(queryResult.getInt(1)==1) DatabaseConnection.accessLevel= DatabaseConnection.AccessLevelEnum.VERIFIED;
                else DatabaseConnection.accessLevel= DatabaseConnection.AccessLevelEnum.UNVERIFIED;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void setChangeClientInformationButtonOnAction(ActionEvent event) throws SQLException {
        DatabaseConnection.loggedID=10;
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
