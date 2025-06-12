package services;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import models.Sensor;
import models.WearableDevice;
import utils.AlertEventManager;
import utils.DataSimulator;
import models.EmergencyAlert;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import services.SensorReadingService;
import java.sql.Timestamp;
import java.util.UUID;

public class MonitoringService {
    private final DeviceService deviceService = new DeviceService();
    private final SensorService sensorService = new SensorService();
    private final SensorReadingService sensorReadingService = new SensorReadingService();
    private WearableDevice device;
    private int readingCount = 0;
    private static final int MAX_READINGS = 20;     // Limit to 10 readings
    private static final int INTERVAL_SECONDS = 5;
    EmergencyAlertService emergencyAlertService = new EmergencyAlertService();
    private Consumer<WearableDevice> updateListener;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void startMonitoring() {
        device = new WearableDevice("WD-001", "ElderBand Alpha", 100.0f);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (readingCount >= MAX_READINGS) {
                    System.out.println("Reached max reading count. Stopping monitoring.");
                    shutdown();
                    return;
                }
                refreshSensorData();
                readingCount++;
            } catch (Exception e) {
                e.printStackTrace(); // Print to console
            }
        }, 0, INTERVAL_SECONDS, TimeUnit.SECONDS);

    }

    private String determinePriority(Sensor sensor) {
        String type = sensor.getType().toLowerCase();
        float value = Float.parseFloat(sensor.getSensorReadings());

        return switch (type) {
            case "heart rate" -> {
                if (value < 40 || value > 130) yield "CRITICAL";
                else if (value < 50 || value > 120) yield "HIGH";
                else if (value < 60 || value > 100) yield "MEDIUM";
                else yield "LOW";
            }
            case "blood pressure" -> {
                if (value < 70 || value > 190) yield "CRITICAL";
                else if (value < 80 || value > 180) yield "HIGH";
                else if (value < 90 || value > 140) yield "MEDIUM";
                else yield "LOW";
            }
            case "body temp" -> {
                if (value < 34.0 || value > 40.0) yield "CRITICAL";
                else if (value < 35.0 || value > 39.0) yield "HIGH";
                else if (value < 36.0 || value > 38.0) yield "MEDIUM";
                else yield "LOW";
            }
            default -> "LOW"; // For unknown sensors, assume LOW priority
        };
    }

    private boolean shouldTriggerAlert(String priority) {
        // Only trigger alerts for HIGH, MEDIUM, or CRITICAL
        return !priority.equalsIgnoreCase("LOW");
    }


    public void setUpdateListener(Consumer<WearableDevice> listener) {
        this.updateListener = listener;
    }

    private void refreshSensorData() {
        List<Sensor> updatedSensors = DataSimulator.generateDummySensorData();
        device.setSensors(updatedSensors);

        UUID deviceId = deviceService.getDeviceIdByModel(device.getModel());
        if (deviceId == null) {
            System.err.println("No device found for model: " + device.getModel());
            return;
        }

        for (Sensor s : updatedSensors) {
            System.out.println("Sensor: " + s.getType() + ", value: " + s.getFormattedReading());

            // Determine priority
            String priority = determinePriority(s);

            if (shouldTriggerAlert(priority)) {
                EmergencyAlert alert = new EmergencyAlert(
                        UUID.randomUUID().toString(),
                        priority,                              // <-- Use dynamic priority
                        "Test Patient",
                        s.getType() + " Abnormal",
                        "PT001",
                        "Home",
                        LocalDateTime.now(),
                        "ACTIVE",
                        "MED001"
                );
                emergencyAlertService.insertAlert(alert);
                AlertEventManager.getInstance().triggerAlert(alert);
            }

            UUID sensorId = sensorService.getSensorIdByTypeAndDevice(s.getType(), deviceId);

            if (sensorId != null) {
                float readingValue = Float.parseFloat(s.getSensorReadings());
                Timestamp readingTime = Timestamp.valueOf(LocalDateTime.now());
                sensorReadingService.insertSensorReading(sensorId, readingValue, readingTime);
            } else {
                System.err.println("No sensor found for type: " + s.getType() + " and device: " + deviceId);
            }
        }

        if (updateListener != null) {
            updateListener.accept(device);
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

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        System.out.println("Monitoring service shutdown complete.");
    }
}
