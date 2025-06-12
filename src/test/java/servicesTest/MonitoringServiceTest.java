package servicesTest;

import models.EmergencyAlert;
import models.Sensor;
import models.WearableDevice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import services.WearableDeviceService;
import services.EmergencyAlertService;
import services.MonitoringService;
import services.SensorReadingService;
import services.SensorService;
import utils.AlertEventManager;
import utils.DataSimulator;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MonitoringServiceTest {

    private MonitoringService monitoringService;

    @BeforeEach
    void setUp() {
        monitoringService = new MonitoringService();
    }

    //Tes logika method private di MonitoringService

    @Test
    void testDeterminePriorityHeartRate() {
        Sensor s = new Sensor("heart rate", 131.0f);
        assertEquals("CRITICAL", callDeterminePriority(s));

        s.setSensorReadings(125.0f);
        assertEquals("HIGH", callDeterminePriority(s));

        s.setSensorReadings(105.0f);
        assertEquals("MEDIUM", callDeterminePriority(s));

        s.setSensorReadings(80.0f);
        assertEquals("LOW", callDeterminePriority(s));
    }

    @Test
    void testDeterminePriorityBloodPressure() {
        Sensor s = new Sensor("blood pressure", 200.0f);
        assertEquals("CRITICAL", callDeterminePriority(s));

        s.setSensorReadings(185.0f);
        assertEquals("HIGH", callDeterminePriority(s));

        s.setSensorReadings(150.0f);
        assertEquals("MEDIUM", callDeterminePriority(s));

        s.setSensorReadings(110.0f);
        assertEquals("LOW", callDeterminePriority(s));
    }

    @Test
    void testDeterminePriorityBodyTemp() {
        Sensor s = new Sensor("body temp", 41.0f);
        assertEquals("CRITICAL", callDeterminePriority(s));

        s.setSensorReadings(39.5f);
        assertEquals("HIGH", callDeterminePriority(s));

        s.setSensorReadings(38.5f);
        assertEquals("MEDIUM", callDeterminePriority(s));

        s.setSensorReadings(36.5f);
        assertEquals("LOW", callDeterminePriority(s));
    }

    @Test
    void testDeterminePriorityUnknownSensor() {
        Sensor s = new Sensor("unknown sensor", 123.0f);
        assertEquals("LOW", callDeterminePriority(s));
    }

    @Test
    void testShouldTriggerAlert() {
        // Mengecek apakah alert perlu dipicu berdasarkan prioritas
        assertFalse(callShouldTriggerAlert("LOW"));
        assertTrue(callShouldTriggerAlert("HIGH"));
        assertTrue(callShouldTriggerAlert("MEDIUM"));
        assertTrue(callShouldTriggerAlert("CRITICAL"));
    }

    // Tes fungsi setUpdateListener()
    @Test
    void testSetUpdateListener() {
        Consumer<WearableDevice> mockListener = mock(Consumer.class);
        monitoringService.setUpdateListener(mockListener);
        // Verifikasi pemanggilan listener akan diuji secara tidak langsung di refreshSensorData() pada tes lain
    }

    // Method bantu (memanggil method private dengan Reflection)

    private String callDeterminePriority(Sensor sensor) {
        try {
            // Memanggil method private determinePriority()
            java.lang.reflect.Method method = MonitoringService.class.getDeclaredMethod("determinePriority", Sensor.class);
            method.setAccessible(true);
            return (String) method.invoke(monitoringService, sensor);
        } catch (Exception e) {
            fail("Gagal memanggil method determinePriority: " + e.getMessage());
            return null;
        }
    }

    private boolean callShouldTriggerAlert(String priority) {
        try {
            // Memanggil method private shouldTriggerAlert()
            java.lang.reflect.Method method = MonitoringService.class.getDeclaredMethod("shouldTriggerAlert", String.class);
            method.setAccessible(true);
            return (boolean) method.invoke(monitoringService, priority);
        } catch (Exception e) {
            fail("Gagal memanggil method shouldTriggerAlert: " + e.getMessage());
            return false;
        }
    }

    private boolean callIsCritical(Sensor sensor) {
        try {
            // Memanggil method private isCritical()
            java.lang.reflect.Method method = MonitoringService.class.getDeclaredMethod("isCritical", Sensor.class);
            method.setAccessible(true);
            return (boolean) method.invoke(monitoringService, sensor);
        } catch (Exception e) {
            fail("Gagal memanggil method isCritical: " + e.getMessage());
            return false;
        }
    }
}
