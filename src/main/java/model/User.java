package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter             // automatycznie dodaje gettery do wszystkich pól klasowych
@Setter             // automatycznie dodaje settery do wszystkich pól klasowych
@ToString           // automatycznie dodaje metodę toString
public class User {
    private int userId;
    private String userName;
    private String userLastName;
    private String userEmail;
    private String userPassword;
    private LocalDateTime userRegistration = LocalDateTime.now();
    private boolean userStatus = true;

    public User(String userName, String userLastName, String userEmail, String userPassword) {
        this.userName = userName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }
}
