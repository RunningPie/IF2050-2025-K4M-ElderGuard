package model;

public class Sensor {
    private String type;
    private float sensorReadings;

    public Sensor(String type, float sensorReadings) {
        this.type = type;
        this.sensorReadings = sensorReadings;
    }

    public String getType() {
        return type;
    }

    public float getSensorReadings() {
        return sensorReadings;
    }

    public void setSensorReadings(float value) {
        this.sensorReadings = value;
    }
}
