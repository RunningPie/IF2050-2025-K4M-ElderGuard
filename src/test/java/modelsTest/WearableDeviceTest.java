package modelsTest;

import models.WearableDevice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WearableDeviceTest {

    private UUID testDeviceId;
    private UUID testLansiaId;
    private LocalDateTime testCreatedAt;

    @BeforeEach
    void setUp() {
        testDeviceId = UUID.randomUUID();
        testLansiaId = UUID.randomUUID();
        testCreatedAt = LocalDateTime.now();
    }

    // ## Unit Test untuk Konstruktor dengan 4 Parameter

    @Test
    void testFourParameterConstructorAndGetters() {
        String expectedModel = "SmartWatch Alpha";
        float expectedBatteryLevel = 75.5f;

        WearableDevice device = new WearableDevice(
                testDeviceId,
                expectedModel,
                expectedBatteryLevel,
                testLansiaId
        );

        assertEquals(testDeviceId, device.getDeviceId(), "Device ID harus sama dengan yang diberikan");
        assertEquals(expectedModel, device.getModel(), "Model harus sama dengan yang diberikan");
        assertEquals(expectedBatteryLevel, device.getBatteryLevel(), 0.001, "Battery level harus sama dengan yang diberikan");
        assertEquals(testLansiaId, device.getLansiaId(), "Lansia ID harus sama dengan yang diberikan");

        assertEquals(0.0f, device.getLatitude(), 0.001, "Latitude harus diinisialisasi ke 0");
        assertEquals(0.0f, device.getLongitude(), 0.001, "Longitude harus diinisialisasi ke 0");

        assertNull(device.getCreatedAt(), "CreatedAt harus null karena tidak diinisialisasi di konstruktor ini");
        assertNull(device.getSensors(), "Sensors harus null karena tidak diinisialisasi di konstruktor ini");
    }

    // ## Unit Test untuk Metode `isLowBattery()`

    @Test
    void isLowBattery_shouldReturnTrueWhenBatteryIsBelowTenPercent() {
        WearableDevice device = new WearableDevice(testDeviceId, "Model C", 9.9f, testLansiaId);
        assertTrue(device.isLowBattery(), "isLowBattery harus true jika baterai < 10%");
    }

    @Test
    void isLowBattery_shouldReturnTrueWhenBatteryIsExactlyTenPercent() {
        WearableDevice device = new WearableDevice(testDeviceId, "Model D", 10.0f, testLansiaId);
        assertFalse(device.isLowBattery(), "isLowBattery harus false jika baterai 10%");
    }

    @Test
    void isLowBattery_shouldReturnFalseWhenBatteryIsAboveTenPercent() {
        WearableDevice device = new WearableDevice(testDeviceId, "Model E", 50.0f, testLansiaId);
        assertFalse(device.isLowBattery(), "isLowBattery harus false jika baterai > 10%");
    }
}