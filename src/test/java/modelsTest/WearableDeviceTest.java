package modelsTest;

import models.Sensor;
import models.WearableDevice;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WearableDeviceTest {

    @Test
    void testConstructorAndGetters() {
        // Data dummy
        String deviceID = "device123";
        String model = "Xiaomi Watch";
        float batteryLevel = 75.0f;

        // Buat WearableDevice
        WearableDevice device = new WearableDevice(deviceID, model, batteryLevel);

        // Pastikan getter berfungsi
        assertEquals(model, device.getModel());
        assertEquals(batteryLevel, device.getBatteryLevel());
        // sensors awalnya null
        assertNull(device.getSensors(), "Sensors harus null awalnya.");
    }

    @Test
    void testSetSensors() {
        // Buat WearableDevice
        WearableDevice device = new WearableDevice("dev001", "Fitbit", 55.0f);

        // Buat beberapa sensor dummy
        Sensor s1 = new Sensor("Heart Rate", 72.0f);
        Sensor s2 = new Sensor("Temperature", 36.5f);
        List<Sensor> sensors = Arrays.asList(s1, s2);

        // Set sensors
        device.setSensors(sensors);

        // Pastikan sensors diatur dengan benar
        assertNotNull(device.getSensors(), "Sensors harus tidak null setelah di-set.");
        assertEquals(2, device.getSensors().size(), "Jumlah sensors harus sesuai.");
        assertEquals(s1, device.getSensors().get(0));
        assertEquals(s2, device.getSensors().get(1));
    }

    @Test
    void testIsLowBattery() {
        // Battery level tinggi
        WearableDevice device1 = new WearableDevice("dev1", "Model A", 50.0f);
        assertFalse(device1.isLowBattery(), "Battery masih cukup, harus false.");

        // Battery level rendah
        WearableDevice device2 = new WearableDevice("dev2", "Model B", 5.0f);
        assertTrue(device2.isLowBattery(), "Battery rendah, harus true.");

        // Battery level pas 10 (bukan low)
        WearableDevice device3 = new WearableDevice("dev3", "Model C", 10.0f);
        assertFalse(device3.isLowBattery(), "Battery pas 10, bukan low.");
    }
}
