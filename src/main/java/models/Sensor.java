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

    public String getSensorReadings() { return String.format("%.2f", sensorReadings); }

    public String getFormattedReading() {
        return String.format("%.2f", sensorReadings);
    }

    public void setSensorReadings(float value) {
        this.sensorReadings = value;
    }
}
