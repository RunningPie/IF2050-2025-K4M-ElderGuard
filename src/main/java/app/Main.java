package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import utils.DBUtil;
import java.sql.Connection;
import services.MonitoringService;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Start Monitoring as Background Service
            MonitoringService monitoringService = new MonitoringService();
            monitoringService.startMonitoring();

            // Load the login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Parent root = loader.load();

            // Configure the primary stage
            primaryStage.setTitle("ElderGuard - Healthcare Management System");

            // Configure the primary stage
            primaryStage.setTitle("ElderGuard - Healthcare Management System");
            primaryStage.setScene(new Scene(root));
            primaryStage.setWidth(750);
            primaryStage.setHeight(750);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();

            // Show the application
            primaryStage.setWidth(750);
            primaryStage.setHeight(750);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();

            // Show the application
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load the main application view: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Test database connection before launching the application
        // Test database connection before launching the application
        testDatabaseConnection();

        // Launch the JavaFX application
        // Launch the JavaFX application
        launch(args);
    }

    private static void testDatabaseConnection() {
        System.out.println("=== ElderGuard Application Starting ===");
        System.out.println("Testing database connection...");

        try (Connection conn = DBUtil.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Database connection successful!");
                System.out.println("  Connected to: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("  Database URL: " + DBUtil.getDatabaseInfo());
            } else {
                System.out.println("✗ Database connection failed - connection is null or closed");
                System.out.println("✓ Database connection successful!");
                System.out.println("  Connected to: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("  Database URL: " + DBUtil.getDatabaseInfo());
            }
        } catch (Exception e) {
            System.out.println("✗ Database connection failed: " + e.getMessage());
            System.out.println("  The application will continue but database features may not work properly.");
            System.out.println("  Please check your .env file and database configuration.");
        }

        System.out.println("=== Starting JavaFX Application ===");
    }
}