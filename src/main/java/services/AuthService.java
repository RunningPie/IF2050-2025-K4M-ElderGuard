package service;

import model.UserAccount;
import model.Role;
import utils.DBUtil;
import utils.PasswordUtil;

import java.sql.*;
import java.util.UUID;

public class AuthService {

    /**
     * Authenticates a user with username and password
     * @param username the username
     * @param password the plain text password
     * @return UserAccount object if authentication successful, null otherwise
     */
    public UserAccount authenticate(String username, String password) {
        // Check if database is available
        if (!DBUtil.isDatabaseAvailable()) {
            throw new RuntimeException("Database is not available. Please check your database connection and try again.");
        }

        String sql = "SELECT user_id, username, password, role, created_at FROM user_account WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                // Verify password
                if (PasswordUtil.verifyPassword(password, storedPassword)) {
                    UUID userId = UUID.fromString(rs.getString("user_id"));
                    String roleString = rs.getString("role");
                    Role role = Role.valueOf(roleString.toUpperCase());
                    Timestamp createdAt = rs.getTimestamp("created_at");

                    System.out.println("✓ Authentication successful for user: " + username);
                    return new UserAccount(userId, username, storedPassword, role, createdAt);
                } else {
                    System.out.println("✗ Authentication failed for user: " + username + " (wrong password)");
                }
            } else {
                System.out.println("✗ Authentication failed for user: " + username + " (user not found)");
            }

        } catch (SQLException e) {
            System.err.println("Authentication database error: " + e.getMessage());
            e.printStackTrace();

            // Provide more specific error messages
            if (e.getMessage().contains("authentication failed") || e.getMessage().contains("password authentication failed")) {
                throw new RuntimeException("Database authentication failed. Please check your database credentials.");
            } else if (e.getMessage().contains("Connection") || e.getMessage().contains("connect")) {
                throw new RuntimeException("Cannot connect to database. Please check your network connection and database settings.");
            } else {
                throw new RuntimeException("Database error during authentication: " + e.getMessage());
            }
        }

        return null; // Authentication failed
    }

    /**
     * Creates a new user account
     * @param username the username
     * @param password the plain text password
     * @param role the user role
     * @return true if account created successfully, false otherwise
     */
    public boolean createAccount(String username, String password, Role role) {
        // Check if database is available
        if (!DBUtil.isDatabaseAvailable()) {
            throw new RuntimeException("Database is not available. Please check your database connection and try again.");
        }

        // Check if username already exists
        if (usernameExists(username)) {
            System.out.println("✗ Account creation failed: Username '" + username + "' already exists");
            return false;
        }

        String sql = "INSERT INTO user_account (user_id, username, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            UUID userId = UUID.randomUUID();
            String hashedPassword = PasswordUtil.hashPassword(password);

            stmt.setObject(1, userId);
            stmt.setString(2, username);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, role.toString());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✓ Account created successfully for user: " + username + " with role: " + role);

                // Create additional profile based on role
                createUserProfile(userId, role);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Account creation database error: " + e.getMessage());
            e.printStackTrace();

            // Check for specific database errors
            if (e.getMessage().contains("duplicate key") || e.getMessage().contains("unique constraint")) {
                throw new RuntimeException("Username already exists. Please choose a different username.");
            } else if (e.getMessage().contains("Connection") || e.getMessage().contains("connect")) {
                throw new RuntimeException("Cannot connect to database. Please check your network connection and database settings.");
            } else {
                throw new RuntimeException("Database error during account creation: " + e.getMessage());
            }
        }

        return false;
    }

    /**
     * Checks if a username already exists
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    private boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM user_account WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Failed to check username: " + e.getMessage());
            e.printStackTrace();
            // If we can't check, assume it doesn't exist and let the insert fail if it does
            return false;
        }

        return false;
    }

    /**
     * Creates additional user profile based on role
     * @param userId the user ID
     * @param role the user role
     */
    private void createUserProfile(UUID userId, Role role) {
        try (Connection conn = DBUtil.getConnection()) {

            switch (role) {
                case LANSIA:
                    createLansiaProfile(conn, userId);
                    System.out.println("✓ Lansia profile created for user ID: " + userId);
                    break;
                case FAMILY:
                    createFamilyProfile(conn, userId);
                    System.out.println("✓ Family profile created for user ID: " + userId);
                    break;
                case MEDICAL_STAFF:
                    createMedicalStaffProfile(conn, userId);
                    System.out.println("✓ Medical staff profile created for user ID: " + userId);
                    break;
                case ADMIN:
                default:
                    System.out.println("✓ No additional profile needed for role: " + role);
                    break;
            }

        } catch (SQLException e) {
            System.err.println("Warning: Failed to create user profile for role " + role + ": " + e.getMessage());
            e.printStackTrace();
            // Don't throw exception here as the main account was already created
        }
    }

    /**
     * Creates a lansia profile
     * @param conn the database connection
     * @param userId the user ID
     */
    private void createLansiaProfile(Connection conn, UUID userId) throws SQLException {
        String sql = "INSERT INTO lansia (user_id) VALUES (?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Creates a family member profile
     * @param conn the database connection
     * @param userId the user ID
     */
    private void createFamilyProfile(Connection conn, UUID userId) throws SQLException {
        String sql = "INSERT INTO family_member (user_id) VALUES (?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Creates a medical staff profile
     * @param conn the database connection
     * @param userId the user ID
     */
    private void createMedicalStaffProfile(Connection conn, UUID userId) throws SQLException {
        String sql = "INSERT INTO medical_staff (user_id) VALUES (?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Changes user password
     * @param userId the user ID
     * @param oldPassword the current password
     * @param newPassword the new password
     * @return true if password changed successfully, false otherwise
     */
    public boolean changePassword(UUID userId, String oldPassword, String newPassword) {
        if (!DBUtil.isDatabaseAvailable()) {
            throw new RuntimeException("Database is not available. Please check your database connection and try again.");
        }

        // First verify the old password
        String selectSql = "SELECT password FROM user_account WHERE user_id = ?";
        String updateSql = "UPDATE user_account SET password = ? WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection()) {

            // Verify old password
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setObject(1, userId);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    String storedPassword = rs.getString("password");

                    if (!PasswordUtil.verifyPassword(oldPassword, storedPassword)) {
                        System.out.println("✗ Password change failed: Old password doesn't match");
                        return false; // Old password doesn't match
                    }
                } else {
                    System.out.println("✗ Password change failed: User not found");
                    return false; // User not found
                }
            }

            // Update password
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                String hashedNewPassword = PasswordUtil.hashPassword(newPassword);
                updateStmt.setString(1, hashedNewPassword);
                updateStmt.setObject(2, userId);

                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✓ Password changed successfully for user ID: " + userId);
                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to change password: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error during password change: " + e.getMessage());
        }

        return false;
    }

    /**
     * Gets user account by ID
     * @param userId the user ID
     * @return UserAccount object if found, null otherwise
     */
    public UserAccount getUserById(UUID userId) {
        if (!DBUtil.isDatabaseAvailable()) {
            throw new RuntimeException("Database is not available. Please check your database connection and try again.");
        }

        String sql = "SELECT user_id, username, password, role, created_at FROM user_account WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String roleString = rs.getString("role");
                Role role = Role.valueOf(roleString.toUpperCase());
                Timestamp createdAt = rs.getTimestamp("created_at");

                return new UserAccount(userId, username, password, role, createdAt);
            }

        } catch (SQLException e) {
            System.err.println("Failed to get user: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error during user retrieval: " + e.getMessage());
        }

        return null;
    }
}