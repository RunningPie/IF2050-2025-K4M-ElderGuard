package services;

import models.UserAccount;
import models.Role;
import utils.DBUtil;
import utils.SessionManager;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;


public class AuthService {

    /**
     * Authenticate user with username and password
     */
    public UserAccount authenticate(String username, String password) {
        String query = "SELECT user_id, username, password, role, created_at FROM user_account WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                // In production, use proper password hashing (BCrypt, etc.)
                if (password.equals(storedPassword)) {
                    UUID userId = UUID.fromString(rs.getString("user_id"));
                    String roleString = rs.getString("role");
                    Role role = Role.valueOf(roleString);
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    ZonedDateTime zonedCreatedAt = createdAt.toInstant().atZone(java.time.ZoneId.systemDefault());

                    return new UserAccount(userId, username, storedPassword, role, zonedCreatedAt);
                }
            }

        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create new user account
     */
    public boolean createAccount(String username, String password, Role role) {
        // Check if username already exists
        if (usernameExists(username)) {
            return false;
        }

        String insertQuery = "INSERT INTO user_account (user_id, username, password, role, created_at) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            UUID userId = UUID.randomUUID();
            Timestamp now = Timestamp.from(java.time.Instant.now());

            stmt.setObject(1, userId, java.sql.Types.OTHER);
            stmt.setString(2, username);
            stmt.setString(3, password); // In production, hash the password
            stmt.setString(4, role.toString());
            stmt.setTimestamp(5, now);

            int rowsAffected = stmt.executeUpdate();

            // If user is LANSIA, also create entry in lansia table
            if (rowsAffected > 0 && role == Role.LANSIA) {
                createLansiaEntry(conn, userId);
            }

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Account creation error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create entry in lansia table for lansia users
     */
    private void createLansiaEntry(Connection conn, UUID userId) {
        String insertLansiaQuery = "INSERT INTO lansia (user_id, created_at) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insertLansiaQuery)) {
            stmt.setObject(1, userId, java.sql.Types.OTHER);
            stmt.setTimestamp(2, Timestamp.from(java.time.Instant.now()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to create lansia entry: " + e.getMessage());
        }
    }

    /**
     * Verify password for a user
     */
    public boolean verifyPassword(String username, String password) {
        String query = "SELECT password FROM user_account WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return password.equals(storedPassword); // In production, use proper password verification
            }

        } catch (SQLException e) {
            System.err.println("Password verification error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update user password
     */
    public boolean updatePassword(String username, String newPassword) {
        String updateQuery = "UPDATE user_account SET password = ? WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, newPassword); // In production, hash the password
            stmt.setString(2, username);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Password update error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if username already exists
     */
    private boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM user_account WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Username check error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get user by ID
     */
    public UserAccount getUserById(UUID userId) {
        String query = "SELECT user_id, username, password, role, created_at FROM user_account WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                Role role = Role.valueOf(rs.getString("role"));
                Timestamp createdAt = rs.getTimestamp("created_at");
                ZonedDateTime zonedCreatedAt = createdAt.toInstant().atZone(java.time.ZoneId.systemDefault());

                return new UserAccount(userId, username, password, role, zonedCreatedAt);
            }

        } catch (SQLException e) {
            System.err.println("Get user error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Update user profile
     */
    public boolean updateProfile(UUID userId, String newUsername) {
        // Check if new username already exists (excluding current user)
        if (usernameExistsExcludingUser(newUsername, userId)) {
            return false;
        }

        String updateQuery = "UPDATE user_account SET username = ? WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, newUsername);
            stmt.setString(2, userId.toString());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Profile update error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if username exists excluding a specific user
     */
    private boolean usernameExistsExcludingUser(String username, UUID excludeUserId) {
        String query = "SELECT COUNT(*) FROM user_account WHERE username = ? AND user_id != ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, excludeUserId.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Username check error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
    public void logout() {
        SessionManager.clearSession();
        System.out.println("User logged out successfully");
    }

    /**
     * Get all users from the database
     */
    public List<UserAccount> getAllUsers() {
        List<UserAccount> users = new ArrayList<>();
        String query = "SELECT user_id, username, password, role, created_at FROM user_account";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UUID userId = UUID.fromString(rs.getString("user_id"));
                String username = rs.getString("username");
                String password = rs.getString("password");
                Role role = Role.valueOf(rs.getString("role"));
                Timestamp createdAt = rs.getTimestamp("created_at");
                ZonedDateTime zonedCreatedAt = createdAt.toInstant().atZone(java.time.ZoneId.systemDefault());

                users.add(new UserAccount(userId, username, password, role, zonedCreatedAt));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

}