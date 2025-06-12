package services;

import models.Sensor;
import models.WearableDevice;
import models.EmergencyAlert;
import models.UserAccount;
import models.Role;
import utils.AlertEventManager;
import utils.DataSimulator;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MonitoringService {
    private final WearableDeviceService wearableDeviceService = new WearableDeviceService();
    private final SensorService sensorService = new SensorService();
    private final SensorReadingService sensorReadingService = new SensorReadingService();
    private final EmergencyAlertService emergencyAlertService = new EmergencyAlertService();
    private final AuthService authService = new AuthService();

    private WearableDevice device;
    private UserAccount patient;

    private Consumer<WearableDevice> updateListener;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private int readingCount = 0;
    private static final int MAX_READINGS = 10;
    private static final int INTERVAL_SECONDS = 10;

    public void startMonitoring() {
        // Select random Lansia and their device
        List<UserAccount> allUsers = authService.getAllUsers();
        List<WearableDevice> allDevices = wearableDeviceService.getAllDevices();

        for (UserAccount user : allUsers) {
            if (user.getUserRole() == Role.LANSIA) {
                for (WearableDevice dev : allDevices) {
                    if (dev.getLansiaId() != null && dev.getLansiaId().equals(user.getUserID())) {
                        this.patient = user;
                        this.device = dev;
                        break;
                    }
                }
            }
            if (patient != null && device != null) break;
        }

        if (device == null || patient == null) {
            System.err.println("❌ No matching Lansia and device found. Monitoring aborted.");
            return;
        }

        System.out.println("✅ Monitoring started for Lansia: " + patient.getUsername() + "with device: " + device.getDeviceId() + " - " + device.getModel());

        scheduler.scheduleAtFixedRate(() -> {
            if (readingCount >= MAX_READINGS) {
                System.out.println("✅ Reached max reading count. Stopping monitoring.");
                shutdown();
                return;
            }
            refreshSensorData();
            readingCount++;
        }, 0, INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private void refreshSensorData() {
        List<Sensor> updatedSensors = DataSimulator.generateDummySensorData();
        device.setSensors(updatedSensors);

        UUID deviceId = device.getDeviceId();

        for (Sensor s : updatedSensors) {
            System.out.println("Sensor: " + s.getType() + ", value: " + s.getFormattedReading());

            // Save reading to DB
            UUID sensorId = sensorService.getSensorIdByTypeAndDevice(s.getType(), deviceId);
            if (sensorId != null) {
                float readingValue = Float.parseFloat(s.getSensorReadings());
                Timestamp readingTime = Timestamp.valueOf(LocalDateTime.now());
                sensorReadingService.insertSensorReading(sensorId, readingValue, readingTime);
            }

            // Determine priority
            String priority = determinePriority(s);
            if (shouldTriggerAlert(priority)) {
                EmergencyAlert alert = new EmergencyAlert(
                        UUID.randomUUID().toString(),
                        priority,
                        patient.getUsername(),
                        s.getType() + " Abnormal",
                        patient.getUserID(),
                        "Lat: " + device.getLatitude() + ", Lon: " + device.getLongitude(),
                        LocalDateTime.now(),
                        "ACTIVE",
                        "MED001"
                );
                emergencyAlertService.insertAlert(alert);
                AlertEventManager.getInstance().triggerAlert(alert);
            }
        }

        // Notify UI
        if (updateListener != null) {
            updateListener.accept(device);
        }
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
            default -> "LOW";
        };
    }

    private boolean shouldTriggerAlert(String priority) {
        return !priority.equalsIgnoreCase("LOW");
    }

    public void setUpdateListener(Consumer<WearableDevice> listener) {
        this.updateListener = listener;
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
        System.out.println("🛑 Monitoring service shutdown complete.");
    }
}
