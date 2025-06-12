package modelsTest;

import models.Sensor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue; // Import assertTrue

class SensorTest {

    @Test
    void testConstructorAndGetters() {
        // Membuat object Sensor
        Sensor sensor = new Sensor("Temperature", 25.6789f);

        // Cek tipe sensor
        assertEquals("Temperature", sensor.getType());

        // Cek pembacaan sensor (harus 2 desimal)
        String expectedReading = String.format("%.2f", 25.6789f);
        assertEquals(expectedReading, sensor.getSensorReadings());

        // Cek metode getFormattedReading juga
        assertEquals(expectedReading, sensor.getFormattedReading());
    }

    @Test
    void testSetSensorReadings() {
        // Membuat object Sensor
        Sensor sensor = new Sensor("Heart Rate", 60.0f);

        // Ubah nilai pembacaan sensor
        float newValue = 72.3456f;
        sensor.setSensorReadings(newValue);

        // Cek pembacaan baru (harus 2 desimal)
        String expectedReading = String.format("%.2f", newValue);
        assertEquals(expectedReading, sensor.getSensorReadings());
        assertEquals(expectedReading, sensor.getFormattedReading()); // Tambahkan ini juga
    }

    @Test
    void testFormattedReadingConsistency() {
        // Menguji bahwa kedua metode pemformatan selalu konsisten
        Sensor sensor1 = new Sensor("Pressure", 101.325f);
        String reading1 = sensor1.getSensorReadings();
        String reading2 = sensor1.getFormattedReading();

        // Memastikan kedua metode mengembalikan hasil yang sama persis
        assertEquals(reading1, reading2, "getSensorReadings() dan getFormattedReading() harus mengembalikan hasil yang sama.");

        // Memastikan outputnya memang terformat dengan benar (angka, titik, dua angka desimal)
        assertTrue(reading1.matches("-?\\d+\\.\\d{2}"), "Format string harus memiliki dua angka desimal dan mendukung negatif.");

        // Uji dengan nilai negatif
        Sensor sensorNeg = new Sensor("Voltage", -12.345f);
        String negReading1 = sensorNeg.getSensorReadings();
        String negReading2 = sensorNeg.getFormattedReading();
        assertEquals(negReading1, negReading2);
        assertEquals("-12.35", negReading1); // Pastikan pembulatan negatif juga benar
    }

    @Test
    void testFloatValueFormatting() {
        // Menguji berbagai skenario nilai float untuk memastikan pemformatan yang benar
        Sensor sensorRoundUp = new Sensor("Humidity", 75.125f);
        assertEquals("75.13", sensorRoundUp.getSensorReadings(), "Harus membulatkan ke atas.");
        assertEquals("75.13", sensorRoundUp.getFormattedReading(), "Harus membulatkan ke atas.");

        Sensor sensorNoDecimal = new Sensor("Power", 100f);
        assertEquals("100.00", sensorNoDecimal.getSensorReadings(), "Harus memiliki dua desimal, meskipun nilai bulat.");
        assertEquals("100.00", sensorNoDecimal.getFormattedReading(), "Harus memiliki dua desimal, meskipun nilai bulat.");

        Sensor sensorOneDecimal = new Sensor("Flow", 1.5f);
        assertEquals("1.50", sensorOneDecimal.getSensorReadings(), "Harus memiliki dua desimal, meskipun satu desimal.");
        assertEquals("1.50", sensorOneDecimal.getFormattedReading(), "Harus memiliki dua desimal, meskipun satu desimal.");

        Sensor sensorLongDecimal = new Sensor("Weight", 65.43210987f);
        assertEquals("65.43", sensorLongDecimal.getSensorReadings(), "Harus memotong ke dua desimal.");
        assertEquals("65.43", sensorLongDecimal.getFormattedReading(), "Harus memotong ke dua desimal.");

        Sensor sensorZero = new Sensor("Null", 0f);
        assertEquals("0.00", sensorZero.getSensorReadings(), "Nol harus diformat dengan dua desimal.");
        assertEquals("0.00", sensorZero.getFormattedReading(), "Nol harus diformat dengan dua desimal.");
    }
}
