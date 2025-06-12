package app;

/**
 * A simple launcher class to start the JavaFX application.
 * This class is necessary for creating a runnable fat JAR that includes JavaFX components.
 * The JVM will run the main method in this class, which then calls the main method
 * of the actual JavaFX application (Main.class).
 */
public class Launcher {
    public static void main(String[] args) {
        // Calls the main method of your actual JavaFX Application class
        Main.main(args);
    }
}
