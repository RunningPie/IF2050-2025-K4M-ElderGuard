package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.*;
import utils.AlertEventManager;
import utils.DataSimulator;
import utils.SessionManager;

import java.io.IOException;
import java.util.List;

public class MonitoringController extends NavigationController{

    @FXML private Label heartRateLabel;
    @FXML private Label bloodPressureLabel;
    @FXML private Label temperatureLabel;
    @FXML private Label lastUpdatedLabel;

    @FXML private Label deviceModelLabel;
    @FXML private Label batteryLabel;
    @FXML private Label deviceStatusLabel;

    @FXML
    private TableView<Sensor> sensorTable;

    @FXML
    private TableColumn<Sensor, String> typeColumn;

    @FXML
    private TableColumn<Sensor, Float> valueColumn;

    @FXML
    private Label statusLabel;

    @FXML
    private ProgressBar batteryBar;

    private WearableDevice device;

    @FXML
    public void initialize() {
        device = new WearableDevice("WD-001", "ElderBand Alpha", 100.0f);

        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("sensorReadings"));

        refreshSensorData();

        // Simulasi update setiap 5 detik
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> refreshSensorData()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void refreshSensorData() {
        List<Sensor> updatedSensors = DataSimulator.generateDummySensorData();
        device.setSensors(updatedSensors);
        sensorTable.getItems().setAll(updatedSensors);

        // Update info tambahan
        batteryBar.setProgress(device.getBatteryLevel() / 100.0);
        statusLabel.setText("Last update: " + java.time.LocalTime.now().withNano(0));

        // Update label-label di dashboard
        for (Sensor s : updatedSensors) {
            System.out.println("Sensor: " + s.getType() + ", " + s.getSensorReadings());
            switch (s.getType().toLowerCase()) {
                case "heart rate" -> heartRateLabel.setText(s.getFormattedReading());
                case "blood pressure" -> bloodPressureLabel.setText(s.getFormattedReading());
                case "body temp" -> temperatureLabel.setText(s.getFormattedReading());
            }
        }

        lastUpdatedLabel.setText("Updated: " + java.time.LocalTime.now().withNano(0));
        deviceModelLabel.setText(device.getModel());

        // Baterai
        float battery = device.getBatteryLevel();
        System.out.println("Battery: " + battery);
        batteryLabel.setText(String.format("%.0f%%", battery));
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
