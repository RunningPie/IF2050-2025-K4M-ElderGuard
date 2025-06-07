package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.UserAccount;
import model.Role;
import security.AccessControlManager;
import utils.SessionManager;

import java.io.IOException;

public class NavigationController {

    @FXML
    private VBox navigationMenu;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button emergencyAlertsButton;

    @FXML
    private Button accountButton;

    @FXML
    private Button logoutButton;

    @FXML
    public void initialize() {
        setupUserInterface();
        updateNavigationMenu();
    }

    private void setupUserInterface() {
        UserAccount currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            if (welcomeLabel != null) {
                welcomeLabel.setText("Welcome, " + currentUser.getUsername());
            }
        }
    }

    private void updateNavigationMenu() {
        UserAccount currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        Role role = currentUser.getUserRole();

        // Hide all buttons initially
        if (dashboardButton != null) dashboardButton.setVisible(false);
        if (emergencyAlertsButton != null) emergencyAlertsButton.setVisible(false);
        if (accountButton != null) accountButton.setVisible(false);

        // Show buttons based on role requirements
        switch (role) {
            case FAMILY:
                // FAMILY: Dashboard, Emergency Alert, Account
                if (dashboardButton != null) {
                    dashboardButton.setVisible(true);
                    dashboardButton.setText("Dashboard");
                }
                if (emergencyAlertsButton != null) {
                    emergencyAlertsButton.setVisible(true);
                    emergencyAlertsButton.setText("Emergency Alerts");
                }
                if (accountButton != null) {
                    accountButton.setVisible(true);
                    accountButton.setText("Account");
                }
                break;

            case LANSIA:
                // LANSIA: Dashboard, Account
                if (dashboardButton != null) {
                    dashboardButton.setVisible(true);
                    dashboardButton.setText("Dashboard");
                }
                if (accountButton != null) {
                    accountButton.setVisible(true);
                    accountButton.setText("Account");
                }
                break;

            case MEDICAL_STAFF:
                // MEDICAL_STAFF: Emergency Alert, Account
                if (emergencyAlertsButton != null) {
                    emergencyAlertsButton.setVisible(true);
                    emergencyAlertsButton.setText("Emergency Alerts");
                }
                if (accountButton != null) {
                    accountButton.setVisible(true);
                    accountButton.setText("Account");
                }
                break;

            case ADMIN:
                // ADMIN: All options available
                if (dashboardButton != null) {
                    dashboardButton.setVisible(true);
                    dashboardButton.setText("Dashboard");
                }
                if (emergencyAlertsButton != null) {
                    emergencyAlertsButton.setVisible(true);
                    emergencyAlertsButton.setText("Emergency Alerts");
                }
                if (accountButton != null) {
                    accountButton.setVisible(true);
                    accountButton.setText("Account");
                }
                break;
        }
    }

    @FXML
    private void handleDashboard() {
        UserAccount currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            showAccessDeniedAlert("Dashboard");
            return;
        }

        Role role = currentUser.getUserRole();
        if (role != Role.FAMILY && role != Role.LANSIA && role != Role.ADMIN) {
            showAccessDeniedAlert("Dashboard");
            return;
        }

        String dashboardPath = AccessControlManager.getDashboardPath(role);
        navigateToView(dashboardPath, "Dashboard");
    }

    @FXML
    private void handleEmergencyAlerts() {
        UserAccount currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            showAccessDeniedAlert("Emergency Alerts");
            return;
        }

        Role role = currentUser.getUserRole();
        if (role != Role.FAMILY && role != Role.MEDICAL_STAFF && role != Role.ADMIN) {
            showAccessDeniedAlert("Emergency Alerts");
            return;
        }

        String viewPath;
        String title;

        if (role == Role.MEDICAL_STAFF || role == Role.ADMIN) {
            viewPath = "/view/EmergencyAlertsView.fxml";
            title = "All Emergency Alerts";
        } else {
            viewPath = "/view/EmergencyAlertsView.fxml";
            title = "Family Emergency Alerts";
        }

        navigateToView(viewPath, title);
    }

    @FXML
    private void handleAccount() {
        UserAccount currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            showAccessDeniedAlert("Account");
            return;
        }

        // All roles can access account management
        navigateToView("/view/AccountView.fxml", "Account Management");
    }

    @FXML
    private void handleLogout() {
        SessionManager.clearSession();
        navigateToView("/view/LoginView.fxml", "Login");
    }

    protected void navigateToView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = getCurrentStage();
            if (stage != null) {
                stage.setScene(new Scene(root));
                stage.setTitle("ElderGuard - " + title);
                stage.centerOnScreen();
            }

        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Failed to load " + title + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Stage getCurrentStage() {
        if (logoutButton != null && logoutButton.getScene() != null) {
            return (Stage) logoutButton.getScene().getWindow();
        }
        if (dashboardButton != null && dashboardButton.getScene() != null) {
            return (Stage) dashboardButton.getScene().getWindow();
        }
        if (accountButton != null && accountButton.getScene() != null) {
            return (Stage) accountButton.getScene().getWindow();
        }
        return null;
    }

    private void showAccessDeniedAlert(String feature) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Access Denied");
        alert.setHeaderText("Insufficient Permissions");
        alert.setContentText("You don't have permission to access " + feature + ".");
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }
}