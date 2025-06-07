package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import model.Sensor;
import model.WearableDevice;
import utils.DataSimulator;

import java.util.List;

public class MonitoringController {

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
    }
}
