package utils;

import models.Sensor;

import java.sql.Timestamp;
import java.util.*;

public class SensorDataSimulator {

    private static final Random random = new Random();

    public static List<Sensor> generateSensors(UUID deviceId) {
        List<Sensor> sensors = new ArrayList<>();
        sensors.add(new Sensor("HeartRate", 98.0f, deviceId));
        sensors.add(new Sensor("BloodPressure", 96.0f, deviceId));
        sensors.add(new Sensor("Temperature", 91.0f, deviceId));
        sensors.add(new Sensor("Oxygen", 94.0f, deviceId));
        updateReadings(sensors);
        return sensors;
    }

    public static void updateReadings(List<Sensor> sensors) {
        for (Sensor s : sensors) {
            float reading = switch (s.getType().toLowerCase()) {
                case "heartrate" -> 60 + random.nextFloat() * 40;
                case "bloodpressure" -> 100 + random.nextFloat() * 20;
                case "temperature" -> 36 + random.nextFloat() * 1.5f;
                case "oxygen" -> 93 + random.nextFloat() * 5;
                default -> random.nextFloat() * 100;
            };
            s.setLastReading(reading);
            s.setBatteryLevel(s.getBatteryLevel() - 0.3f); // Simulasi drain baterai
        }
    }
}
