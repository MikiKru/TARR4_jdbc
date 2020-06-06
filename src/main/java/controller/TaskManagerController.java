package controller;

import lombok.AllArgsConstructor;
import model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class TaskManagerController {
    private Connection connection;

    // metoda zwracająca wszystkich użytkowników z tabelki tm_user
    public List<User> getAllUsers() throws SQLException {
        // obiekt na którym interpretowane będzie polecenie w składni MySQL
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM tm_user");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<User> users = new ArrayList<>();
        while (resultSet.next()){
            users.add(new User(
                    resultSet.getInt("user_id"),
                    resultSet.getString("user_name"),
                    resultSet.getString("user_lastname"),
                    resultSet.getString("user_email"),
                    resultSet.getString("user_password"),
                    LocalDateTime.parse(resultSet.getString("user_registration"), formatter),
                    resultSet.getBoolean("user_status")
                    ));
        }
        return users;
    }
}
