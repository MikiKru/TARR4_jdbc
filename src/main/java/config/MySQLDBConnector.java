package config;

public class MySQLDBConnector {
    public void javaConnectorTest(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Błąd biblioteki mysql-java-connector");
            e.printStackTrace();
        }
    }
}
