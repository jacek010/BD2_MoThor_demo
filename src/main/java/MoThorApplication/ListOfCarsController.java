package MoThorApplication;

import MoThorApplication.Models.ClientCarListModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListOfCarsController implements Initializable {

    @FXML
    private Button exitButton;
    @FXML
    private TableView<ClientCarListModel> carListModelTableView;
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

    ObservableList<ClientCarListModel> clientCarListModelObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resource){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String clientViewQuery="SELECT * FROM ClientView";

        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(clientViewQuery);

            while(queryResult.next()){
                String queryCarModelName = queryResult.getString("CarModelName");
                String queryManufacturerName = queryResult.getString("ManufacturerName");
                String queryCarTypeName = queryResult.getString("CarTypeName");
                String queryColor = queryResult.getString("Color");
                Integer queryEnginePower = queryResult.getInt("EnginePower");
                Float queryDailyLendingPrice = queryResult.getFloat("DailyLendingPrice");


                clientCarListModelObservableList.add(new ClientCarListModel(queryCarModelName, queryManufacturerName, queryCarTypeName, queryColor, queryEnginePower, queryDailyLendingPrice));
            }

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
                if(clientCarListModel.getCarModelName().toLowerCase().contains(searchKeyword)) return true;
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
    public void exitButtonOnAction(ActionEvent event){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }


}
