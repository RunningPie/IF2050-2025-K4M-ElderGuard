package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.UserAccount;
import model.Role;
import service.AuthService;
import utils.SessionManager;

import java.io.IOException;

public class AuthController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button signUpButton;

    @FXML
    private Label errorLabel;

    @FXML
    private ComboBox<Role> roleComboBox;

    private AuthService authService;

    public AuthController() {
        this.authService = new AuthService();
    }

    @FXML
    private void initialize() {
        // Initialize role combo box if it exists (for signup)
        if (roleComboBox != null) {
            roleComboBox.getItems().addAll(Role.values());
            roleComboBox.setValue(Role.LANSIA); // Default value
        }

        // Add enter key handler for login
        if (passwordField != null) {
            passwordField.setOnAction(e -> handleLogin());
        }
    }

    @FXML
    private void handleLogin() {
        clearErrorMessage();

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validate input
        if (username.isEmpty()) {
            showError("Username cannot be empty!");
            return;
        }

        if (password.isEmpty()) {
            showError("Password cannot be empty!");
            return;
        }

        try {
            // Disable login button to prevent multiple clicks
            loginButton.setDisable(true);

            // Attempt authentication
            UserAccount user = authService.authenticate(username, password);

            if (user != null) {
                // Store user session
                SessionManager.setCurrentUser(user);

                // Navigate to appropriate dashboard based on role
                navigateToDashboard(user.getUserRole());

            } else {
                showError("Invalid username or password!");
            }

        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            loginButton.setDisable(false);
        }
    }

    @FXML
    private void handleSignUp() {
        try {
            // Load signup view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SignUpView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("ElderGuard - Sign Up");

        } catch (IOException e) {
            showError("Failed to load sign up page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignUpSubmit() {
        clearErrorMessage();

        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        Role role = roleComboBox.getValue();

        // Validate input
        if (username.isEmpty()) {
            showError("Username cannot be empty!");
            return;
        }

        if (password.isEmpty()) {
            showError("Password cannot be empty!");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters long!");
            return;
        }

        if (role == null) {
            showError("Please select a role!");
            return;
        }

        try {
            // Disable signup button to prevent multiple clicks
            signUpButton.setDisable(true);

            // Create new user account
            boolean success = authService.createAccount(username, password, role);

            if (success) {
                showSuccess("Account created successfully! Please login.");

                // Navigate back to login
                navigateToLogin();

            } else {
                showError("Failed to create account. Username might already exist.");
            }

        } catch (Exception e) {
            showError("Sign up failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            signUpButton.setDisable(false);
        }
    }

    @FXML
    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((usernameField != null) ? usernameField : signUpButton).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("ElderGuard - Login");
            stage.getScene().getRoot().requestLayout();


        } catch (IOException e) {
            showError("Failed to load login page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToDashboard(Role role) {
        try {
            String fxmlPath = getDashboardPath(role);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("ElderGuard - " + role + " Dashboard");
            stage.getScene().getRoot().requestLayout();


        } catch (IOException e) {
            showError("Failed to load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getDashboardPath(Role role) {
        switch (role) {
            case ADMIN:
                return "/view/AdminDashboard.fxml";
            case LANSIA:
                return "/view/LansiaDashboard.fxml";
            case FAMILY:
                return "/view/FamilyDashboard.fxml";
            case MEDICAL_STAFF:
                return "/view/MedicalDashboard.fxml";
            default:
                return "/view/LoginView.fxml";
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    private void showSuccess(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setStyle("-fx-text-fill: #27ae60;");
        }
    }

    private void showInfo(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setStyle("-fx-text-fill: #3498db;");
        }
    }

    private void clearErrorMessage() {
        if (errorLabel != null) {
            errorLabel.setText("");
        }
    }
}