package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class WearableDevice {
    private UUID deviceID;
    private String model;
    private float batteryLevel;
    private float latitude;
    private float longitude;
    private UUID lansiaID;
    private List<Sensor> sensors;
    private LocalDateTime createdAt;

    public WearableDevice(UUID deviceID, String model, float batteryLevel, UUID lansiaID) {
        this.deviceID = deviceID;
        this.model = model;
        this.batteryLevel = batteryLevel;
        this.latitude = 0;
        this.longitude = 0;
        this.lansiaID = lansiaID;
    }

    public WearableDevice(UUID deviceID, String model, float batteryLevel, float latitude, float longitude, UUID lansiaID, List<Sensor> sensors, LocalDateTime createdAt) {
        this.deviceID = deviceID;
        this.model = model;
        this.batteryLevel = batteryLevel;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lansiaID = lansiaID;
    }

    public UUID getDeviceId() {
        return deviceID;
    }

    public UUID getLansiaId() {
        return lansiaID;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getModel() { return model; }

    public Float getLatitude() { return latitude; }

    public Float getLongitude() { return longitude; }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public float getBatteryLevel() {
        return batteryLevel;
    }

    public boolean isLowBattery() {
        return this.getBatteryLevel() < 10;
    }
}
