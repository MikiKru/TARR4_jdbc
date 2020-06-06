import config.MySQLDBConnector;
import controller.TaskManagerController;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        MySQLDBConnector mySQLDBConnector = new MySQLDBConnector();
        mySQLDBConnector.javaConnectorTest();
        Connection connection = mySQLDBConnector.setConnection();

        TaskManagerController tmc = new TaskManagerController(connection);
        System.out.println(tmc.getAllUsers());

        mySQLDBConnector.closeConnection(connection);

    }
}
