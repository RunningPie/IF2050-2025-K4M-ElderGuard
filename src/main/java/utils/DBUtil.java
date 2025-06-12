package utils;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.net.UnknownHostException;

public class DBUtil {
    private static Dotenv dotenv;
    private static final String URL;
    private static boolean connectionTested = false;
    private static boolean connectionAvailable = false;

    static {
        // Prioritize system environment variable, then fall back to .env file
        String dbUrl = System.getenv("SUPABASE_DB_URL");

        if (dbUrl == null || dbUrl.trim().isEmpty()) {
            dotenv = Dotenv.configure().ignoreIfMissing().load();
            dbUrl = dotenv.get("SUPABASE_DB_URL");
        } else {
            dotenv = Dotenv.configure().ignoreIfMissing().load();
        }

        URL = dbUrl;
        validateConfiguration();
    }

    private static void validateConfiguration() {
        if (URL == null || URL.trim().isEmpty()) {
            System.err.println("Error: SUPABASE_DB_URL is not configured in the environment or .env file.");
        }
    }

    private static String maskPassword(String url) {
        if (url == null) return "null";
        return url.replaceAll("password=[^&]*", "password=***");
    }

    public static Connection getConnection() throws SQLException {
        if (URL == null || URL.trim().isEmpty()) {
            throw new SQLException("Database URL is not configured. Please check your environment variables or .env file.");
        }

        try {
            // **FIX**: Manually load the PostgreSQL driver class.
            // This ensures the driver is registered with the DriverManager in complex classloader environments.
            Class.forName("org.postgresql.Driver");

            // Use the URL directly to get the connection.
            Connection conn = DriverManager.getConnection(URL);
            connectionAvailable = true;
            return conn;
        } catch (ClassNotFoundException e) {
            // This new exception will be thrown if the postgresql jar is missing from the classpath.
            throw new SQLException("PostgreSQL JDBC driver not found in the JAR file. Please check your dependencies.", e);
        } catch (SQLException e) {
            connectionAvailable = false;
            if (e.getCause() instanceof UnknownHostException) {
                throw new SQLException("Cannot resolve database host. Check network connectivity or hostname.", e);
            }
            if (e.getMessage().contains("authentication failed")) {
                throw new SQLException("Database authentication failed. Check username and password.", e);
            }
            System.err.println("Failed to connect to database: " + maskPassword(URL));
            throw e;
        }
    }

    // Other methods remain the same...

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
            return false;
        }
    }

    public static Connection getConnectionIfAvailable() {
        if (!testConnection()) {
            return null;
        }
        try {
            return getConnection();
        } catch (SQLException e) {
            return null;
        }
    }

    public static boolean isDatabaseAvailable() {
        return testConnection();
    }

    public static String getDatabaseInfo() {
        return "Database URL: " + maskPassword(URL);
    }
}
