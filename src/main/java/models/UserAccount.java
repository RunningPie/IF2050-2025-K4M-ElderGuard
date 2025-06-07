package model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

public class UserAccount {
    private final UUID userID;
    private String username;
    private String password;
    private final Role userRole;
    private final ZonedDateTime createdAt;

    public UserAccount(String username, String password, Role userRole) {
        this.userID = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.createdAt = ZonedDateTime.now();
    }

    public void getInfo(){
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Role: " + userRole.toString());
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }

}
