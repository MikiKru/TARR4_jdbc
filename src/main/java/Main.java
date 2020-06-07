import config.MySQLDBConnector;
import controller.TaskManagerController;
import model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws SQLException {
        MySQLDBConnector mySQLDBConnector = new MySQLDBConnector();
        mySQLDBConnector.javaConnectorTest();
        Connection connection = mySQLDBConnector.setConnection();

        TaskManagerController tmc = new TaskManagerController(connection, 3);
        tmc.printContent(tmc.getAllUsers());
        tmc.printContent(tmc.getAllRoles());
        System.out.println(tmc.getUserById(1));
        System.out.println(tmc.getUserById(10));
//        tmc.saveUser("Aleksandra","Mowa", "am@am.pl", "am");
//        tmc.printContent(tmc.getAllUsers());
        System.out.println("Zmieniono hasło: " + tmc.updateUserPasswordById(1,"yy"));
        System.out.println("Zmieniono hasło: " + tmc.updateUserPasswordById(10,"xxx"));
        tmc.deleteUserByIdRecursively(3);
        tmc.addRoleByRoleNameToUser("admin",1);
        tmc.getAllUsersWithRoles();
        tmc.loginUser("mk@mk.pl", "00");
        tmc.loginUser("mk@mk.pl", "111");
        tmc.loginUser("mk@mk.pl", "234");
        tmc.loginUser("mk@mk.pl", "012");
        tmc.loginUser("mk@mk.pl", "yy");
        tmc.getAggregatedRoles();
//        tmc.addUserWithTransactions("Anna", "Pies", "ap@ap.pl", "ap");
        tmc.addUsersWithTransactions(
                new ArrayList<>(Arrays.asList(
                        new User("X1","X1","x1@x.pl","xx"),
                        new User("Y1","Y1","y1@y.pl","yy"),
                        new User("Z","Z","z@z.pl","zz")
                        )));
        mySQLDBConnector.closeConnection(connection);

    }
}
