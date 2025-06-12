package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.UserAccount;
import models.Role;
import services.AuthService;
import utils.SessionManager;
import security.AccessControlManager;

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
            roleComboBox.getItems().addAll(Role.LANSIA, Role.FAMILY, Role.MEDICAL_STAFF);
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
            if (loginButton != null) {
                loginButton.setDisable(true);
            }

            // Attempt authentication
            UserAccount user = authService.authenticate(username, password);

            if (user != null) {
                // Store user session
                SessionManager.setCurrentUser(user);

                // Navigate to appropriate page based on role
                navigateAfterLogin(user.getUserRole());

            } else {
                showError("Invalid username or password!");
            }

        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (loginButton != null) {
                loginButton.setDisable(false);
            }
        }
    }

    @FXML
    private void handleSignUp() {
        try {
            // Load signup view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SignUpView.fxml"));
            Parent root = loader.load();

            Stage stage = getCurrentStage();
            if (stage != null) {
                stage.setScene(new Scene(root));
                stage.setTitle("ElderGuard - Sign Up");
            }

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
            if (signUpButton != null) {
                signUpButton.setDisable(true);
            }

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
            if (signUpButton != null) {
                signUpButton.setDisable(false);
            }
        }
    }

    @FXML
    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = getCurrentStage();
            if (stage != null) {
                stage.setScene(new Scene(root));
                stage.setTitle("ElderGuard - Login");
            }

        } catch (IOException e) {
            showError("Failed to load login page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateAfterLogin(Role role) {
        try {
            String fxmlPath = AccessControlManager.getDefaultPageAfterLogin(role);
            String title = getPageTitle(role);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = getCurrentStage();
            if (stage != null) {
                stage.setScene(new Scene(root));
                stage.setTitle("ElderGuard - " + title);
            }

        } catch (IOException e) {
            showError("Failed to load page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getPageTitle(Role role) {
        switch (role) {
            case FAMILY:
                return "Family Dashboard";
            case LANSIA:
                return "Lansia Dashboard";
            case MEDICAL_STAFF:
                return "Emergency Alerts";
            case ADMIN:
                return "Admin Dashboard";
            default:
                return "Dashboard";
        }
    }

    private Stage getCurrentStage() {
        if (loginButton != null && loginButton.getScene() != null) {
            return (Stage) loginButton.getScene().getWindow();
        }
        if (usernameField != null && usernameField.getScene() != null) {
            return (Stage) usernameField.getScene().getWindow();
        }
        if (signUpButton != null && signUpButton.getScene() != null) {
            return (Stage) signUpButton.getScene().getWindow();
        }
        return null;
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

    private void clearErrorMessage() {
        if (errorLabel != null) {
            errorLabel.setText("");
        }
    }
}