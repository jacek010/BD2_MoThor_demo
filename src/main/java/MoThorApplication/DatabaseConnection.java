package MoThorApplication;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    public Connection databaseLink;

    public Connection getConnection(){
        String databaseName = "Mothor";
        String url = "jdbc:mariadb://wesolachmurka.synology.me:3306/"+databaseName;
        String username = "admin";
        String password = "H4$lo12345";
        try {
            databaseLink = DriverManager.getConnection(url,username,password);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return databaseLink;
    }

}
