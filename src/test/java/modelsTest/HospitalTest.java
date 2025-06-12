package modelsTest;

import models.Hospital;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HospitalTest {

    @Test
    void testNewHospitalConstructor() {
        // Data dummy untuk rumah sakit baru
        String name = "City Hospital";
        Float latitude = 10.0f;
        Float longitude = 20.0f;

        // Membuat objek Hospital dengan constructor rumah sakit baru
        Hospital hospital = new Hospital(name, latitude, longitude);

        // hospitalId dan createdAt harus otomatis terisi
        assertNotNull(hospital.getHospitalId(), "Hospital ID harus terbentuk");
        assertEquals(name, hospital.getHospitalName());
        assertEquals(latitude, hospital.getLatitude());
        assertEquals(longitude, hospital.getLongitude());
        assertNotNull(hospital.getCreatedAt(), "CreatedAt harus otomatis terisi");
    }

    @Test
    void testExistingHospitalConstructor() {
        // Data rumah sakit yang sudah ada di database
        UUID id = UUID.randomUUID();
        String name = "General Hospital";
        Float latitude = 30.0f;
        Float longitude = 40.0f;
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        // Membuat objek Hospital dengan constructor untuk rumah sakit yang sudah ada
        Hospital hospital = new Hospital(id, name, latitude, longitude, createdAt);

        // Semua data harus sama dengan yang diberikan
        assertEquals(id, hospital.getHospitalId());
        assertEquals(name, hospital.getHospitalName());
        assertEquals(latitude, hospital.getLatitude());
        assertEquals(longitude, hospital.getLongitude());
        assertEquals(createdAt, hospital.getCreatedAt());
    }

    @Test
    void testSetters() {
        // Membuat rumah sakit baru
        Hospital hospital = new Hospital("Old Name", 50.0f, 60.0f);

        // Mengubah data menggunakan setter
        hospital.setHospitalName("New Name");
        hospital.setLatitude(70.0f);
        hospital.setLongitude(80.0f);

        // Pastikan data berubah sesuai yang di-set
        assertEquals("New Name", hospital.getHospitalName());
        assertEquals(70.0f, hospital.getLatitude());
        assertEquals(80.0f, hospital.getLongitude());
    }

    @Test
    void testToString() {
        // Data rumah sakit
        UUID id = UUID.randomUUID();
        String name = "Sample Hospital";
        Float latitude = 90.0f;
        Float longitude = 100.0f;
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        Hospital hospital = new Hospital(id, name, latitude, longitude, createdAt);
        String result = hospital.toString();

        // Pastikan semua data penting ada di string hasil toString()
        assertTrue(result.contains("hospitalId=" + id.toString()));
        assertTrue(result.contains("hospitalName='" + name + "'"));
        assertTrue(result.contains("latitude=" + latitude));
        assertTrue(result.contains("longitude=" + longitude));
        assertTrue(result.contains("createdAt=" + createdAt.toString()));
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        // Dua objek Hospital dengan hospitalId yang sama → harus dianggap sama
        Hospital h1 = new Hospital(id, "Name1", 1.0f, 2.0f, new Timestamp(System.currentTimeMillis()));
        Hospital h2 = new Hospital(id, "Name2", 3.0f, 4.0f, new Timestamp(System.currentTimeMillis()));

        // Harus sama karena hospitalId sama
        assertEquals(h1, h2);
        assertEquals(h1.hashCode(), h2.hashCode());

        // Buat Hospital baru dengan id berbeda → harus tidak sama
        Hospital h3 = new Hospital("New Hospital", 5.0f, 6.0f);
        assertNotEquals(h1, h3);
    }
}