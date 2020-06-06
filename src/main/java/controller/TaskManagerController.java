package controller;

import lombok.AllArgsConstructor;
import model.Role;
import model.User;

import java.sql.*;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
    // metoda zwracająca wszyskie role w tabelce tm_role
    public List<Role> getAllRoles() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM tm_role");
        List<Role> roles = new ArrayList<>();
        while (resultSet.next()) {
            roles.add(new Role(
                    resultSet.getInt("role_id"),
                    resultSet.getString("role_name")));
        }
        return roles;
    }
    // metoda zwracająca obiekt User po kluczu user_id
    public User getUserById(int userId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM tm_user WHERE user_id = ?");
        ps.setInt(1, userId);
        ResultSet resultSet = ps.executeQuery();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(resultSet.next()){
            return new User(
                    resultSet.getInt("user_id"),
                    resultSet.getString("user_name"),
                    resultSet.getString("user_lastname"),
                    resultSet.getString("user_email"),
                    resultSet.getString("user_password"),
                    LocalDateTime.parse(resultSet.getString("user_registration"), formatter),
                    resultSet.getBoolean("user_status")
            );
        }
        return null;
    }


    // metoda wypisująca zawartość listy
    public void printContent(List list){
        list.forEach(System.out::println);
    }
}
