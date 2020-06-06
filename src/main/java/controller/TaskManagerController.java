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
    // metoda wprowadzająca nowy rekord do tabelki tm_user
    public void saveUser(String name, String lastName, String email, String password) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "insert into tm_user values (default, ?, ?, ?, ?, default, default);"
        );
        ps.setString(1, name);
        ps.setString(2,lastName);
        ps.setString(3,email);
        ps.setString(4,password);
        ps.executeUpdate();         // dla poleceń typu insert, update, delete, create, drop, alter
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(
                "SELECT user_id FROM tm_user ORDER BY user_registration DESC LIMIT 1");
        if(resultSet.next()) {
            addRoleToUser(resultSet.getInt("user_id"), 2);
        }
    }
    public void addRoleToUser(int userId, int roleId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO user_role VALUES (?, ?)");
        ps.setInt(1, userId);
        ps.setInt(2, roleId);
        ps.executeUpdate();
    }
    // metoda do zmiany hasła użytkownika
    public boolean updateUserPasswordById(int userId, String newPassword) throws SQLException {
        if(getUserById(userId) != null){
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE tm_user SET user_password = ? WHERE user_id = ?");
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            ps.executeUpdate();
            return true;
        }
        return false;
    }
    // metoda usuwająca użytkownka po id wraz z jego powiązaniami w tabelce user_role
    public void deleteUserByIdRecursively(int user_id) throws SQLException {
        // 1. Usunięcie wszystkich powiązań z user_id w tabelce user_role
        PreparedStatement ps_role = connection.prepareStatement("DELETE FROM user_role WHERE user_id = ?");
        ps_role.setInt(1, user_id);
        ps_role.executeUpdate();
        // 2. Usunięcie użytkownika po user_id
        PreparedStatement ps_user = connection.prepareStatement("DELETE FROM tm_user WHERE user_id = ?");
        ps_user.setInt(1, user_id);
        ps_user.executeUpdate();
    }
}





