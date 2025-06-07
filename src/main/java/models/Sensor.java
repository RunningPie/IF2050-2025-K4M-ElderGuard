package models;

import java.sql.Timestamp;
import java.util.UUID;

public class Sensor {
    private final UUID sensorId;
    private String type;
    private Float batteryLevel;
    private UUID deviceId;
    private Float lastReading;
    private Timestamp lastReadingTime;
    private final Timestamp createdAt;

    // Constructor for new sensor
    public Sensor(String type, Float batteryLevel, UUID deviceId) {
        this.sensorId = UUID.randomUUID();
        this.type = type;
        this.batteryLevel = batteryLevel;
        this.deviceId = deviceId;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Constructor for existing sensor (from database)
    public Sensor(UUID sensorId, String type, Float batteryLevel, UUID deviceId, Float lastReading, Timestamp lastReadingTime, Timestamp createdAt) {
        this.sensorId = sensorId;
        this.type = type;
        this.batteryLevel = batteryLevel;
        this.deviceId = deviceId;
        this.lastReading = lastReading;
        this.lastReadingTime = lastReadingTime;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getSensorId() { return sensorId; }
    public String getType() { return type; }
    public Float getBatteryLevel() { return batteryLevel; }
    public UUID getDeviceId() { return deviceId; }
    public Float getLastReading() { return lastReading; }
    public Timestamp getLastReadingTime() { return lastReadingTime; }
    public Timestamp getCreatedAt() { return createdAt; }

    // Setters
    public void setType(String type) { this.type = type; }
    public void setBatteryLevel(Float batteryLevel) {
        if (batteryLevel >= 0 && batteryLevel <= 100) {
            this.batteryLevel = batteryLevel;
        }
    }
    public void setDeviceId(UUID deviceId) { this.deviceId = deviceId; }
    public void setLastReading(Float lastReading) {
        this.lastReading = lastReading;
        this.lastReadingTime = new Timestamp(System.currentTimeMillis());
    }

    // Utility methods
    public boolean isLowBattery() {
        return batteryLevel != null && batteryLevel < 15.0f;
    }

    public String getReadingStatus() {
        if (lastReadingTime == null) return "No readings";

        long timeDiff = System.currentTimeMillis() - lastReadingTime.getTime();
        long minutes = timeDiff / (1000 * 60);

        if (minutes < 5) return "Recent";
        if (minutes < 30) return "Normal";
        if (minutes < 60) return "Delayed";
        return "Offline";
    }

    public String getFormattedReading() {
        if (lastReading == null) return "No data";

        switch (type.toLowerCase()) {
            case "heartrate":
                return String.format("%.0f BPM", lastReading);
            case "temperature":
                return String.format("%.1f°C", lastReading);
            case "bloodpressure":
                return String.format("%.0f mmHg", lastReading);
            case "oxygen":
                return String.format("%.1f%%", lastReading);
            default:
                return String.format("%.2f", lastReading);
        }
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "sensorId=" + sensorId +
                ", type='" + type + '\'' +
                ", batteryLevel=" + batteryLevel +
                ", deviceId=" + deviceId +
                ", lastReading=" + lastReading +
                ", lastReadingTime=" + lastReadingTime +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Sensor sensor = (Sensor) obj;
        return sensorId.equals(sensor.sensorId);
    }

    @Override
    public int hashCode() {
        return sensorId.hashCode();
    }
}
