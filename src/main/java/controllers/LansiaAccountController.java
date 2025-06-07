package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import models.UserAccount;
import models.UserProfile;
import models.Role;
import utils.SessionManager;
import service.AuthService;
import service.UserProfileService;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class LansiaAccountController extends NavigationController{

    @FXML
    private VBox expandedSidebar;

    @FXML
    private VBox collapsedSidebar;

    @FXML
    private Button toggleSidebarButton;

    @FXML
    private Button toggleSidebarButtonCollapsed;

    // Navigation buttons (expanded)
    @FXML
    private Button dashboardButton;

    @FXML
    private Button emergencyAlertsButton;

    @FXML
    private Button accountButton;

    // Navigation buttons (collapsed)
    @FXML
    private Button dashboardButtonCollapsed;

    @FXML
    private Button emergencyAlertsButtonCollapsed;

    @FXML
    private Button accountButtonCollapsed;

    // Form fields
    @FXML
    private TextField nameField;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button editNameButton;

    @FXML
    private Button editLocationButton;

    @FXML
    private Button editPhoneButton;

    @FXML
    private Button editPasswordButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label statusLabel;

    private AuthService authService;
    private UserProfileService profileService;
    private UserAccount currentUser;
    private UserProfile currentProfile;
    private boolean editMode = false;
    private boolean sidebarExpanded = true;
    private String originalName, originalPhone;

    public LansiaAccountController() {
        this.authService = new AuthService();
        this.profileService = new UserProfileService();
    }

    @FXML
    public void initialize() {
        // Check if user is logged in
        if (!SessionManager.isLoggedIn()) {
            showErrorMessage("No active session found. Please login.");
            navigateToView("/view/LoginView.fxml", "Login");
            return;
        }

        loadUserData();
        setupRoleBasedNavigation();
    }

    private void setupRoleBasedNavigation() {
        UserAccount currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        Role role = currentUser.getUserRole();

        // Hide all buttons initially
        hideAllNavigationButtons();

        // Show buttons based on role requirements
        switch (role) {
            case LANSIA:
                // LANSIA: Dashboard, Account
                showNavigationButton(dashboardButton, dashboardButtonCollapsed, "Dashboard", "D");
                showNavigationButton(accountButton, accountButtonCollapsed, "Account", "A");
                break;
        }

        // Highlight current page (Account)
        if (accountButton != null) {
            accountButton.setStyle("-fx-background-color: linear-gradient(to right, #ffffff 0%, #f8f9fa 100%); -fx-text-fill: #0C3C78; -fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 12; -fx-font-weight: bold;");
        }
        if (accountButtonCollapsed != null) {
            accountButtonCollapsed.setStyle("-fx-background-color: #bbcfec; -fx-text-fill: black; -fx-font-size: 16px; -fx-background-radius: 5;");
        }
    }

    private void hideAllNavigationButtons() {
        setButtonVisibility(dashboardButton, false);
        setButtonVisibility(emergencyAlertsButton, false);
        setButtonVisibility(accountButton, false);
        setButtonVisibility(dashboardButtonCollapsed, false);
        setButtonVisibility(emergencyAlertsButtonCollapsed, false);
        setButtonVisibility(accountButtonCollapsed, false);
    }

    private void setButtonVisibility(Button button, boolean visible) {
        if (button != null) {
            button.setVisible(visible);
        }
    }

    private void showNavigationButton(Button expandedBtn, Button collapsedBtn, String expandedText, String collapsedText) {
        if (expandedBtn != null) {
            expandedBtn.setVisible(true);
            expandedBtn.setText(expandedText);
        }
        if (collapsedBtn != null) {
            collapsedBtn.setVisible(true);
            collapsedBtn.setText(collapsedText);
        }
    }

    private void loadUserData() {
        currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            showErrorMessage("No user session found");
            return;
        }

        try {
            // Load complete profile data from database
            currentProfile = profileService.getUserProfile(currentUser.getUserID());
            if (currentProfile == null) {
                showErrorMessage("Failed to load user profile from database");
                return;
            }

            // Populate form fields with database data
            populateFormFields();
            showSuccessMessage("Profile loaded successfully from database");

        } catch (Exception e) {
            showErrorMessage("Error loading user data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateFormFields() {
        if (currentProfile == null) return;

        // Set name field
        if (nameField != null) {
            String username = currentProfile.getUsername();
            nameField.setText(username != null ? username : "");
            originalName = username != null ? username : "";
        }

        // Load phone from database contact_info
        if (phoneField != null) {
            String phone = currentProfile.getPhone();
            phoneField.setText(phone.isEmpty() ? "" : phone);
            originalPhone = phone.isEmpty() ? "" : phone;
        }

        if (passwordField != null) {
            passwordField.setText("••••••••••");
        }

        // Display role-specific information in status
        displayRoleSpecificInfo();
    }

    private void displayRoleSpecificInfo() {
        if (currentProfile == null) return;

        StringBuilder info = new StringBuilder();
        info.append("Role: ").append(getRoleDisplayName(currentProfile.getRole()));

        switch (currentProfile.getRole()) {
            case LANSIA:
                if (currentProfile.getBirthdate() != null) {
                    info.append(" | Born: ").append(currentProfile.getBirthdate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
                if (currentProfile.getDeviceModel() != null) {
                    info.append(" | Device: ").append(currentProfile.getDeviceModel());
                }
                if (currentProfile.getBatteryLevel() != null) {
                    info.append(" | Battery: ").append(String.format("%.1f%%", currentProfile.getBatteryLevel()));
                }
                break;
        }

        showInfoMessage(info.toString());
    }

    @FXML
    private void editName() {
        toggleEditMode();
        if (nameField != null) {
            nameField.setEditable(editMode);
            nameField.setStyle(editMode ? getEditableStyle() : getReadOnlyStyle());
            if (editMode) nameField.requestFocus();
        }
    }

    @FXML
    private void editPhone() {
        toggleEditMode();
        if (phoneField != null) {
            phoneField.setEditable(editMode);
            phoneField.setStyle(editMode ? getEditableStyle() : getReadOnlyStyle());
            if (editMode) phoneField.requestFocus();
        }
    }

    @FXML
    private void editPassword() {
        // Create a dialog for password change
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your current and new password");

        // Create the dialog content
        VBox content = new VBox(12);
        content.setStyle("-fx-padding: 15;");

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");
        currentPasswordField.setPrefWidth(280);
        currentPasswordField.setStyle("-fx-font-size: 13px;");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        newPasswordField.setPrefWidth(280);
        newPasswordField.setStyle("-fx-font-size: 13px;");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");
        confirmPasswordField.setPrefWidth(280);
        confirmPasswordField.setStyle("-fx-font-size: 13px;");

        Label currentLabel = new Label("Current Password:");
        currentLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        Label newLabel = new Label("New Password:");
        newLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        Label confirmLabel = new Label("Confirm Password:");
        confirmLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        content.getChildren().addAll(
                currentLabel, currentPasswordField,
                newLabel, newPasswordField,
                confirmLabel, confirmPasswordField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String currentPassword = currentPasswordField.getText();
                String newPassword = newPasswordField.getText();
                String confirmPassword = confirmPasswordField.getText();

                if (validatePasswordChange(currentPassword, newPassword, confirmPassword)) {
                    // Update password in database
                    boolean success = authService.updatePassword(currentUser.getUsername(), newPassword);
                    if (success) {
                        showSuccessMessage("Password updated successfully in database");
                    } else {
                        showErrorMessage("Failed to update password in database");
                    }
                }
            }
        });
    }

    private boolean validatePasswordChange(String currentPassword, String newPassword, String confirmPassword) {
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showErrorMessage("All password fields are required");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            showErrorMessage("New passwords do not match");
            return false;
        }

        if (newPassword.length() < 6) {
            showErrorMessage("New password must be at least 6 characters long");
            return false;
        }

        // Verify current password
        if (!authService.verifyPassword(currentUser.getUsername(), currentPassword)) {
            showErrorMessage("Current password is incorrect");
            return false;
        }

        return true;
    }

    @FXML
    private void saveChanges() {
        if (currentProfile == null) return;

        try {
            // Validate changes
            String newName = nameField.getText().trim();
            if (newName.isEmpty()) {
                showErrorMessage("Name cannot be empty");
                return;
            }

            String newPhone = phoneField.getText().trim();

            // Validate phone format if provided
            if (!newPhone.isEmpty() && !UserProfileService.isValidPhone(newPhone)) {
                showErrorMessage("Invalid phone number format. Use format: +62 812-3456-7890");
                return;
            }

            // Update profile data
            currentProfile.setUsername(newName);
            currentProfile.setLocationAndPhone(newPhone, "", "");

            // Save to database
            boolean success = profileService.updateUserProfile(currentProfile);

            if (success) {
                // Update session data
                currentUser.setUsername(newName);
                currentUser.setContactInfo(currentProfile.getContactInfo());

                // Update original values
                originalName = newName;
                originalPhone = newPhone;

                // Reset edit mode
                exitEditMode();

                showSuccessMessage("Account information updated successfully in database");
            } else {
                showErrorMessage("Failed to update account information in database");
            }

        } catch (Exception e) {
            showErrorMessage("Failed to save changes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelChanges() {
        // Restore original values
        if (nameField != null) nameField.setText(originalName);
        if (phoneField != null) phoneField.setText(originalPhone);

        exitEditMode();
        showInfoMessage("Changes cancelled");
    }

    private void toggleEditMode() {
        editMode = !editMode;
        updateEditModeUI();
    }

    private void exitEditMode() {
        editMode = false;
        updateEditModeUI();
    }

    private void updateEditModeUI() {
        // Update button visibility
        if (saveButton != null) saveButton.setVisible(editMode);
        if (cancelButton != null) cancelButton.setVisible(editMode);

        // Reset field styles and editability
        if (!editMode) {
            if (nameField != null) {
                nameField.setEditable(false);
                nameField.setStyle(getReadOnlyStyle());
            }
            if (phoneField != null) {
                phoneField.setEditable(false);
                phoneField.setStyle(getReadOnlyStyle());
            }
        }
    }

    private String getReadOnlyStyle() {
        return "-fx-background-color: linear-gradient(to bottom, #f8f9fa 0%, #e9ecef 100%); -fx-border-color: #dee2e6; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 12; -fx-font-size: 14px; -fx-border-width: 2;";
    }

    private String getEditableStyle() {
        return "-fx-background-color: white; -fx-border-color: #0C3C78; -fx-border-width: 2; -fx-background-radius: 12; -fx-padding: 12; -fx-font-size: 14px;";
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

    @FXML
    private void handleLansiaDashboard() {
        UserAccount currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            showErrorMessage("No user session found");
            return;
        }

        Role role = currentUser.getUserRole();
        if (role != Role.LANSIA) {
            showErrorMessage("Access denied to Dashboard");
            return;
        }

        navigateToView("/view/LansiaDashboard.fxml", "Dashboard");
    }


    private void showErrorMessage(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle("-fx-font-size: 14px; -fx-padding: 15; -fx-background-color: rgba(220,53,69,0.1); -fx-background-radius: 10; -fx-border-color: #dc3545; -fx-border-radius: 10; -fx-border-width: 1; -fx-text-fill: #721c24;");
        }
        System.err.println("ERROR: " + message);
    }

    private void showInfoMessage(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle("-fx-font-size: 14px; -fx-padding: 15; -fx-background-color: rgba(23,162,184,0.1); -fx-background-radius: 10; -fx-border-color: #17a2b8; -fx-border-radius: 10; -fx-border-width: 1; -fx-text-fill: #0c5460;");
        }
        System.out.println("INFO: " + message);
    }

    private void showSuccessMessage(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold; -fx-font-size: 13px;");
        }
        System.out.println("SUCCESS: " + message);
    }

}