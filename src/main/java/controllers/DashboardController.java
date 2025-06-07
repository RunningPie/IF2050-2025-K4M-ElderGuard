package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.UserAccount;
import model.Role;
import security.AccessControlManager;
import utils.SessionManager;

import java.io.IOException;

public class DashboardController extends NavigationController {

    @FXML
    private Label headerTitle;

    @FXML
    private Label sectionTitle;

    @FXML
    private Label roleDescription;

    @FXML
    private VBox dynamicContent;

    @FXML
    private VBox contentContainer;

    @Override
    public void initialize() {
        super.initialize(); // Initialize navigation
        setupDashboardContent();
    }

    private void setupDashboardContent() {
        UserAccount currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        Role role = currentUser.getUserRole();

        // Update header and section titles based on role
        updateTitles(role);

        // Load role-specific content
        loadRoleSpecificContent(role);
    }

    private void updateTitles(Role role) {
        String roleDisplayName = getRoleDisplayName(role);

        if (headerTitle != null) {
            headerTitle.setText("ElderGuard - " + roleDisplayName + " Dashboard");
        }

        if (sectionTitle != null) {
            sectionTitle.setText(roleDisplayName + " Overview");
        }

        if (roleDescription != null) {
            roleDescription.setText("Welcome to your " + roleDisplayName.toLowerCase() + " dashboard");
        }
    }

    private void loadRoleSpecificContent(Role role) {
        if (dynamicContent == null) {
            return;
        }

        dynamicContent.getChildren().clear();

        switch (role) {
            case FAMILY:
                loadFamilyContent();
                break;
            case LANSIA:
                loadLansiaContent();
                break;
            case MEDICAL_STAFF:
                loadMedicalStaffContent();
                break;
            case ADMIN:
                loadAdminContent();
                break;
        }
    }

    private void loadFamilyContent() {
        // Family Member Dashboard Content

        // Quick Actions Section
        VBox quickActions = createSection("Quick Actions", "Manage your family's health monitoring");
        GridPane actionsGrid = new GridPane();
        actionsGrid.setHgap(20);
        actionsGrid.setVgap(15);

        Button viewAlertsBtn = createActionButton("View Family Alerts", "#dc3545");
        viewAlertsBtn.setOnAction(e -> handleEmergencyAlerts());

        Button addFamilyBtn = createActionButton("Add Family Member", "#28a745");
        addFamilyBtn.setOnAction(e -> showInfoMessage("Add Family Member feature coming soon"));

        Button viewHealthBtn = createActionButton("Health Overview", "#17a2b8");
        viewHealthBtn.setOnAction(e -> showInfoMessage("Health Overview feature coming soon"));

        actionsGrid.add(viewAlertsBtn, 0, 0);
        actionsGrid.add(addFamilyBtn, 1, 0);
        actionsGrid.add(viewHealthBtn, 2, 0);

        quickActions.getChildren().add(actionsGrid);
        dynamicContent.getChildren().add(quickActions);

        // Family Status Section
        VBox familyStatus = createSection("Family Status", "Current status of family members");

        Label statusLabel1 = new Label("Family Members: 3 registered");
        statusLabel1.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");

        Label statusLabel2 = new Label("All members status: Normal");
        statusLabel2.setStyle("-fx-font-size: 14px; -fx-text-fill: #28a745;");

        Label statusLabel3 = new Label("Connected Devices: 3/3");
        statusLabel3.setStyle("-fx-font-size: 14px; -fx-text-fill: #17a2b8;");

        familyStatus.getChildren().addAll(statusLabel1, statusLabel2, statusLabel3);
        dynamicContent.getChildren().add(familyStatus);

        // Recent Activity Section
        VBox recentActivity = createSection("Recent Activity", "Latest family health updates");

        Label activity1 = new Label("✓ Daily health check completed for all members");
        activity1.setStyle("-fx-font-size: 12px; -fx-text-fill: #28a745;");

        Label activity2 = new Label("Weekly health report generated");
        activity2.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");

        Label activity3 = new Label("No emergency alerts in the last 7 days");
        activity3.setStyle("-fx-font-size: 12px; -fx-text-fill: #28a745;");

        recentActivity.getChildren().addAll(activity1, activity2, activity3);
        dynamicContent.getChildren().add(recentActivity);
    }

    private void loadLansiaContent() {
        // Elderly User Dashboard Content

        // Health Status Section
        VBox healthStatus = createSection("Health Status", "Your current health indicators");

        GridPane healthGrid = new GridPane();
        healthGrid.setHgap(40);
        healthGrid.setVgap(15);

        // Health metrics
        addHealthMetric(healthGrid, "Heart Rate:", "72 BPM", "#28a745", 0, 0);
        addHealthMetric(healthGrid, "Blood Pressure:", "120/80 mmHg", "#28a745", 0, 1);
        addHealthMetric(healthGrid, "Temperature:", "36.5°C", "#28a745", 1, 0);
        addHealthMetric(healthGrid, "Last Updated:", "2 minutes ago", "#6c757d", 1, 1);

        healthStatus.getChildren().add(healthGrid);
        dynamicContent.getChildren().add(healthStatus);

        // Device Status Section
        VBox deviceStatus = createSection("Wearable Device", "Your health monitoring device status");

        HBox deviceInfo = new HBox(20);
        deviceInfo.setStyle("-fx-alignment: center-left;");

        Label deviceLabel = new Label("Device: ElderWatch Pro");
        deviceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #495057;");

        Label batteryLabel = new Label("Battery: 85%");
        batteryLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #28a745;");

        Label statusLabel = new Label("Status: Connected");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #28a745;");

        deviceInfo.getChildren().addAll(deviceLabel, batteryLabel, statusLabel);
        deviceStatus.getChildren().add(deviceInfo);
        dynamicContent.getChildren().add(deviceStatus);

        // Emergency Button Section
        VBox emergencySection = createSection("Emergency", "Quick access to emergency features");

        Button emergencyBtn = createActionButton("🚨 Emergency Alert", "#dc3545");
        emergencyBtn.setPrefWidth(200);
        emergencyBtn.setStyle(emergencyBtn.getStyle() + "; -fx-font-size: 16px; -fx-font-weight: bold;");
        emergencyBtn.setOnAction(e -> showInfoMessage("Emergency alert feature coming soon"));

        emergencySection.getChildren().add(emergencyBtn);
        dynamicContent.getChildren().add(emergencySection);

        // Recent Alerts Section
        VBox recentAlerts = createSection("Recent Alerts", "Your recent health notifications");

        Label noAlertsLabel = new Label("No recent alerts - All systems normal");
        noAlertsLabel.setStyle("-fx-text-fill: #28a745; -fx-font-style: italic; -fx-font-size: 14px;");

        recentAlerts.getChildren().add(noAlertsLabel);
        dynamicContent.getChildren().add(recentAlerts);
    }

    private void loadMedicalStaffContent() {
        // Medical Staff Dashboard Content

        // Patient Overview Section
        VBox patientOverview = createSection("Patient Overview", "Current patient statistics");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(40);
        statsGrid.setVgap(15);

        addStatsMetric(statsGrid, "Active Patients:", "245", "#0C3C78", 0, 0);
        addStatsMetric(statsGrid, "Critical Cases:", "3", "#dc3545", 0, 1);
        addStatsMetric(statsGrid, "Pending Reviews:", "12", "#ffc107", 1, 0);
        addStatsMetric(statsGrid, "Today's Appointments:", "8", "#28a745", 1, 1);

        patientOverview.getChildren().add(statsGrid);
        dynamicContent.getChildren().add(patientOverview);

        // Critical Patients Section
        VBox criticalPatients = createSection("Critical Patients", "Patients requiring immediate attention");

        VBox criticalList = new VBox(10);

        // Critical patient 1
        HBox patient1 = createCriticalPatientCard(
                "Maria Santos", "78", "Hypertension",
                "BP: 180/110", "5 min ago", "#dc3545"
        );

        // Critical patient 2
        HBox patient2 = createCriticalPatientCard(
                "John Anderson", "82", "Diabetes",
                "Glucose: 280 mg/dL", "12 min ago", "#ed8936"
        );

        criticalList.getChildren().addAll(patient1, patient2);
        criticalPatients.getChildren().add(criticalList);
        dynamicContent.getChildren().add(criticalPatients);

        // Quick Actions Section
        VBox quickActions = createSection("Quick Actions", "Medical staff tools");

        HBox actionsBox = new HBox(15);

        Button viewAllAlertsBtn = createActionButton("View All Alerts", "#dc3545");
        viewAllAlertsBtn.setOnAction(e -> handleEmergencyAlerts());

        Button patientRecordsBtn = createActionButton("Patient Records", "#17a2b8");
        patientRecordsBtn.setOnAction(e -> showInfoMessage("Patient Records feature coming soon"));

        Button reportsBtn = createActionButton("Generate Report", "#6c757d");
        reportsBtn.setOnAction(e -> showInfoMessage("Reports feature coming soon"));

        actionsBox.getChildren().addAll(viewAllAlertsBtn, patientRecordsBtn, reportsBtn);
        quickActions.getChildren().add(actionsBox);
        dynamicContent.getChildren().add(quickActions);
    }

    private void loadAdminContent() {
        // Admin Dashboard Content

        // System Overview Section
        VBox systemOverview = createSection("System Overview", "ElderGuard system statistics");

        GridPane systemGrid = new GridPane();
        systemGrid.setHgap(40);
        systemGrid.setVgap(15);

        addStatsMetric(systemGrid, "Total Users:", "1,247", "#0C3C78", 0, 0);
        addStatsMetric(systemGrid, "Active Devices:", "892", "#28a745", 0, 1);
        addStatsMetric(systemGrid, "System Alerts:", "3", "#ffc107", 1, 0);
        addStatsMetric(systemGrid, "Uptime:", "99.9%", "#28a745", 1, 1);

        systemOverview.getChildren().add(systemGrid);
        dynamicContent.getChildren().add(systemOverview);

        // User Distribution Section
        VBox userDistribution = createSection("User Distribution", "Breakdown of user roles");

        VBox distributionList = new VBox(8);

        Label lansiaCount = new Label("Lansia Users: 456 (36.6%)");
        lansiaCount.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");

        Label familyCount = new Label("Family Members: 623 (49.9%)");
        familyCount.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");

        Label medicalCount = new Label("Medical Staff: 156 (12.5%)");
        medicalCount.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");

        Label adminCount = new Label("Administrators: 12 (1.0%)");
        adminCount.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");

        distributionList.getChildren().addAll(lansiaCount, familyCount, medicalCount, adminCount);
        userDistribution.getChildren().add(distributionList);
        dynamicContent.getChildren().add(userDistribution);

        // Admin Actions Section
        VBox adminActions = createSection("Administrative Actions", "System management tools");

        HBox actionsBox = new HBox(15);

        Button userMgmtBtn = createActionButton("User Management", "#0C3C78");
        userMgmtBtn.setOnAction(e -> showInfoMessage("User Management feature coming soon"));

        Button systemSettingsBtn = createActionButton("System Settings", "#6c757d");
        systemSettingsBtn.setOnAction(e -> showInfoMessage("System Settings feature coming soon"));

        Button logsBtn = createActionButton("System Logs", "#17a2b8");
        logsBtn.setOnAction(e -> showInfoMessage("System Logs feature coming soon"));

        actionsBox.getChildren().addAll(userMgmtBtn, systemSettingsBtn, logsBtn);
        adminActions.getChildren().add(actionsBox);
        dynamicContent.getChildren().add(adminActions);
    }

    // Helper methods for creating UI components

    private VBox createSection(String title, String description) {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-color: #dee2e6; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #e9ecef;");

        section.getChildren().addAll(titleLabel, descLabel, separator);

        return section;
    }

    private Button createActionButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 10 20; -fx-font-weight: bold; -fx-cursor: hand;");
        button.setPrefWidth(150);
        return button;
    }

    private void addHealthMetric(GridPane grid, String label, String value, String color, int row, int col) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #495057;");

        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-font-size: 14px;");

        grid.add(labelNode, col * 2, row);
        grid.add(valueNode, col * 2 + 1, row);
    }

    private void addStatsMetric(GridPane grid, String label, String value, String color, int row, int col) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #495057;");

        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-font-size: 16px;");

        grid.add(labelNode, col * 2, row);
        grid.add(valueNode, col * 2 + 1, row);
    }

    private HBox createCriticalPatientCard(String name, String age, String condition, String metric, String time, String color) {
        HBox card = new HBox(20);
        card.setStyle("-fx-padding: 15; -fx-background-color: #fff5f5; -fx-border-color: " + color + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-alignment: center-left;");

        VBox patientInfo = new VBox(5);
        Label nameLabel = new Label("Patient: " + name);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");

        Label infoLabel = new Label("Age: " + age + " • Condition: " + condition);
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");

        patientInfo.getChildren().addAll(nameLabel, infoLabel);

        // Spacer
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        VBox metricInfo = new VBox(5);
        metricInfo.setStyle("-fx-alignment: center-right;");

        Label metricLabel = new Label(metric);
        metricLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");

        Label timeLabel = new Label("Last Reading: " + time);
        timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6c757d;");

        metricInfo.getChildren().addAll(metricLabel, timeLabel);

        Button reviewBtn = new Button("Review");
        reviewBtn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 15;");
        reviewBtn.setOnAction(e -> showInfoMessage("Patient review feature coming soon"));

        card.getChildren().addAll(patientInfo, spacer, metricInfo, reviewBtn);

        return card;
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

    private void showInfoMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void handleEmergencyAlerts() {
        // Example: Navigate to the Emergency Alerts page
        // Or display alerts in a dialog
        System.out.println("Emergency alerts handler called!");
        // TODO: implement navigation or alert display
    }


}