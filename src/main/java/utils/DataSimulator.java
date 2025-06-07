package utils;

import model.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataSimulator {

    private static final Random random = new Random();

    public static List<Sensor> generateDummySensorData() {
        List<Sensor> list = new ArrayList<>();
        list.add(new Sensor("Heart Rate", 60 + random.nextFloat() * 40));
        list.add(new Sensor("Blood Pressure", 110 + random.nextFloat() * 20));
        list.add(new Sensor("Body Temp", 36 + random.nextFloat()));
        list.add(new Sensor("Oxygen Saturation", 95 + random.nextFloat() * 5));
        return list;
    }
}
