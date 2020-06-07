package controller;

import lombok.AllArgsConstructor;
import model.Role;
import model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

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
    public void getAllUsersWithRoles() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(
                "SELECT u.user_email, u.user_registration, u.user_status, r.role_name " +
                        "FROM tm_user u join  user_role ur on (u.user_id = ur.user_id) " +
                        "join tm_role r on (r.role_id = ur.role_id) " +
                        "order by u.user_registration;");
        while (resultSet.next()){
            System.out.printf("| %15s | %20s | %5s | %10s |\n",
                    resultSet.getString(1), resultSet.getString(2),
                    resultSet.getBoolean(3), resultSet.getString(4)
                    );
        }
    }
    public boolean userRoleCheck(String roleName, int userId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "select * from user_role where " +
                        "role_id = (select role_id from tm_role where role_name = ?) and user_id = ?;"
        );
        ps.setString(1, roleName);
        ps.setInt(2, userId);
        ResultSet resultSet = ps.executeQuery();
        return resultSet.next();
    }
    // metoda przypisjąca uprewnienie do użytkownika
    public void addRoleByRoleNameToUser(String roleName, int userId) throws SQLException {
        if(!userRoleCheck(roleName, userId)) {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO user_role VALUES (?, (SELECT role_id FROM tm_role WHERE role_name = ?))"
            );
            ps.setInt(1, userId);
            ps.setString(2, roleName);
            ps.executeUpdate();
        }
    }
    // metoda usuwająca role użytkownika
    public void deleteRoleByRoleNameToUser(String roleName, int userId) throws SQLException {
        if(!userRoleCheck(roleName, userId)) {
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM user_role WHERE user_id = ? AND role_id = (SELECT role_id FROM tm_role WHERE role_name = ?)"
            );
            ps.setInt(1, userId);
            ps.setString(2, roleName);
            ps.executeUpdate();
        }
    }
    private int loginCount = 3;
    // 1. metoda do logowania użytkownika -> email + password + status = true
    // 2. gdy 3 razy pod rząd błędnie się zaloguje to status = false
    public void loginUser(String email, String password) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM task_manager.tm_user " +
                        "WHERE user_email=? AND user_password=? and user_status=1;"
        );
        ps.setString(1, email);
        ps.setString(2, password);
        if(ps.executeQuery().next()){
            System.out.println("Poprawnie zalogowano użytkownika: " + email);
            loginCount = 3;
        } else {
            loginCount --;
            if(loginCount < 0){
                System.out.println("konta zablokowane");
                PreparedStatement ps1 = connection.prepareStatement(
                        "UPDATE tm_user SET user_status = 0 WHERE user_email = ?");
                ps1.setString(1, email);
                ps1.executeUpdate();
            } else {
                System.out.println("Błąd logowania");
            }
        }
    }
    // metoda zwracająca role wraz z ich  częśtością wystepowania
    public void getAggregatedRoles() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM roles_agg");
        while (resultSet.next()){
            System.out.printf("| %15s | %3d |\n", resultSet.getString(1), resultSet.getInt(2));
        }
    }
    public void addUserWithTransactions(String name, String lastName, String email, String password) throws SQLException {
        // ustawienie transakcji
        connection.setAutoCommit(false);
        saveUser(name, lastName, email, password);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Czy na pewno chcesz dodać nowego użytkownika? (T/N)");
        if(scanner.nextLine().equals("T")){
            connection.commit();        // zatwoerdzenie zmian do bazy danych
        } else {
            connection.rollback();      // odrzucenie zmian do bazy danych
        }
    }
    // metoda wprowadzająca wielu użytkowników z listy i wsytuacji gdy wysąpi błąd podczas ich wprowadzania wszystkie
    // dotychczas wprowadzone rekrdy są wycofywane
    public void addUsersWithTransactions(List<User> users) throws SQLException {
        connection.setAutoCommit(false);
        AtomicBoolean isError = new AtomicBoolean(false);
        users.stream().forEach(user ->
                    {
                        try {
                            saveUser(user.getUserName(), user.getUserLastName(),user.getUserEmail(), user.getUserPassword());
                        } catch (SQLException e) {
                            System.out.println("Błąd!!!");
                            isError.set(true);
                        }
                    });
        if(isError.get()){
            connection.rollback();
        } else {
            connection.commit();
        }
    }

}





