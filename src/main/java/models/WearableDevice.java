package model;

import java.sql.Timestamp;
import java.util.UUID;

public class WearableDevice {
    private final UUID deviceId;
    private String model;
    private Float batteryLevel;
    private Float latitude;
    private Float longitude;
    private UUID lansiaId;
    private final Timestamp createdAt;

    // Constructor for new device
    public WearableDevice(String model, Float batteryLevel, Float latitude, Float longitude, UUID lansiaId) {
        this.deviceId = UUID.randomUUID();
        this.model = model;
        this.batteryLevel = batteryLevel;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lansiaId = lansiaId;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Constructor for existing device (from database)
    public WearableDevice(UUID deviceId, String model, Float batteryLevel, Float latitude, Float longitude, UUID lansiaId, Timestamp createdAt) {
        this.deviceId = deviceId;
        this.model = model;
        this.batteryLevel = batteryLevel;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lansiaId = lansiaId;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getDeviceId() { return deviceId; }
    public String getModel() { return model; }
    public Float getBatteryLevel() { return batteryLevel; }
    public Float getLatitude() { return latitude; }
    public Float getLongitude() { return longitude; }
    public UUID getLansiaId() { return lansiaId; }
    public Timestamp getCreatedAt() { return createdAt; }

    // Setters
    public void setModel(String model) { this.model = model; }
    public void setBatteryLevel(Float batteryLevel) {
        if (batteryLevel >= 0 && batteryLevel <= 100) {
            this.batteryLevel = batteryLevel;
        }
    }
    public void setLatitude(Float latitude) { this.latitude = latitude; }
    public void setLongitude(Float longitude) { this.longitude = longitude; }
    public void setLansiaId(UUID lansiaId) { this.lansiaId = lansiaId; }

    // Utility methods
    public boolean isLowBattery() {
        return batteryLevel != null && batteryLevel < 20.0f;
    }

    public String getBatteryStatus() {
        if (batteryLevel == null) return "Unknown";
        if (batteryLevel >= 80) return "Excellent";
        if (batteryLevel >= 50) return "Good";
        if (batteryLevel >= 20) return "Low";
        return "Critical";
    }

    @Override
    public String toString() {
        return "WearableDevice{" +
                "deviceId=" + deviceId +
                ", model='" + model + '\'' +
                ", batteryLevel=" + batteryLevel +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", lansiaId=" + lansiaId +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WearableDevice that = (WearableDevice) obj;
        return deviceId.equals(that.deviceId);
    }

    @Override
    public int hashCode() {
        return deviceId.hashCode();
    }
}