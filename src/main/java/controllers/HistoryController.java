package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.LansiaMedicalHistory;
import java.sql.Timestamp;
import java.util.UUID;

public class HistoryController {

    @FXML
    private TableView<LansiaMedicalHistory> historyTable;

    // Sesuaikan nama dengan fx:id di FXML
    @FXML
    private TableColumn<LansiaMedicalHistory, String> columnCondition;

    @FXML
    private TableColumn<LansiaMedicalHistory, Timestamp> columnDiagnosisDate;

    @FXML
    private TableColumn<LansiaMedicalHistory, String> columnSeverity;

    @FXML
    private TableColumn<LansiaMedicalHistory, String> columnDescription;

    // Comment field yang tidak ada di FXML
    // @FXML
    // private ComboBox<LansiaMedicalHistory.Severity> severityCombo;

    // @FXML
    // private TextField conditionInput;

    // @FXML
    // private TextArea descriptionInput;

    private ObservableList<LansiaMedicalHistory> observableHistoryList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Comment inisialisasi ComboBox yang tidak ada
        // severityCombo.setItems(FXCollections.observableArrayList(LansiaMedicalHistory.Severity.values()));

        // Inisialisasi Table Columns dengan nama yang benar
        columnCondition.setCellValueFactory(new PropertyValueFactory<>("medicalCondition"));
        columnDiagnosisDate.setCellValueFactory(new PropertyValueFactory<>("diagnosisDate"));
        columnSeverity.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSeverity().name())
        );
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Inisialisasi TableView
        historyTable.setItems(observableHistoryList);

        // Load sample data untuk testing
        loadSampleData();
    }

    // Method untuk testing - tambah sample data
    private void loadSampleData() {
        LansiaMedicalHistory sample1 = new LansiaMedicalHistory(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Diabetes",
                new Timestamp(System.currentTimeMillis()),
                "Type 2 diabetes mellitus",
                LansiaMedicalHistory.Severity.MEDIUM
        );

        LansiaMedicalHistory sample2 = new LansiaMedicalHistory(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Hypertension",
                new Timestamp(System.currentTimeMillis() - 86400000), // 1 day ago
                "High blood pressure condition",
                LansiaMedicalHistory.Severity.HIGH
        );

        observableHistoryList.addAll(sample1, sample2);
    }

    // Comment method yang menggunakan field yang tidak ada
    /*
    @FXML
    private void addHistoryEntry() {
        String condition = conditionInput.getText();
        String description = descriptionInput.getText();
        LansiaMedicalHistory.Severity severity = severityCombo.getValue();

        if (condition.isEmpty() || description.isEmpty() || severity == null) {
            showAlert("Validation Error", "Please fill all fields.");
            return;
        }

        LansiaMedicalHistory newEntry = new LansiaMedicalHistory(
                UUID.randomUUID(),
                UUID.randomUUID(),
                condition,
                new Timestamp(System.currentTimeMillis()),
                description,
                severity
        );

        observableHistoryList.add(newEntry);
        clearInputFields();
    }

    private void clearInputFields() {
        conditionInput.clear();
        descriptionInput.clear();
        severityCombo.getSelectionModel().clearSelection();
    }
    */

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}