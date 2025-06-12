package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.LansiaMedicalHistory;
import services.LansiaMedicalHistoryService;
import utils.SessionManager;
import models.UserAccount;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class HistoryController extends NavigationController {

    @FXML
    private TableView<LansiaMedicalHistory> historyTable;

    @FXML
    private TableColumn<LansiaMedicalHistory, String> columnCondition;

    @FXML
    private TableColumn<LansiaMedicalHistory, Timestamp> columnDiagnosisDate;

    @FXML
    private TableColumn<LansiaMedicalHistory, String> columnSeverity;

    @FXML
    private TableColumn<LansiaMedicalHistory, String> columnDescription;

    // Input fields
    @FXML
    private ComboBox<LansiaMedicalHistory.Severity> severityCombo;

    @FXML
    private TextField conditionInput;

    @FXML
    private TextArea descriptionInput;

    // Action buttons
    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button refreshButton;

    private ObservableList<LansiaMedicalHistory> observableHistoryList = FXCollections.observableArrayList();
    private LansiaMedicalHistoryService medicalHistoryService;
    private LansiaMedicalHistory selectedHistory = null;

    @FXML
    public void initialize() {
        // Call parent initialization first
        super.initialize();

        try {
            // Initialize service
            medicalHistoryService = new LansiaMedicalHistoryService();

            // Initialize ComboBox
            severityCombo.setItems(FXCollections.observableArrayList(LansiaMedicalHistory.Severity.values()));

            // Initialize Table Columns
            columnCondition.setCellValueFactory(new PropertyValueFactory<>("medicalCondition"));
            columnDiagnosisDate.setCellValueFactory(new PropertyValueFactory<>("diagnosisDate"));
            columnSeverity.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue().getSeverity() != null ?
                                    cellData.getValue().getSeverity().name() : "N/A")
            );
            columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

            // Initialize TableView
            historyTable.setItems(observableHistoryList);

            // Add selection listener
            historyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                selectedHistory = newSelection;
                updateButtonStates();
                if (newSelection != null) {
                    populateFormFields(newSelection);
                }
            });

            // Load user's medical history
            loadUserMedicalHistory();

            // Initialize button states
            updateButtonStates();

        } catch (Exception e) {
            System.err.println("Error initializing HistoryController: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void loadUserMedicalHistory() {
        try {
            UserAccount currentUser = SessionManager.getCurrentUser();
            if (currentUser != null) {
                // Filter histories for current user
                observableHistoryList.clear();
                medicalHistoryService.getAllHistories().stream()
                        .filter(history -> history.getUserId().equals(currentUser.getUserID()))
                        .forEach(observableHistoryList::add);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load medical history: " + e.getMessage());
        }
    }

    private void populateFormFields(LansiaMedicalHistory history) {
        if (history != null) {
            conditionInput.setText(history.getMedicalCondition() != null ? history.getMedicalCondition() : "");
            descriptionInput.setText(history.getDescription() != null ? history.getDescription() : "");
            severityCombo.setValue(history.getSeverity());
        }
    }

    private void updateButtonStates() {
        boolean hasSelection = selectedHistory != null;
        editButton.setDisable(!hasSelection);
        deleteButton.setDisable(!hasSelection);
    }

    @FXML
    private void handleAddHistory() {
        try {
            String condition = conditionInput.getText().trim();
            String description = descriptionInput.getText().trim();
            LansiaMedicalHistory.Severity severity = severityCombo.getValue();

            if (condition.isEmpty() || description.isEmpty() || severity == null) {
                showAlert("Validation Error", "Please fill all fields.");
                return;
            }

            UserAccount currentUser = SessionManager.getCurrentUser();
            if (currentUser == null) {
                showAlert("Error", "User session not found.");
                return;
            }

            LansiaMedicalHistory newEntry = new LansiaMedicalHistory(
                    currentUser.getUserID(),  // This assumes getUserId() exists in UserAccount
                    condition,
                    description,
                    severity
            );

            medicalHistoryService.addHistory(newEntry);
            observableHistoryList.add(newEntry);
            handleClearForm();

            showSuccessAlert("Success", "Medical history entry added successfully.");

        } catch (Exception e) {
            showAlert("Error", "Failed to add medical history: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditHistory() {
        if (selectedHistory == null) {
            showAlert("Selection Error", "Please select a history entry to edit.");
            return;
        }

        try {
            String condition = conditionInput.getText().trim();
            String description = descriptionInput.getText().trim();
            LansiaMedicalHistory.Severity severity = severityCombo.getValue();

            if (condition.isEmpty() || description.isEmpty() || severity == null) {
                showAlert("Validation Error", "Please fill all fields.");
                return;
            }

            // Update the selected history
            selectedHistory.setMedicalCondition(condition);
            selectedHistory.setDescription(description);
            selectedHistory.setSeverity(severity);
            selectedHistory.setDiagnosisDate(new Timestamp(System.currentTimeMillis()));

            medicalHistoryService.updateHistory(selectedHistory);

            // Refresh the table
            historyTable.refresh();
            handleClearForm();

            showSuccessAlert("Success", "Medical history entry updated successfully.");

        } catch (Exception e) {
            showAlert("Error", "Failed to update medical history: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteHistory() {
        if (selectedHistory == null) {
            showAlert("Selection Error", "Please select a history entry to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Medical History Entry");
        confirmAlert.setContentText("Are you sure you want to delete this medical history entry?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    medicalHistoryService.removeHistory(selectedHistory.getHistoryId());
                    observableHistoryList.remove(selectedHistory);
                    handleClearForm();
                    selectedHistory = null;
                    updateButtonStates();

                    showSuccessAlert("Success", "Medical history entry deleted successfully.");

                } catch (Exception e) {
                    showAlert("Error", "Failed to delete medical history: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    public void handleClearForm() {
        conditionInput.clear();
        descriptionInput.clear();
        severityCombo.getSelectionModel().clearSelection();
        historyTable.getSelectionModel().clearSelection();
        selectedHistory = null;
        updateButtonStates();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Add missing navigation method if not in parent class
    @FXML
    private void handleEmergencyAlerts() {
    }
}