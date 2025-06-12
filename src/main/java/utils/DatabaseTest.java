package utils;

import java.sql.*;

/**
 * Simple test class to verify database connection and setup
 */
public class DatabaseTest {

    public static void main(String[] args) {
        System.out.println("=== ElderGuard Database Connection Test ===");

        // Test 1: Basic connection
        testConnection();

        // Test 2: Check if tables exist
        checkTables();

        // Test 3: Try to insert and retrieve a test user
        testUserOperations();
    }

    private static void testConnection() {
        System.out.println("\n1. Testing basic database connection...");

        try (Connection conn = DBUtil.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Database connection successful!");
                System.out.println("   Database: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("   Version: " + conn.getMetaData().getDatabaseProductVersion());
                System.out.println("   URL: " + DBUtil.getDatabaseInfo());
            } else {
                System.out.println("✗ Database connection failed - connection is null or closed");
            }
        } catch (Exception e) {
            System.out.println("✗ Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void checkTables() {
        System.out.println("\n2. Checking if required tables exist...");

        String[] requiredTables = {
                "user_account", "lansia", "family_member", "wearable_device",
                "sensor", "emergency_event", "medical_history", "server_logs"
        };

        try (Connection conn = DBUtil.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();

            for (String table : requiredTables) {
                ResultSet rs = meta.getTables(null, null, table, new String[]{"TABLE"});
                if (rs.next()) {
                    System.out.println("✓ Table '" + table + "' exists");
                } else {
                    System.out.println("✗ Table '" + table + "' NOT found");
                }
                rs.close();
            }

        } catch (Exception e) {
            System.out.println("✗ Failed to check tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testUserOperations() {
        System.out.println("\n3. Testing user operations...");

        try (Connection conn = DBUtil.getConnection()) {

            // Test if we can query the user_account table
            String sql = "SELECT COUNT(*) as user_count FROM user_account";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    int userCount = rs.getInt("user_count");
                    System.out.println("✓ Successfully queried user_account table");
                    System.out.println("   Current user count: " + userCount);
                } else {
                    System.out.println("✗ No results from user_account query");
                }
            }

            // Test if we can see the table structure
            String describeSql = "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = 'user_account' ORDER BY ordinal_position";
            try (PreparedStatement stmt = conn.prepareStatement(describeSql);
                 ResultSet rs = stmt.executeQuery()) {

                System.out.println("✓ user_account table structure:");
                while (rs.next()) {
                    String columnName = rs.getString("column_name");
                    String dataType = rs.getString("data_type");
                    System.out.println("   - " + columnName + " (" + dataType + ")");
                }
            }

        } catch (Exception e) {
            System.out.println("✗ Failed to test user operations: " + e.getMessage());
            e.printStackTrace();
        }
    }
}