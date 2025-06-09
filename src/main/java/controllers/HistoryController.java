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

    @FXML
    private TableColumn<LansiaMedicalHistory, String> conditionColumn;

    @FXML
    private TableColumn<LansiaMedicalHistory, Timestamp> dateColumn;

    @FXML
    private TableColumn<LansiaMedicalHistory, String> severityColumn;

    @FXML
    private TableColumn<LansiaMedicalHistory, String> descriptionColumn;

    @FXML
    private ComboBox<LansiaMedicalHistory.Severity> severityCombo;

    @FXML
    private TextField conditionInput;

    @FXML
    private TextArea descriptionInput;

    private ObservableList<LansiaMedicalHistory> observableHistoryList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Inisialisasi ComboBox Severity
        severityCombo.setItems(FXCollections.observableArrayList(LansiaMedicalHistory.Severity.values()));

        // Inisialisasi Table Columns
        conditionColumn.setCellValueFactory(new PropertyValueFactory<>("medicalCondition"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosisDate"));
        severityColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSeverity().name())
        );
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Inisialisasi TableView
        historyTable.setItems(observableHistoryList);

        // Optionally, load existing data here (from DB)
    }

    @FXML
    private void addHistoryEntry() {
        String condition = conditionInput.getText();
        String description = descriptionInput.getText();
        LansiaMedicalHistory.Severity severity = severityCombo.getValue();

        if (condition.isEmpty() || description.isEmpty() || severity == null) {
            showAlert("Validation Error", "Please fill all fields.");
            return;
        }

        // Buat entry baru
        LansiaMedicalHistory newEntry = new LansiaMedicalHistory(
                UUID.randomUUID(),         // historyId → akan di DB, sementara pakai random
                UUID.randomUUID(),         // userId → sementara pakai random, sesuaikan kalau ada user login
                condition,
                new Timestamp(System.currentTimeMillis()),
                description,
                severity
        );

        // Masukkan ke list
        observableHistoryList.add(newEntry);

        // Reset input fields
        clearInputFields();
    }

    private void clearInputFields() {
        conditionInput.clear();
        descriptionInput.clear();
        severityCombo.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
