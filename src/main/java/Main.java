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
        tmc.printContent(tmc.getAllUsers());
        tmc.printContent(tmc.getAllRoles());
        System.out.println(tmc.getUserById(1));
        System.out.println(tmc.getUserById(10));
//        tmc.saveUser("Anna","Nowak", "an@an.pl", "an");
//        tmc.printContent(tmc.getAllUsers());
        System.out.println("Zmieniono hasło: " + tmc.updateUserPasswordById(1,"xxx"));
        System.out.println("Zmieniono hasło: " + tmc.updateUserPasswordById(10,"xxx"));
        mySQLDBConnector.closeConnection(connection);

    }
}
