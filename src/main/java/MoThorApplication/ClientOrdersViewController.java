package MoThorApplication;


import MoThorApplication.Models.ClientCarListModel;
import MoThorApplication.Models.ClientOrdersViewModel;
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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientOrdersViewController{

    @FXML
    private TableView<ClientOrdersViewModel> clientOrdersViewModelTableView;
    @FXML
    private TableColumn<ClientOrdersViewModel,Integer>orderIDTableColumn;
    @FXML
    private TableColumn<ClientOrdersViewModel, Integer> employeeIDTableColumn;
    @FXML
    private TableColumn<ClientOrdersViewModel, Integer> carIDTableColumn;
    @FXML
    private TableColumn<ClientOrdersViewModel, String> startDateTableColumn;
    @FXML
    private TableColumn<ClientOrdersViewModel, String> endDateTableColumn;
    @FXML
    private TableColumn<ClientOrdersViewModel, String> orderStatusTableColumn;
    @FXML
    private TableColumn<ClientOrdersViewModel, Float> fullLendingPriceTableColumn;
    @FXML
        private Button exitButton;
    @FXML
        private TextField keywordsTextField;
    @FXML
        private Label loggedAsLabel;

    ObservableList<ClientOrdersViewModel>clientOrdersViewModelObservableList= FXCollections.observableArrayList();

    private int clientID;

    public void setClientID(int clientID) {
        this.clientID = clientID;
        initialize();
    }
    public void initialize() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        String selectClientOrders="SELECT * FROM Orders WHERE ClientID="+clientID;
        try {
            setLoggedAsLabel(connectDB);
            showClientOrders(connectDB,selectClientOrders);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void showClientOrders(Connection connectDB, String query)
    {
        clientOrdersViewModelTableView.setItems(null);
        clientOrdersViewModelObservableList.clear();
        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            while(queryResult.next()){
                Integer queryOrderID = queryResult.getInt("OrderID");
                Integer queryEmployeeID = queryResult.getInt("EmployeeID");
                Integer queryCarID = queryResult.getInt("CarID");
                String queryStartDate = queryResult.getString("StartDate");
                String queryEndDate = queryResult.getString("EndDate");
                String queryOrderStatus = queryResult.getString("OrderStatus");
                Float queryFullLendingPrice = queryResult.getFloat("FullLendingPrice");

                clientOrdersViewModelObservableList.add(new ClientOrdersViewModel(queryOrderID,queryEmployeeID,queryCarID,queryStartDate,queryEndDate,queryOrderStatus,queryFullLendingPrice));
            }

            orderIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("OrderID"));
            employeeIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("EmployeeID"));
            carIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("CarID"));
            startDateTableColumn.setCellValueFactory(new PropertyValueFactory<>("StartDate"));
            endDateTableColumn.setCellValueFactory(new PropertyValueFactory<>("EndDate"));
            orderStatusTableColumn.setCellValueFactory(new PropertyValueFactory<>("OrderStatus"));
            fullLendingPriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("FullLendingPrice"));

            clientOrdersViewModelTableView.setItems(clientOrdersViewModelObservableList);

            FilteredList<ClientOrdersViewModel> filteredData = new FilteredList<>(clientOrdersViewModelObservableList,b->true);

            keywordsTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(clientOrdersViewModel -> {

                //gdy nie ma żadnego słowa kluczowego wyświetla wszystkie dostępne rekordy
                if (newValue.isEmpty() || newValue.isBlank()) return true;

                String searchKeyword = newValue.toLowerCase();
                if (clientOrdersViewModel.getOrderID().toString().contains(searchKeyword)) return true;
                else if (clientOrdersViewModel.getEmployeeID().toString().contains(searchKeyword)) return true;
                else if (clientOrdersViewModel.getCarID().toString().contains(searchKeyword)) return true;
                else if (clientOrdersViewModel.getStartDate().contains(searchKeyword)) return true;
                else if (clientOrdersViewModel.getEndDate().contains(searchKeyword)) return true;
                else if (clientOrdersViewModel.getOrderStatus().toLowerCase().contains(searchKeyword)) return true;
                else if (clientOrdersViewModel.getFullLendingPrice().toString().contains(searchKeyword)) return true;
                else return false;

            }));

            SortedList<ClientOrdersViewModel> sortedData=new SortedList<>(filteredData);

            sortedData.comparatorProperty().bind(clientOrdersViewModelTableView.comparatorProperty());

            clientOrdersViewModelTableView.setItems(sortedData);


        } catch(SQLException e){
            Logger.getLogger(ListOfCarsController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }

    public void setLoggedAsLabel(Connection connectDB) throws SQLException {
        String query="SELECT FirstName, LastName FROM Human WHERE HumanID="+clientID;
        Statement statement = connectDB.createStatement();
        ResultSet queryResult = statement.executeQuery(query);
    while (queryResult.next()) loggedAsLabel.setText("Showing orders history for: "+queryResult.getString("FirstName")+" "+queryResult.getString("LastName"));
    }

    public void exitButtonOnAction(ActionEvent event){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }


}
