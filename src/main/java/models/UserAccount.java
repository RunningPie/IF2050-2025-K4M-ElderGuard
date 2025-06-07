package model;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.UUID;

public class UserAccount {
    private final UUID userID;
    private String username;
    private String password; // This will be hashed
    private final Role userRole;
    private final Timestamp createdAt;

    // Constructor for new user (generates new UUID and current timestamp)
    public UserAccount(String username, String password, Role userRole) {
        this.userID = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.createdAt = Timestamp.from(ZonedDateTime.now().toInstant());
    }

    // Constructor for existing user (from database)
    public UserAccount(UUID userID, String username, String password, Role userRole, Timestamp createdAt) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getUserRole() {
        return userRole;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Utility methods
    public void getInfo() {
        System.out.println("User ID: " + userID);
        System.out.println("Username: " + username);
        System.out.println("Role: " + userRole.toString());
        System.out.println("Created At: " + createdAt);
    }

    public String getDisplayName() {
        return username + " (" + userRole + ")";
    }

    public boolean isAdmin() {
        return userRole == Role.ADMIN;
    }

    public boolean isLansia() {
        return userRole == Role.LANSIA;
    }

    public boolean isFamily() {
        return userRole == Role.FAMILY;
    }

    public boolean isMedicalStaff() {
        return userRole == Role.MEDICAL_STAFF;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "userID=" + userID +
                ", username='" + username + '\'' +
                ", userRole=" + userRole +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserAccount that = (UserAccount) obj;
        return userID.equals(that.userID);
    }

    @Override
    public int hashCode() {
        return userID.hashCode();
    }
}