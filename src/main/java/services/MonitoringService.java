// services/MonitoringService.java
package services;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import models.Sensor;
import models.WearableDevice;
import utils.AlertEventManager;
import utils.DataSimulator;
import models.EmergencyAlert;

import java.time.LocalDateTime;
import java.util.List;

public class MonitoringService {
    private WearableDevice device;
    private Timeline timeline;

    public void startMonitoring() {
        device = new WearableDevice("WD-001", "ElderBand Alpha", 100.0f);

        timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> refreshSensorData()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void refreshSensorData() {
        List<Sensor> updatedSensors = DataSimulator.generateDummySensorData();
        device.setSensors(updatedSensors);

        for (Sensor s : updatedSensors) {
            System.out.println("Sensor: " + s.getType() + ", " + s.getSensorReadings());
            if (isCritical(s)) {
                EmergencyAlert alert = new EmergencyAlert(
                        "EA" + System.currentTimeMillis(),
                        "Critical",
                        "Test Patient",
                        s.getType() + " Abnormal",
                        "PT001",
                        "Home",
                        LocalDateTime.now(),
                        "Active"
                );
                AlertEventManager.getInstance().triggerAlert(alert);
            }
        }
    }

    private boolean isCritical(Sensor sensor) {
        String type = sensor.getType().toLowerCase();
        float value = Float.parseFloat(sensor.getSensorReadings());
        return switch (type) {
            case "heart rate" -> value < 50 || value > 120;
            case "blood pressure" -> value < 80 || value > 180;
            case "body temp" -> value < 35.0 || value > 39.0;
            default -> false;
        };
    }
}
