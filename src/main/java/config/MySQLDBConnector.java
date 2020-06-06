package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDBConnector {
    public void javaConnectorTest(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Błąd biblioteki mysql-java-connector");
            e.printStackTrace();
        }
    }
    public Connection setConnection(){
        try {
            String url = "jdbc:mysql://localhost:3306/task_manager" +
                    "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            String user = "tm_user";
            String password = "qwe123";
            Connection connection = DriverManager.getConnection(url,user,password);
            System.out.println("Ustanowiono połączenie z bazą danych");
            return connection;
        } catch (SQLException e) {
            System.out.println("Błąd połączenia z baza danych");
            e.printStackTrace();
            return null;
        }
    }
}
