package modelsTest;

import models.EmergencyAlert;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmergencyAlertTest {

    @Test
    void testConstructorAndGetters() {
        LocalDateTime createdAt = LocalDateTime.now();

        EmergencyAlert alert = new EmergencyAlert(
                "A001", "High", "John Doe", "Cardiac Arrest",
                "P001", "Room 101", createdAt, "Active", "Nurse1"
        );

        // Cek nilai getter
        assertEquals("A001", alert.getAlertId());
        assertEquals("High", alert.getPriority());
        assertEquals("John Doe", alert.getPatientName());
        assertEquals("Cardiac Arrest", alert.getAlertType());
        assertEquals("P001", alert.getPatientId());
        assertEquals("Room 101", alert.getLocation());
        assertEquals(createdAt, alert.getCreatedAt());
        assertEquals("Active", alert.getStatus());
        assertEquals("Nurse1", alert.getAssignedTo());
    }

    @Test
    void testSetters() {
        LocalDateTime createdAt = LocalDateTime.now();
        EmergencyAlert alert = new EmergencyAlert(
                "A001", "High", "John Doe", "Cardiac Arrest",
                "P001", "Room 101", createdAt, "Active", "Nurse1"
        );

        // Ubah nilai via setter
        alert.setPriority("Low");
        alert.setPatientName("Jane Doe");
        alert.setStatus("Resolved");
        alert.setAssignedTo("Nurse2");

        // Pastikan setter bekerja
        assertEquals("Low", alert.getPriority());
        assertEquals("Jane Doe", alert.getPatientName());
        assertEquals("Resolved", alert.getStatus());
        assertEquals("Nurse2", alert.getAssignedTo());
    }

    @Test
    void testCreatedAtIsNotNull() {
        EmergencyAlert alert = new EmergencyAlert(
                "A002", "Medium", "Alice", "Fall", "P002",
                "Room 202", LocalDateTime.now(), "Pending", "Nurse3"
        );

        // createdAt tidak boleh null
        assertNotNull(alert.getCreatedAt());
    }
}