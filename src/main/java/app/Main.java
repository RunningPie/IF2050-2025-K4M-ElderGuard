package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

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
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Gagal memuat tampilan utama.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
