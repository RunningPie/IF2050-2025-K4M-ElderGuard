package utils;

import model.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataSimulator {

    private static final Random random = new Random();

    public static List<Sensor> generateDummySensorData() {
        List<Sensor> list = new ArrayList<>();
        list.add(new Sensor("Heart Rate", 45 + random.nextFloat() * 80));
        list.add(new Sensor("Blood Pressure", 70 + random.nextFloat() * 120));
        list.add(new Sensor("Body Temp", 34 + random.nextFloat() * 10));
        list.add(new Sensor("Oxygen Saturation", 80 + random.nextFloat() * 20));
        return list;
    }
}
