package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Sensor;
import models.WearableDevice;
import services.MonitoringService;

import java.util.List;

public class MonitoringController {

    @FXML private Label heartRateLabel;
    @FXML private Label bloodPressureLabel;
    @FXML private Label temperatureLabel;
    @FXML private Label lastUpdatedLabel;
    @FXML private Label deviceModelLabel;
    @FXML private Label batteryLabel;
    @FXML private Label deviceStatusLabel;
    @FXML private TableView<Sensor> sensorTable;
    @FXML private TableColumn<Sensor, String> typeColumn;
    @FXML private TableColumn<Sensor, Float> valueColumn;
    @FXML private Label statusLabel;
    @FXML private ProgressBar batteryBar;

    private MonitoringService monitoringService;

    @FXML
    public void initialize() {
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("sensorReadings"));

        monitoringService = new MonitoringService();
        monitoringService.setUpdateListener(device -> {
            Platform.runLater(() -> updateUI(device));  // <- runLater here
        });
        monitoringService.startMonitoring();
    }

    private void updateUI(WearableDevice device) {
        List<Sensor> updatedSensors = device.getSensors();
        sensorTable.getItems().setAll(updatedSensors);

        for (Sensor s : updatedSensors) {
            switch (s.getType().toLowerCase()) {
                case "heart rate" -> heartRateLabel.setText(s.getFormattedReading());
                case "blood pressure" -> bloodPressureLabel.setText(s.getFormattedReading());
                case "body temp" -> temperatureLabel.setText(s.getFormattedReading());
            }
        }

        batteryBar.setProgress(device.getBatteryLevel() / 100.0);
        batteryLabel.setText(String.format("%.0f%%", device.getBatteryLevel()));
        deviceModelLabel.setText(device.getModel());
        lastUpdatedLabel.setText("Updated: " + java.time.LocalTime.now().withNano(0));
        statusLabel.setText("Last update: " + java.time.LocalTime.now().withNano(0));

        if (device.isLowBattery()) {
            batteryLabel.setStyle("-fx-text-fill: red;");
            deviceStatusLabel.setText("Low Battery");
            deviceStatusLabel.setStyle("-fx-text-fill: red;");
        } else {
            batteryLabel.setStyle("-fx-text-fill: green;");
            deviceStatusLabel.setText("Connected");
            deviceStatusLabel.setStyle("-fx-text-fill: green;");
        }
    }

}
