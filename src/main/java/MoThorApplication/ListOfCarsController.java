package MoThorApplication;

import MoThorApplication.Models.ClientCarListModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
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
    private TextField keywordsTextField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Label loggedAsLabel;

    ObservableList<ClientCarListModel> clientCarListModelObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resource){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

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
        });
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue)->{
            if(startDatePicker.getValue()==null)startDatePicker.setValue(newValue.minusDays(5));
            else if(newValue.isBefore(startDatePicker.getValue().plusDays(5))) startDatePicker.setValue(newValue.minusDays(5));
            String startDate = startDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endDate = endDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String clientViewQuery2="select * from ClientView where CarID not IN (select CarID from Orders where (StartDate between '"+startDate+"' AND'"+endDate+"')or(EndDate between '"+startDate+"'and'"+endDate+"'))";
            showCarList(connectDB, clientViewQuery2);
        });

    }

    public void showCarList(Connection connectDB,String query)
    {
        carListModelTableView.setItems(null);
        clientCarListModelObservableList.clear();
        //System.out.println(query);
        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            while(queryResult.next()){
                Integer queryCarID= queryResult.getInt("CarID");
                String queryCarModelName = queryResult.getString("CarModelName");
                String queryManufacturerName = queryResult.getString("ManufacturerName");
                String queryCarTypeName = queryResult.getString("CarTypeName");
                String queryColor = queryResult.getString("Color");
                Integer queryEnginePower = queryResult.getInt("EnginePower");
                Float queryDailyLendingPrice = queryResult.getFloat("DailyLendingPrice");


                clientCarListModelObservableList.add(new ClientCarListModel(queryCarID, queryCarModelName, queryManufacturerName, queryCarTypeName, queryColor, queryEnginePower, queryDailyLendingPrice));
            }

            carIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("CarID"));
            carModelTableColumn.setCellValueFactory(new PropertyValueFactory<>("CarModelName"));
            manufacturerTableColumn.setCellValueFactory(new PropertyValueFactory<>("ManufacturerName"));
            carTypeTableColumn.setCellValueFactory(new PropertyValueFactory<>("CarTypeName"));
            colorTableColumn.setCellValueFactory(new PropertyValueFactory<>("Color"));
            enginePowerTableColumn.setCellValueFactory(new PropertyValueFactory<>("EnginePower"));
            costPerDayTableColumn.setCellValueFactory(new PropertyValueFactory<>("DailyLendingPrice"));

            carListModelTableView.setItems(clientCarListModelObservableList);

            FilteredList<ClientCarListModel> filteredData = new FilteredList<>(clientCarListModelObservableList, b->true);

            keywordsTextField.textProperty().addListener((observable, oldValue, newValue)-> filteredData.setPredicate(clientCarListModel->{

                //gdy nie ma żadnego słowa kluczowego wyświetla wszystkie dostępne rekordy
                if(newValue.isEmpty() || newValue.isBlank()) return true;

                String searchKeyword = newValue.toLowerCase();
                if (clientCarListModel.getCarID().toString().contains(searchKeyword)) return true;
                else if(clientCarListModel.getCarModelName().toLowerCase().contains(searchKeyword)) return true;
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


        } catch(SQLException e){
            Logger.getLogger(ListOfCarsController.class.getName()).log(Level.SEVERE,null,e);
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
    public void exitButtonOnAction(ActionEvent event){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }


}
