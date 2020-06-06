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
            return DriverManager.getConnection("?","?","?");
        } catch (SQLException e) {
            System.out.println("Błą połączenia z baza danych");
            e.printStackTrace();
            return null;
        }
    }
}
