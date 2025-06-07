package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class AuthController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            return;
        }

        if ("admin".equals(username) && "admin".equals(password)) {
            errorLabel.setText("Login berhasil!");
        } else {
            errorLabel.setText("Username atau password salah.");
        }
    }

    @FXML
    private void handleMenuHover(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle(button.getStyle() + "-fx-background-color: #4A7BB8;");
    }

    @FXML
    private void handleMenuExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle(button.getStyle().replace("-fx-background-color: #4A7BB8;", "-fx-background-color: transparent;"));
    }

    // Hover handlers for login button
    @FXML
    private void handleLoginHover(MouseEvent event) {
        loginButton.setStyle(loginButton.getStyle() + "-fx-background-color: linear-gradient(to bottom, #5A8BC8, #3E6CA6);");
    }

    @FXML
    private void handleLoginExit(MouseEvent event) {
        loginButton.setStyle(loginButton.getStyle().replace(
                "-fx-background-color: linear-gradient(to bottom, #5A8BC8, #3E6CA6);",
                "-fx-background-color: linear-gradient(to bottom, #4A7BB8, #2E5C96);"
        ));
    }
}
