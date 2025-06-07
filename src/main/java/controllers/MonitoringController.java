package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import models.Sensor;
import models.WearableDevice;
import utils.SensorDataSimulator;

import java.util.List;
import java.util.UUID;

public class MonitoringController {

    @FXML
    private TableView<Sensor> sensorTable;

    @FXML
    private TableColumn<Sensor, String> typeColumn;

    @FXML
    private TableColumn<Sensor, String> readingColumn;

    @FXML
    private TableColumn<Sensor, String> statusColumn;

    @FXML
    private Label deviceLabel;

    @FXML
    private ProgressBar batteryBar;

    private WearableDevice device;
    private List<Sensor> sensors;

    @FXML
    public void initialize() {
        UUID lansiaId = UUID.randomUUID(); // placeholder
        device = new WearableDevice("Model-X", 88.0f, -6.9f, 107.6f, lansiaId);
        sensors = SensorDataSimulator.generateSensors(device.getDeviceId());

        deviceLabel.setText("Device: " + device.getModel() + " [" + device.getDeviceId().toString().substring(0, 6) + "]");
        batteryBar.setProgress(device.getBatteryLevel() / 100.0);

        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        readingColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormattedReading());
        });
        statusColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getReadingStatus());
        });

        sensorTable.getItems().addAll(sensors);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> refreshSensorReadings()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void refreshSensorReadings() {
        SensorDataSimulator.updateReadings(sensors);
        sensorTable.refresh();
    }
}
