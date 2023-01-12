package MoThorApplication.EmployeePanel;

import MoThorApplication.DatabaseConnection;
import MoThorApplication.EmployeeWindowHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class EmployeeWindowController  implements Initializable {
    @FXML
    private Label loggedAsLabel;
    @FXML
    private Button exitButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Tab employeesTab;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        employeesTab.setDisable(true);

        EmployeeWindowHelper.setEmployeeLoggedAsLabel(loggedAsLabel);
        setEmployeeAccessLevel(connectDB);
        if(DatabaseConnection.accessLevel== DatabaseConnection.AccessLevelEnum.MANAGER) employeesTab.setDisable(false);
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
        EmployeeWindowHelper.exit(exitButton);
    }

    public void logoutButtonOnAction(ActionEvent event){
        EmployeeWindowHelper.logout(logoutButton);
    }
}
