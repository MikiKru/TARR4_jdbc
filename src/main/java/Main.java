import config.MySQLDBConnector;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        MySQLDBConnector mySQLDBConnector = new MySQLDBConnector();
        mySQLDBConnector.javaConnectorTest();
        Connection connection = mySQLDBConnector.setConnection();
        // ???
        mySQLDBConnector.closeConnection(connection);

    }
}
