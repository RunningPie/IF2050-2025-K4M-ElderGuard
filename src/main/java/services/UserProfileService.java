package service;

import models.UserAccount;
import models.UserProfile;
import models.Role;
import utils.DBUtil;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

public class UserProfileService {

    /**
     * Get complete user profile data from database
     */
// Replace the getUserProfile method in UserProfileService.java

    public UserProfile getUserProfile(UUID userId) {
        String sql = """
    SELECT 
        ua.user_id, ua.username, ua.contact_info, ua.role, ua.created_at,
        l.birthdate,
        wd.model as device_model, wd.battery_level,
        h.hospital_name,
        ms.created_at as employment_date,
        (SELECT COUNT(DISTINCT fm.lansia_id) FROM family_member fm WHERE fm.family_member_id = ua.user_id) as family_member_count
    FROM user_account ua
    LEFT JOIN lansia l ON ua.user_id = l.user_id
    LEFT JOIN wearable_device wd ON l.user_id = wd.lansia_id
    LEFT JOIN medical_staff ms ON ua.user_id = ms.user_id
    LEFT JOIN hospital h ON ms.hospital_id = h.hospital_id
    WHERE ua.user_id = ?::uuid
    """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Pass UUID as string, PostgreSQL will cast it
            stmt.setString(1, userId.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserProfile profile = new UserProfile();

                    // Basic user data
                    profile.setUserId(UUID.fromString(rs.getString("user_id")));
                    profile.setUsername(rs.getString("username"));
                    profile.setContactInfo(rs.getString("contact_info"));
                    profile.setRole(Role.valueOf(rs.getString("role")));

                    // Handle created_at timestamp
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        profile.setCreatedAt(createdAt.toInstant().atZone(ZoneId.systemDefault()));
                    }

                    // Lansia-specific data
                    Timestamp birthdate = rs.getTimestamp("birthdate");
                    if (birthdate != null) {
                        profile.setBirthdate(birthdate.toLocalDateTime());
                    }
                    profile.setDeviceModel(rs.getString("device_model"));

                    // Fix Float conversion issue - use getFloat() instead of getObject()
                    float batteryLevel = rs.getFloat("battery_level");
                    if (!rs.wasNull()) {
                        profile.setBatteryLevel(batteryLevel);
                    }

                    // Medical Staff data
                    profile.setHospitalName(rs.getString("hospital_name"));

                    Timestamp employmentDate = rs.getTimestamp("employment_date");
                    if (employmentDate != null) {
                        profile.setEmploymentDate(employmentDate.toLocalDateTime());
                    }

                    // Family data - fix Integer conversion
                    int familyCount = rs.getInt("family_member_count");
                    if (!rs.wasNull()) {
                        profile.setFamilyMemberCount(familyCount);
                    }

                    // Admin data
                    if (profile.getRole() == Role.ADMIN) {
                        profile.setTotalSystemUsers(getTotalSystemUsers());
                        profile.setTotalDevices(getTotalDevices());
                    }

                    return profile;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user profile: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Helper methods for admin data
    private Integer getTotalSystemUsers() {
        String sql = "SELECT COUNT(*) FROM user_account";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total users: " + e.getMessage());
        }
        return null;
    }

    private Integer getTotalDevices() {
        String sql = "SELECT COUNT(*) FROM wearable_device";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total devices: " + e.getMessage());
        }
        return null;
    }

    /**
     * Load Lansia-specific data
     */
    private void loadLansiaData(UserProfile profile) throws SQLException {
        String query = """
                SELECT l.birthdate, wd.model as device_model, wd.battery_level 
                FROM lansia l 
                LEFT JOIN wearable_device wd ON l.user_id = wd.lansia_id 
                WHERE l.user_id = ?::uuid
                """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, profile.getUserId().toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp birthdate = rs.getTimestamp("birthdate");
                if (birthdate != null) {
                    profile.setBirthdate(birthdate.toLocalDateTime());
                }

                profile.setDeviceModel(rs.getString("device_model"));

                Float batteryLevel = rs.getObject("battery_level", Float.class);
                profile.setBatteryLevel(batteryLevel);
            }
        }
    }

    /**
     * Load Medical Staff-specific data
     */
    private void loadMedicalStaffData(UserProfile profile) throws SQLException {
        String query = """
                SELECT h.hospital_name, ms.created_at as employment_date 
                FROM medical_staff ms 
                JOIN hospital h ON ms.hospital_id = h.hospital_id 
                WHERE ms.user_id = ?::uuid
                """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, profile.getUserId().toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                profile.setHospitalName(rs.getString("hospital_name"));

                Timestamp employmentDate = rs.getTimestamp("employment_date");
                if (employmentDate != null) {
                    profile.setEmploymentDate(employmentDate.toLocalDateTime());
                }
            }
        }
    }

    /**
     * Load Family-specific data
     */
    private void loadFamilyData(UserProfile profile) throws SQLException {
        String query = """
                SELECT COUNT(DISTINCT fm.lansia_id) as family_member_count 
                FROM family_member fm 
                WHERE fm.family_member_id = ?::uuid
                """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, profile.getUserId().toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                profile.setFamilyMemberCount(rs.getInt("family_member_count"));
            }
        }
    }

    /**
     * Update user profile information
     */
    public boolean updateUserProfile(UserProfile profile) {
        String updateUserQuery = "UPDATE user_account SET username = ?, contact_info = ? WHERE user_id = ?::uuid";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try {
                // Update main user account
                try (PreparedStatement stmt = conn.prepareStatement(updateUserQuery)) {
                    stmt.setString(1, profile.getUsername());
                    stmt.setString(2, profile.getContactInfo());
                    stmt.setString(3, profile.getUserId().toString());
                    int rowsUpdated = stmt.executeUpdate();

                    if (rowsUpdated == 0) {
                        throw new SQLException("No user found with ID: " + profile.getUserId());
                    }
                }

                // Update role-specific data
                updateRoleSpecificData(conn, profile);

                conn.commit(); // Commit transaction
                return true;

            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Error updating user profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update role-specific data
     */
    private void updateRoleSpecificData(Connection conn, UserProfile profile) throws SQLException {
        switch (profile.getRole()) {
            case LANSIA:
                updateLansiaData(conn, profile);
                break;
            case MEDICAL_STAFF:
                updateMedicalStaffData(conn, profile);
                break;
            case FAMILY:
                updateFamilyData(conn, profile);
                break;
            // Admin data is usually read-only system statistics
        }
    }

    /**
     * Update Lansia-specific data
     */
    private void updateLansiaData(Connection conn, UserProfile profile) throws SQLException {
        if (profile.getBirthdate() != null) {
            // Check if lansia record exists
            String checkQuery = "SELECT COUNT(*) FROM lansia WHERE user_id = ?::uuid";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, profile.getUserId().toString());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    // Update existing record
                    String updateQuery = "UPDATE lansia SET birthdate = ? WHERE user_id = ?::uuid";
                    try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                        stmt.setTimestamp(1, Timestamp.valueOf(profile.getBirthdate()));
                        stmt.setString(2, profile.getUserId().toString());
                        stmt.executeUpdate();
                    }
                } else {
                    // Create new record
                    String insertQuery = "INSERT INTO lansia (user_id, birthdate) VALUES (?::uuid, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                        stmt.setString(1, profile.getUserId().toString());
                        stmt.setTimestamp(2, Timestamp.valueOf(profile.getBirthdate()));
                        stmt.executeUpdate();
                    }
                }
            }
        }
    }

    /**
     * Update Medical Staff-specific data
     */
    private void updateMedicalStaffData(Connection conn, UserProfile profile) throws SQLException {
        // Medical staff data is typically managed by admin, not self-updated
        // This method can be extended for specific medical staff updates if needed
    }

    /**
     * Update Family-specific data
     */
    private void updateFamilyData(Connection conn, UserProfile profile) throws SQLException {
        // Family-specific updates can be added here
        // For example, updating family relationships
    }

    /**
     * Parse location from contact_info field
     * Assumes format: "+62 812-3456-7890|Bandung|West Java"
     */
    public static String[] parseLocation(String contactInfo) {
        if (contactInfo != null && contactInfo.contains("|")) {
            String[] parts = contactInfo.split("\\|");
            if (parts.length >= 3) {
                return new String[]{parts[1], parts[2]}; // city, state
            }
        }
        return new String[]{"", ""};
    }

    /**
     * Format contact info with location
     * Format: "phone|city|state"
     */
    public static String formatContactInfo(String phone, String city, String state) {
        return phone + "|" + city + "|" + state;
    }

    /**
     * Extract phone from contact_info
     */
    public static String extractPhone(String contactInfo) {
        if (contactInfo != null && contactInfo.contains("|")) {
            return contactInfo.split("\\|")[0];
        }
        return contactInfo != null ? contactInfo : "";
    }

    /**
     * Validate phone number format
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && !phone.trim().isEmpty();
        // Basic validation for Indonesian phone numbers
    }

    /**
     * Get user by username (for authentication)
     */
    public UserAccount getUserByUsername(String username) {
        String query = "SELECT user_id, username, password, contact_info, role, created_at " +
                "FROM user_account WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UUID userId = UUID.fromString(rs.getString("user_id"));
                String password = rs.getString("password");
                String contactInfo = rs.getString("contact_info");
                Role role = Role.valueOf(rs.getString("role"));

                Timestamp createdAt = rs.getTimestamp("created_at");
                ZonedDateTime createdAtZoned = createdAt.toInstant().atZone(java.time.ZoneId.systemDefault());

                return new UserAccount(userId, username, password, contactInfo, role, createdAtZoned);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user by username: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}