package MoThorApplication.EmployeePanel;

import MoThorApplication.DatabaseConnection;
import MoThorApplication.ListOfCarsController;
import MoThorApplication.Models.EmployeeOrdersViewModel;
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

public class EmployeeOrdersController implements Initializable {

    @FXML
        private TableView<EmployeeOrdersViewModel> employeeOrdersViewModelTableView;
    @FXML
        private TableColumn<EmployeeOrdersViewModel, Integer> orderIDTableColumn;
    @FXML
        private TableColumn<EmployeeOrdersViewModel, Integer> clientIDTableColumn;
    @FXML
        private TableColumn<EmployeeOrdersViewModel, Integer> employeeIDTableColumn;
    @FXML
        private TableColumn<EmployeeOrdersViewModel, Integer> carIDTableColumn;
    @FXML
        private TableColumn<EmployeeOrdersViewModel, String> startDateTableColumn;
    @FXML
        private TableColumn<EmployeeOrdersViewModel, String> endDateTableColumn;
    @FXML
        private TableColumn<EmployeeOrdersViewModel, String> orderStatusTableColumn;
    @FXML
        private TableColumn<EmployeeOrdersViewModel, Float> fullLendingPriceTableColumn;
    @FXML
        private TableColumn<EmployeeOrdersViewModel, String> detailsTableColumn;


    @FXML
        private Button exitButton;
    @FXML
        private TextField keywordsOrdersTextField;


    EmployeeOrdersViewModel orderRecord;
    ObservableList<EmployeeOrdersViewModel> employeeOrdersViewModelObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getData();
    }


    public void getData()
    {
        employeeOrdersViewModelTableView.setItems(null);
        employeeOrdersViewModelObservableList.clear();

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT * FROM Orders";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            while (queryResult.next())
            {
                Integer orderID = queryResult.getInt("OrderID");
                Integer clientID = queryResult.getInt("ClientID");
                Integer employeeID = queryResult.getInt("EmployeeID");
                Integer carID = queryResult.getInt("CarID");
                String startDate = queryResult.getString("StartDate");
                String endDate = queryResult.getString("EndDate");
                String orderStatus = queryResult.getString("OrderStatus");
                Float fullLendingPrice=queryResult.getFloat("FullLendingPrice");

                employeeOrdersViewModelObservableList.add(new EmployeeOrdersViewModel(orderID, clientID, employeeID, carID, startDate, endDate, orderStatus, fullLendingPrice));
            }

            orderIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("orderID"));
            clientIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("clientID"));
            employeeIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
            carIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("carID"));
            startDateTableColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
            endDateTableColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
            orderStatusTableColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
            fullLendingPriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("fullLendingPrice"));

            employeeOrdersViewModelTableView.setItems(employeeOrdersViewModelObservableList);

            FilteredList<EmployeeOrdersViewModel> filteredData = new FilteredList<>(employeeOrdersViewModelObservableList, b->true);

            keywordsOrdersTextField.textProperty().addListener((observable, oldValue, newValue)->filteredData.setPredicate(employeeOrdersViewModel -> {

                //gdy nie ma żadnego słowa kluczowego wyświetla wszystkie dostępne rekordy
                if (newValue.isEmpty() || newValue.isBlank()) return true;
                String searchKeyword = newValue.toLowerCase();
                if (employeeOrdersViewModel.getOrderID().toString().contains(searchKeyword))return true;
                else if(employeeOrdersViewModel.getClientID().toString().contains(searchKeyword))return true;
                else if(employeeOrdersViewModel.getEmployeeID().toString().contains(searchKeyword))return true;
                else if(employeeOrdersViewModel.getCarID().toString().contains(searchKeyword))return true;
                else if(employeeOrdersViewModel.getStartDate().toLowerCase().contains(searchKeyword))return true;
                else if(employeeOrdersViewModel.getEndDate().toLowerCase().contains(searchKeyword))return true;
                else if(employeeOrdersViewModel.getOrderStatus().toLowerCase().contains(searchKeyword))return true;
                else if(employeeOrdersViewModel.getFullLendingPrice().toString().contains(searchKeyword))return true;
                else return false;
            }));

            SortedList<EmployeeOrdersViewModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(employeeOrdersViewModelTableView.comparatorProperty());

            employeeOrdersViewModelTableView.setItems(sortedData);

            Callback<TableColumn<EmployeeOrdersViewModel, String>, TableCell<EmployeeOrdersViewModel, String>> cellFactory = (TableColumn<EmployeeOrdersViewModel, String> param) -> {
                // make cell containing buttons

                return new TableCell<EmployeeOrdersViewModel, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        //that cell created only on non-empty rows
                        if (empty) {
                            setGraphic(null);

                        } else {

                            //FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.CAR);
                            Button detailsButton = new Button("Details");
                            detailsButton.setStyle("-fx-background-color: #1aa3ff;" +
                                    "");

                            detailsButton.setOnMouseClicked((MouseEvent event) -> {
                                employeeOrdersViewModelTableView.getSelectionModel().select(this.getIndex());
                                orderRecord = employeeOrdersViewModelTableView.getSelectionModel().getSelectedItem();

                                FXMLLoader loader = new FXMLLoader();
                                loader.setLocation(getClass().getResource("employeeOrderDetailsWindow.fxml"));
                                try {
                                    loader.load();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                                EmployeeOrderDetailsController employeeOrderDetailsController = loader.getController();
                                try {
                                    employeeOrderDetailsController.getData(orderRecord.getOrderID());
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                                Parent parent = loader.getRoot();
                                Stage stage = new Stage();
                                stage.setScene(new Scene(parent));
                                stage.initStyle(StageStyle.UTILITY);
                                stage.show();


                            });

                            HBox manageBtn = new HBox(detailsButton);
                            manageBtn.setStyle("-fx-alignment:center");
                            HBox.setMargin(detailsButton, new Insets(2, 3, 0, 2));

                            setGraphic(manageBtn);
                        }
                        setText(null);

                    }

                };
            };
            detailsTableColumn.setCellFactory(cellFactory);
            employeeOrdersViewModelTableView.setItems(sortedData);

        } catch(SQLException e){
            Logger.getLogger(ListOfCarsController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }


    }


    public void exitButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

}
