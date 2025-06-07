package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.UserAccount;
import model.Role;
import model.Permission;
import security.AccessControlManager;
import utils.SessionManager;
import service.AuthService;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class AccountController {

    @FXML private TextField usernameField;
    @FXML private Label roleLabel;
    @FXML private Label roleDisplayLabel;
    @FXML private Label memberSinceLabel;
    @FXML private Label userIdLabel;
    @FXML private Label lastLoginLabel;
    @FXML private Label sessionTimeLabel;
    @FXML private Label statusLabel;

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private VBox roleSpecificSection;
    @FXML private Label roleSpecificTitle;
    @FXML private VBox roleSpecificContent;
    @FXML private ListView<String> permissionsList;

    private AuthService authService;
    private UserAccount currentUser;
    private Timeline sessionTimer;

    public AccountController() {
        this.authService = new AuthService();
    }

    @FXML
    private void initialize() {
        loadUserData();
        setupRoleSpecificContent();
        loadPermissions();
        startSessionTimer();
    }

    private void loadUserData() {
        currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            showErrorMessage("No user session found");
            return;
        }

        // Populate user information
        if (usernameField != null) {
            usernameField.setText(currentUser.getUsername());
        }

        if (roleLabel != null) {
            roleLabel.setText("Role: " + getRoleDisplayName(currentUser.getUserRole()));
        }

        if (roleDisplayLabel != null) {
            roleDisplayLabel.setText(getRoleDisplayName(currentUser.getUserRole()));
        }

        if (memberSinceLabel != null) {
            memberSinceLabel.setText(currentUser.getCreatedAt()
                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        }

        if (userIdLabel != null) {
            userIdLabel.setText(currentUser.getUserID().toString());
        }

        if (lastLoginLabel != null) {
            lastLoginLabel.setText("Current session");
        }
    }

    private void setupRoleSpecificContent() {
        if (currentUser == null || roleSpecificSection == null) return;

        Role userRole = currentUser.getUserRole();

        switch (userRole) {
            case FAMILY:
                setupFamilySpecificContent();
                break;
            case LANSIA:
                setupLansiaSpecificContent();
                break;
            case MEDICAL_STAFF:
                setupMedicalStaffSpecificContent();
                break;
            case ADMIN:
                setupAdminSpecificContent();
                break;
        }
    }

    private void setupFamilySpecificContent() {
        if (roleSpecificTitle != null) {
            roleSpecificTitle.setText("Family Information");
        }

        if (roleSpecificContent != null) {
            roleSpecificContent.getChildren().clear();

            Label familyGroupLabel = new Label("Family Group ID: " +
                    (currentUser.getFamilyGroupId() != null ? currentUser.getFamilyGroupId().toString() : "Not assigned"));

            Label membersLabel = new Label("Family Members: 3 registered");
            Label alertsLabel = new Label("Emergency Alerts Access: Family members only");

            Button manageFamilyBtn = new Button("Manage Family Members");
            manageFamilyBtn.setOnAction(e -> showInfoMessage("Family management feature coming soon"));

            roleSpecificContent.getChildren().addAll(familyGroupLabel, membersLabel, alertsLabel, manageFamilyBtn);
        }
    }

    private void setupLansiaSpecificContent() {
        if (roleSpecificTitle != null) {
            roleSpecificTitle.setText("Personal Health Information");
        }

        if (roleSpecificContent != null) {
            roleSpecificContent.getChildren().clear();

            Label healthStatusLabel = new Label("Health Monitoring: Active");
            Label emergencyContactLabel = new Label("Emergency Contacts: 2 configured");
            Label medicationLabel = new Label("Medication Reminders: Enabled");

            Button updateHealthBtn = new Button("Update Health Profile");
            updateHealthBtn.setOnAction(e -> showInfoMessage("Health profile update feature coming soon"));

            roleSpecificContent.getChildren().addAll(healthStatusLabel, emergencyContactLabel, medicationLabel, updateHealthBtn);
        }
    }

    private void setupMedicalStaffSpecificContent() {
        if (roleSpecificTitle != null) {
            roleSpecificTitle.setText("Medical Staff Information");
        }

        if (roleSpecificContent != null) {
            roleSpecificContent.getChildren().clear();

            Label departmentLabel = new Label("Department: Emergency Care");
            Label licenseLabel = new Label("Medical License: Active");
            Label patientsLabel = new Label("Assigned Patients: 127");
            Label shiftsLabel = new Label("Current Shift: Day (08:00 - 20:00)");

            Button updateLicenseBtn = new Button("Update License Information");
            updateLicenseBtn.setOnAction(e -> showInfoMessage("License update feature coming soon"));

            roleSpecificContent.getChildren().addAll(departmentLabel, licenseLabel, patientsLabel, shiftsLabel, updateLicenseBtn);
        }
    }

    private void setupAdminSpecificContent() {
        if (roleSpecificTitle != null) {
            roleSpecificTitle.setText("Administrator Information");
        }

        if (roleSpecificContent != null) {
            roleSpecificContent.getChildren().clear();

            Label systemAccessLabel = new Label("System Access: Full Administrative");
            Label usersLabel = new Label("Total Users: 1,247");
            Label alertsLabel = new Label("System Alerts: 3 pending");

            Button systemManagementBtn = new Button("System Management");
            systemManagementBtn.setOnAction(e -> showInfoMessage("System management panel coming soon"));

            roleSpecificContent.getChildren().addAll(systemAccessLabel, usersLabel, alertsLabel, systemManagementBtn);
        }
    }

    private void loadPermissions() {
        if (permissionsList == null) return;

        Set<Permission> permissions = AccessControlManager.getCurrentUserPermissions();
        ObservableList<String> permissionNames = FXCollections.observableArrayList();

        for (Permission permission : permissions) {
            permissionNames.add("✓ " + getPermissionDisplayName(permission));
        }

        permissionsList.setItems(permissionNames);
    }

    private String getPermissionDisplayName(Permission permission) {
        switch (permission) {
            case VIEW_DASHBOARD: return "View Dashboard";
            case VIEW_FAMILY_EMERGENCY_ALERTS: return "View Family Emergency Alerts";
            case VIEW_ALL_EMERGENCY_ALERTS: return "View All Emergency Alerts";
            case CREATE_EMERGENCY_ALERT: return "Create Emergency Alerts";
            case RESPOND_TO_EMERGENCY_ALERT: return "Respond to Emergency Alerts";
            case VIEW_OWN_ACCOUNT: return "View Own Account";
            case EDIT_OWN_ACCOUNT: return "Edit Own Account";
            case VIEW_FAMILY_ACCOUNTS: return "View Family Accounts";
            case EDIT_FAMILY_ACCOUNTS: return "Edit Family Accounts";
            case VIEW_ALL_ACCOUNTS: return "View All Accounts";
            case EDIT_ALL_ACCOUNTS: return "Edit All Accounts";
            case DELETE_ACCOUNTS: return "Delete Accounts";
            case MANAGE_USERS: return "Manage Users";
            case MANAGE_SYSTEM_SETTINGS: return "Manage System Settings";
            case VIEW_SYSTEM_LOGS: return "View System Logs";
            default: return permission.toString();
        }
    }

    private void startSessionTimer() {
        // Update session time every minute
        sessionTimer = new Timeline(new KeyFrame(Duration.minutes(1), e -> updateSessionTime()));
        sessionTimer.setCycleCount(Timeline.INDEFINITE);
        sessionTimer.play();

        updateSessionTime();
    }

    private void updateSessionTime() {
        if (sessionTimeLabel != null) {
            long remainingTime = SessionManager.getSessionRemainingTime();
            long minutes = remainingTime / (1000 * 60);
            sessionTimeLabel.setText(minutes + " minutes");

            if (minutes < 5) {
                sessionTimeLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            } else if (minutes < 10) {
                sessionTimeLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
            } else {
                sessionTimeLabel.setStyle("-fx-text-fill: #27ae60;");
            }
        }
    }

    @FXML
    private void updateProfile() {
        try {
            AccessControlManager.requirePermission(Permission.EDIT_OWN_ACCOUNT);

            String newUsername = usernameField.getText().trim();
            if (newUsername.isEmpty()) {
                showErrorMessage("Username cannot be empty");
                return;
            }

            if (newUsername.length() < 3) {
                showErrorMessage("Username must be at least 3 characters long");
                return;
            }

            // Update username (this would typically update the database)
            currentUser.setUsername(newUsername);

            showSuccessMessage("Profile updated successfully");

        } catch (SecurityException e) {
            showErrorMessage("Access denied: " + e.getMessage());
        }
    }

    @FXML
    private void changePassword() {
        try {
            AccessControlManager.requirePermission(Permission.EDIT_OWN_ACCOUNT);

            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Validate input
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showErrorMessage("All password fields are required");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                showErrorMessage("New passwords do not match");
                return;
            }

            if (newPassword.length() < 6) {
                showErrorMessage("New password must be at least 6 characters long");
                return;
            }

            // Verify current password
            if (!authService.verifyPassword(currentUser.getUsername(), currentPassword)) {
                showErrorMessage("Current password is incorrect");
                return;
            }

            // Update password (this would typically update the database)
            boolean success = authService.updatePassword(currentUser.getUsername(), newPassword);

            if (success) {
                currentUser.setPassword(newPassword);

                // Clear password fields
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();

                showSuccessMessage("Password changed successfully");
            } else {
                showErrorMessage("Failed to update password");
            }

        } catch (SecurityException e) {
            showErrorMessage("Access denied: " + e.getMessage());
        }
    }

    @FXML
    private void extendSession() {
        SessionManager.refreshSession();
        updateSessionTime();
        showSuccessMessage("Session extended successfully");
    }

    @FXML
    private void endSession() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("End Session");
        confirm.setHeaderText("Are you sure you want to end your session?");
        confirm.setContentText("You will be logged out and redirected to the login page.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (sessionTimer != null) {
                    sessionTimer.stop();
                }
                SessionManager.clearSession();
                navigateToLogin();
            }
        });
    }

    @FXML
    private void goBack() {
        try {
            if (currentUser != null) {
                String dashboardPath = getDashboardPath(currentUser.getUserRole());
                navigateToView(dashboardPath, "Dashboard");
            }
        } catch (Exception e) {
            showErrorMessage("Failed to navigate back: " + e.getMessage());
        }
    }

    private String getDashboardPath(Role role) {
        switch (role) {
            case FAMILY: return "/view/FamilyDashboard.fxml";
            case MEDICAL_STAFF: return "/view/MedicalDashboard.fxml";
            case LANSIA: return "/view/LansiaDashboard.fxml";
            case ADMIN: return "/view/AdminDashboard.fxml";
            default: return "/view/LoginView.fxml";
        }
    }

    private String getRoleDisplayName(Role role) {
        switch (role) {
            case ADMIN: return "Administrator";
            case LANSIA: return "Elderly User";
            case FAMILY: return "Family Member";
            case MEDICAL_STAFF: return "Medical Staff";
            default: return role.toString();
        }
    }

    private void navigateToView(String fxmlPath, String title) {
        try {
            if (sessionTimer != null) {
                sessionTimer.stop();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = getCurrentStage();
            if (stage != null) {
                stage.setScene(new Scene(root));
                stage.setTitle("ElderGuard - " + title);
                stage.centerOnScreen();
            }
        } catch (IOException e) {
            showErrorMessage("Navigation failed: " + e.getMessage());
        }
    }

    private void navigateToLogin() {
        navigateToView("/view/LoginView.fxml", "Login");
    }

    private Stage getCurrentStage() {
        if (usernameField != null && usernameField.getScene() != null) {
            return (Stage) usernameField.getScene().getWindow();
        }
        return null;
    }

    private void showSuccessMessage(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Success", message);
        updateStatusLabel(message, "-fx-text-fill: #27ae60;");
    }

    private void showErrorMessage(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", message);
        updateStatusLabel(message, "-fx-text-fill: #e74c3c;");
    }

    private void showInfoMessage(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Information", message);
        updateStatusLabel(message, "-fx-text-fill: #3498db;");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateStatusLabel(String message, String style) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle(style);
        }
    }
}