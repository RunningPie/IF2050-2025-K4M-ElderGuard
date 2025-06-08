package models;

import java.time.ZonedDateTime;
import java.util.UUID;

public class UserAccount {
    private final UUID userID;
    private String username;
    private String password;
    private final Role userRole;
    private final ZonedDateTime createdAt;
    private String contactInfo; // Added to match database schema

    public UserAccount(String username, String password, Role userRole) {
        this.userID = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.createdAt = ZonedDateTime.now();
    }

    // Constructor with ID (for loading from database)
    public UserAccount(UUID userID, String username, String password, Role userRole, ZonedDateTime createdAt) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.createdAt = createdAt;
    }

    // Full constructor including contact_info
    public UserAccount(UUID userID, String username, String password, String contactInfo, Role userRole, ZonedDateTime createdAt) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.contactInfo = contactInfo;
        this.userRole = userRole;
        this.createdAt = createdAt;
    }

    public void getInfo(){
        System.out.println("Username: " + username);
        System.out.println("Role: " + userRole.toString());
        System.out.println("User ID: " + userID.toString());
        System.out.println("Contact Info: " + contactInfo);
    }

    // Getters
    public UUID getUserID() { return userID; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getUserRole() { return userRole; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public String getContactInfo() { return contactInfo; }

    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    @Override
    public String toString() {
        return "UserAccount{" +
                "userID=" + userID +
                ", username='" + username + '\'' +
                ", userRole=" + userRole +
                ", contactInfo='" + contactInfo + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}