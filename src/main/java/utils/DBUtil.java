package utils;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.net.UnknownHostException;

public class DBUtil {
    private static final Dotenv dotenv;
    private static final String URL;
    private static boolean connectionTested = false;
    private static boolean connectionAvailable = false;

    static {
        // Load .env file
        dotenv = Dotenv.configure().ignoreIfMissing().load();

        // Load database URL (which includes user and password as parameters)
        URL = dotenv.get("SUPABASE_DB_URL");

        // Validate configuration
        validateConfiguration();
    }

    private static void validateConfiguration() {
        if (URL == null || URL.trim().isEmpty()) {
            System.err.println("Error: SUPABASE_DB_URL is not configured in .env file");
            return;
        }

        // Check for placeholder values
        if (URL.contains("[YOUR-PASSWORD]") || URL.contains("[...]") || URL.contains("<your-")) {
            System.err.println("Error: SUPABASE_DB_URL contains placeholder values. Please update with your actual Supabase credentials.");
            System.err.println("Current URL: " + maskPassword(URL));
            return;
        }

        // Check if URL has the expected format
        if (!URL.startsWith("jdbc:postgresql://")) {
            System.err.println("Warning: Database URL should start with 'jdbc:postgresql://'");
            return;
        }

        // Check if URL contains user and password parameters
        if (!URL.contains("user=") || !URL.contains("password=")) {
            System.err.println("Warning: Database URL should contain 'user=' and 'password=' parameters");
            return;
        }

        // Check for quoted passwords and warn about it
        if (URL.contains("password='") || URL.contains("password=\"")) {
            System.err.println("Warning: Password appears to be quoted in the URL. JDBC URLs should not have quotes around password values.");
            System.err.println("Consider removing quotes from the password in your .env file");
        }

        System.out.println("Database URL configured: " + maskPassword(URL));
    }

    /**
     * Masks the password in the URL for logging purposes
     */
    private static String maskPassword(String url) {
        if (url == null) return "null";
        // Handle both quoted and unquoted passwords
        return url.replaceAll("password=[^&]*", "password=***");
    }

    /**
     * Gets a database connection using the full URL with embedded credentials
     */
    public static Connection getConnection() throws SQLException {
        if (URL == null || URL.trim().isEmpty()) {
            throw new SQLException("Database URL is not configured. Please check your .env file.");
        }

        if (URL.contains("[YOUR-PASSWORD]") || URL.contains("[...]")) {
            throw new SQLException("Database URL contains placeholder values. Please update your .env file with actual Supabase credentials.");
        }

        try {
            // Use the URL directly since it contains all connection parameters
            Connection conn = DriverManager.getConnection(URL);
            connectionAvailable = true;
            return conn;
        } catch (SQLException e) {
            connectionAvailable = false;

            // Provide more specific error messages
            if (e.getCause() instanceof UnknownHostException) {
                throw new SQLException("Cannot resolve database host. This might be due to:\n" +
                        "  - Network connectivity issues\n" +
                        "  - Incorrect hostname in database URL\n" +
                        "  - Supabase project might be paused or deleted\n" +
                        "Original error: " + e.getMessage(), e);
            }

            // Check for authentication issues
            if (e.getMessage().contains("authentication failed") || e.getMessage().contains("password authentication failed")) {
                throw new SQLException("Database authentication failed. Please check:\n" +
                        "  - Username and password are correct\n" +
                        "  - Password doesn't have extra quotes\n" +
                        "  - User format matches Supabase requirements\n" +
                        "Original error: " + e.getMessage(), e);
            }

            System.err.println("Failed to connect to database:");
            System.err.println("URL: " + maskPassword(URL));
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Tests if the database connection is working
     */
    public static boolean testConnection() {
        if (connectionTested) {
            return connectionAvailable;
        }

        try (Connection conn = getConnection()) {
            connectionTested = true;
            connectionAvailable = conn != null && !conn.isClosed();
            return connectionAvailable;
        } catch (SQLException e) {
            connectionTested = true;
            connectionAvailable = false;
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets a connection only if database is available, returns null otherwise
     */
    public static Connection getConnectionIfAvailable() {
        if (!testConnection()) {
            return null;
        }

        try {
            return getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to get database connection: " + e.getMessage());
            return null;
        }
    }

    /**
     * Checks if database connection is available
     */
    public static boolean isDatabaseAvailable() {
        return testConnection();
    }

    /**
     * Gets the database URL (with masked password for security)
     */
    public static String getDatabaseInfo() {
        return "Database URL: " + maskPassword(URL);
    }
}