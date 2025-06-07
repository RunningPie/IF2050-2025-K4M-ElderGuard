package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import utils.DBUtil;
import java.sql.Connection;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // pastikan path ini cocok dengan lokasi di resources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("ElderGuard - Login");
            primaryStage.setScene(new Scene(root));
            primaryStage.setWidth(500);
            primaryStage.setHeight(500);
            primaryStage.show();

            System.out.println("ElderGuard application started successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Gagal memuat tampilan utama.");
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting ElderGuard application...");

        // Test database connection
        testDatabaseConnection();

        // Launch JavaFX application
        System.out.println("Launching JavaFX application...");
        launch(args);
    }

    private static void testDatabaseConnection() {
        try {
            System.out.println("Testing database connection...");
            Connection conn = DBUtil.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Successfully connected to Supabase Database!");
                conn.close();
            }
        } catch (Exception e) {
            System.err.println("✗ Database connection failed: " + e.getMessage());

            // Check for specific error types and provide helpful messages
            if (e.getMessage().contains("Wrong password")) {
                System.err.println("Authentication Error: Please check your database password in the .env file");
            } else if (e.getMessage().contains("UnknownHostException")) {
                System.err.println("Network Error: Cannot reach the database server");
            } else {
                System.err.println("Database Error: " + e.getCause());
            }

            System.err.println("");
            System.err.println("TROUBLESHOOTING TIPS:");
            System.err.println("1. Check your .env file password (no quotes needed)");
            System.err.println("2. Verify your Supabase project is active (not paused)");
            System.err.println("3. Check your internet connection");
            System.err.println("4. Verify database credentials in Supabase dashboard");
            System.err.println("");
            System.err.println("✓ The application will continue without database functionality.");
            System.err.println("You can fix the database connection later and restart the app.");
        }
    }
}