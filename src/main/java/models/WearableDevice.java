package models;

import java.util.List;

public class WearableDevice {
    private String deviceID;
    private String model;
    private float batteryLevel;
    private List<Sensor> sensors;

    public WearableDevice(String deviceID, String model, float batteryLevel) {
        this.deviceID = deviceID;
        this.model = model;
        this.batteryLevel = batteryLevel;
    }

    public String getModel() {
        return model;
    }

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
