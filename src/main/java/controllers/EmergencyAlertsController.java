package controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import model.EmergencyAlert;
import model.UserAccount;
import model.Role;
import utils.AlertEventManager;
import utils.SessionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.StackPane;


public class EmergencyAlertsController extends NavigationController {

    @FXML
    private Label headerTitle;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private Button refreshButton;

    @FXML
    private Button createAlertButton;

    @FXML
    private Label criticalCountLabel;

    @FXML
    private Label highCountLabel;

    @FXML
    private Label mediumCountLabel;

    @FXML
    private Label lowCountLabel;

    @FXML
    private Label totalCountLabel;

    @FXML
    private TableView<EmergencyAlert> alertsTable;

    @FXML
    private TableColumn<EmergencyAlert, String> priorityColumn;

    @FXML
    private TableColumn<EmergencyAlert, String> alertTypeColumn;

    @FXML
    private TableColumn<EmergencyAlert, String> patientColumn;

    @FXML
    private TableColumn<EmergencyAlert, String> locationColumn;

    @FXML
    private TableColumn<EmergencyAlert, String> timeColumn;

    @FXML
    private TableColumn<EmergencyAlert, String> statusColumn;

    @FXML
    private TableColumn<EmergencyAlert, String> actionsColumn;

    @FXML
    private VBox roleInfoSection;

    @FXML
    private Label roleInfoTitle;

    @FXML
    private Label roleInfoContent;

    private ObservableList<EmergencyAlert> allAlerts = FXCollections.observableArrayList();
    private ObservableList<EmergencyAlert> filteredAlerts = FXCollections.observableArrayList();

    @Override
    public void initialize() {
        super.initialize(); // Initialize navigation
        setupEmergencyAlertsView();
        setupTableColumns();
        setupRoleBasedAccess();
        loadSampleData();
        refreshAlerts();

        AlertEventManager.getInstance().addListener(this::handleIncomingAlert);
    }

    private void handleIncomingAlert(EmergencyAlert alert) {
        Platform.runLater(() -> {
            System.out.println("Incoming alert: " + alert);

            // Add the incoming alert to the list
            allAlerts.add(alert);

            // Refresh the alerts list and UI
            refreshAlerts();
        });
    }

    private void setupEmergencyAlertsView() {
        UserAccount currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        Role role = currentUser.getUserRole();

        // Update header title based on role
        if (headerTitle != null) {
            if (role == Role.MEDICAL_STAFF || role == Role.ADMIN) {
                headerTitle.setText("ElderGuard - All Emergency Alerts");
            } else {
                headerTitle.setText("ElderGuard - Family Emergency Alerts");
            }
        }

        // Setup filter options
        if (filterComboBox != null) {
            filterComboBox.getItems().addAll("All", "Critical", "High", "Medium", "Low");
            filterComboBox.setValue("All");
            filterComboBox.setOnAction(e -> applyFilter());
        }
    }

    private void setupTableColumns() {
        // Priority column with color coding
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityColumn.setCellFactory(column -> new TableCell<EmergencyAlert, String>() {
            @Override
            protected void updateItem(String priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(priority);
                    switch (priority.toLowerCase()) {
                        case "critical":
                            setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold;");
                            break;
                        case "high":
                            setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-font-weight: bold;");
                            break;
                        case "medium":
                            setStyle("-fx-background-color: #ffeaa7; -fx-text-fill: #8e4a00; -fx-font-weight: bold;");
                            break;
                        case "low":
                            setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        // Other columns
        alertTypeColumn.setCellValueFactory(new PropertyValueFactory<>("alertType"));
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        // Time column with formatted date
        timeColumn.setCellValueFactory(cellData -> {
            LocalDateTime time = cellData.getValue().getCreatedAt();
            String formattedTime = time.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
            return new SimpleStringProperty(formattedTime);
        });

        // Status column with color coding
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<EmergencyAlert, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status.toLowerCase()) {
                        case "active":
                            setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                            break;
                        case "resolved":
                            setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                            break;
                        case "in progress":
                            setStyle("-fx-text-fill: #ffc107; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("-fx-text-fill: #6c757d;");
                    }
                }
            }
        });

        // Actions column with buttons
        actionsColumn.setCellFactory(column -> new TableCell<EmergencyAlert, String>() {
            private final Button actionButton = new Button();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    EmergencyAlert alert = getTableView().getItems().get(getIndex());
                    UserAccount currentUser = SessionManager.getCurrentUser();

                    if (currentUser != null &&
                            (currentUser.getUserRole() == Role.MEDICAL_STAFF ||
                                    currentUser.getUserRole() == Role.ADMIN)) {

                        if ("Active".equals(alert.getStatus())) {
                            actionButton.setText("Respond");
                            actionButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 3; -fx-padding: 5 10;");
                            actionButton.setOnAction(e -> respondToAlert(alert));
                        } else {
                            actionButton.setText("View");
                            actionButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-background-radius: 3; -fx-padding: 5 10;");
                            actionButton.setOnAction(e -> viewAlert(alert));
                        }
                    } else {
                        actionButton.setText("View");
                        actionButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 3; -fx-padding: 5 10;");
                        actionButton.setOnAction(e -> viewAlert(alert));
                    }

                    setGraphic(actionButton);
                }
            }
        });

        alertsTable.setItems(filteredAlerts);
    }

    private void setupRoleBasedAccess() {
        UserAccount currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        Role role = currentUser.getUserRole();

        // Configure create alert button based on role
        if (createAlertButton != null) {
            boolean canCreateAlert = role == Role.FAMILY || role == Role.LANSIA ||
                    role == Role.MEDICAL_STAFF || role == Role.ADMIN;
            createAlertButton.setVisible(canCreateAlert);
        }

        // Setup role-specific information
        if (roleInfoTitle != null && roleInfoContent != null) {
            switch (role) {
                case FAMILY:
                    roleInfoTitle.setText("Family Emergency Alerts");
                    roleInfoContent.setText("You can view emergency alerts for your family members and create new alerts when needed. " +
                            "Only alerts related to your family group are displayed here.");
                    break;
                case MEDICAL_STAFF:
                    roleInfoTitle.setText("Medical Staff Information");
                    roleInfoContent.setText("As medical staff, you can view all emergency alerts in the system and respond to them. " +
                            "Use the 'Respond' button to take action on active alerts.");
                    break;
                case LANSIA:
                    roleInfoTitle.setText("Emergency Alert System");
                    roleInfoContent.setText("You can create emergency alerts if you need immediate assistance. " +
                            "Your family members and medical staff will be notified automatically.");
                    break;
                case ADMIN:
                    roleInfoTitle.setText("Administrator View");
                    roleInfoContent.setText("You have full access to all emergency alerts in the system. " +
                            "You can view, respond to, and manage all alerts across the platform.");
                    break;
            }
        }
    }

    private void loadSampleData() {
        // Sample emergency alerts data
//        allAlerts.clear();

        allAlerts.add(new EmergencyAlert("EA001", "Critical", "Maria Santos",
                "Heart Attack", "PT001", "Home - Living Room",
                LocalDateTime.now().minusMinutes(5), "Active"));

        allAlerts.add(new EmergencyAlert("EA002", "High", "John Anderson",
                "Fall Detection", "PT002", "Bathroom",
                LocalDateTime.now().minusMinutes(15), "In Progress"));

        allAlerts.add(new EmergencyAlert("EA003", "Medium", "Sarah Wilson",
                "Irregular Heartbeat", "PT003", "Bedroom",
                LocalDateTime.now().minusMinutes(30), "Active"));

        allAlerts.add(new EmergencyAlert("EA004", "Low", "Robert Chen",
                "Medication Reminder", "PT004", "Kitchen",
                LocalDateTime.now().minusHours(1), "Resolved"));

        allAlerts.add(new EmergencyAlert("EA005", "Critical", "Emma Davis",
                "Severe Hypoglycemia", "PT005", "Garden",
                LocalDateTime.now().minusHours(2), "Resolved"));

//        UserAccount currentUser = SessionManager.getCurrentUser();
//        if (currentUser != null && currentUser.getUserRole() == Role.FAMILY) {
//            // For family users, show only family-related alerts
//            // In a real implementation, this would filter based on family group ID
//            List<EmergencyAlert> familyAlerts = new ArrayList<>();
//            familyAlerts.add(allAlerts.get(0)); // Maria Santos
//            familyAlerts.add(allAlerts.get(2)); // Sarah Wilson
//            allAlerts.setAll(familyAlerts);
//        }
    }

    @FXML
    private void refreshAlerts() {
//        loadSampleData(); // In real implementation, this would fetch from database
        applyFilter();
        updateStatistics();
    }

    @FXML
    private void createAlert() {
        // Show create alert dialog
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Create Emergency Alert");
        dialog.setHeaderText("Emergency Alert Creation");
        dialog.setContentText("Emergency alert creation feature will be implemented here. " +
                "This would include forms for alert type, priority, location, and description.");
        dialog.showAndWait();
    }

    private void applyFilter() {
        String filter = filterComboBox.getValue();
        filteredAlerts.clear();

        if ("All".equals(filter)) {
            filteredAlerts.addAll(allAlerts);
        } else {
            allAlerts.stream()
                    .filter(alert -> alert.getPriority().equalsIgnoreCase(filter))
                    .forEach(filteredAlerts::add);
        }

        updateStatistics();
    }

    private void updateStatistics() {
        int critical = 0, high = 0, medium = 0, low = 0;

        for (EmergencyAlert alert : allAlerts) {
            switch (alert.getPriority().toLowerCase()) {
                case "critical": critical++; break;
                case "high": high++; break;
                case "medium": medium++; break;
                case "low": low++; break;
            }
        }

        if (criticalCountLabel != null) criticalCountLabel.setText(String.valueOf(critical));
        if (highCountLabel != null) highCountLabel.setText(String.valueOf(high));
        if (mediumCountLabel != null) mediumCountLabel.setText(String.valueOf(medium));
        if (lowCountLabel != null) lowCountLabel.setText(String.valueOf(low));
        if (totalCountLabel != null) totalCountLabel.setText(String.valueOf(allAlerts.size()));
    }

    private void respondToAlert(EmergencyAlert alert) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Respond to Emergency Alert");
        dialog.setHeaderText("Emergency Alert Response");
        dialog.setContentText("Are you sure you want to respond to this emergency alert for " +
                alert.getPatientName() + "?\n\n" +
                "Alert Type: " + alert.getAlertType() + "\n" +
                "Priority: " + alert.getPriority() + "\n" +
                "Location: " + alert.getLocation());

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Update alert status
                alert.setStatus("In Progress");
                alert.setAssignedTo(SessionManager.getCurrentUser().getUsername());

                // Refresh the table
                alertsTable.refresh();
                updateStatistics();

                showSuccessMessage("You have successfully responded to the emergency alert.");
            }
        });
    }

    private void viewAlert(EmergencyAlert alert) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Emergency Alert Details");
        dialog.setHeaderText("Alert ID: " + alert.getAlertId());

        String content = String.format(
                "Patient: %s\n" +
                        "Alert Type: %s\n" +
                        "Priority: %s\n" +
                        "Location: %s\n" +
                        "Status: %s\n" +
                        "Created: %s\n" +
                        "Assigned To: %s",
                alert.getPatientName(),
                alert.getAlertType(),
                alert.getPriority(),
                alert.getLocation(),
                alert.getStatus(),
                alert.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                alert.getAssignedTo() != null ? alert.getAssignedTo() : "Unassigned"
        );

        dialog.setContentText(content);
        dialog.showAndWait();
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML private StackPane sidebarContainer;
    @FXML private Button sidebarToggleButton;
    private boolean sidebarVisible = true;

    @FXML
    private void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        sidebarContainer.setVisible(sidebarVisible);
        sidebarContainer.setManaged(sidebarVisible);
        sidebarToggleButton.setText(sidebarVisible ? "☰" : "→"); // Optional icon swap
    }

}