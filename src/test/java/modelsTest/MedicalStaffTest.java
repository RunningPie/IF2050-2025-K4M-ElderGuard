package modelsTest;

import models.MedicalStaff;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MedicalStaffTest {

    @Test
    void testNewMedicalStaffConstructor() {
        // Data untuk medical staff baru
        UUID userId = UUID.randomUUID();
        UUID hospitalId = UUID.randomUUID();
        String specialization = "Cardiology";
        String licenseNumber = "12345-ABC";

        // Membuat object MedicalStaff dengan constructor baru
        MedicalStaff staff = new MedicalStaff(userId, hospitalId, specialization, licenseNumber);

        // createdAt dan semua field harus sesuai
        assertNotNull(staff.getCreatedAt(), "CreatedAt harus otomatis terisi");
        assertEquals(userId, staff.getUserId());
        assertEquals(hospitalId, staff.getHospitalId());
        assertEquals(specialization, staff.getSpecialization());
        assertEquals(licenseNumber, staff.getLicenseNumber());
    }

    @Test
    void testExistingMedicalStaffConstructor() {
        // Data untuk medical staff dari database
        UUID userId = UUID.randomUUID();
        UUID hospitalId = UUID.randomUUID();
        String specialization = "Neurology";
        String licenseNumber = "67890-XYZ";
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        // Membuat object MedicalStaff dengan constructor existing
        MedicalStaff staff = new MedicalStaff(userId, hospitalId, specialization, licenseNumber, createdAt);

        // Semua data harus sesuai
        assertEquals(userId, staff.getUserId());
        assertEquals(hospitalId, staff.getHospitalId());
        assertEquals(specialization, staff.getSpecialization());
        assertEquals(licenseNumber, staff.getLicenseNumber());
        assertEquals(createdAt, staff.getCreatedAt());
    }

    @Test
    void testSetters() {
        // Membuat medical staff
        MedicalStaff staff = new MedicalStaff(
                UUID.randomUUID(), UUID.randomUUID(), "Dermatology", "99999-DEF");

        // Mengatur data baru
        UUID newHospitalId = UUID.randomUUID();
        staff.setHospitalId(newHospitalId);
        staff.setSpecialization("Oncology");
        staff.setLicenseNumber("55555-GHI");

        // Pastikan perubahan benar
        assertEquals(newHospitalId, staff.getHospitalId());
        assertEquals("Oncology", staff.getSpecialization());
        assertEquals("55555-GHI", staff.getLicenseNumber());
    }

    @Test
    void testToString() {
        UUID userId = UUID.randomUUID();
        UUID hospitalId = UUID.randomUUID();
        String specialization = "Pediatrics";
        String licenseNumber = "33333-AAA";
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        MedicalStaff staff = new MedicalStaff(userId, hospitalId, specialization, licenseNumber, createdAt);
        String result = staff.toString();

        // Pastikan semua data penting ada di string
        assertTrue(result.contains("userId=" + userId.toString()));
        assertTrue(result.contains("hospitalId=" + hospitalId.toString()));
        assertTrue(result.contains("specialization='" + specialization + "'"));
        assertTrue(result.contains("licenseNumber='" + licenseNumber + "'"));
        assertTrue(result.contains("createdAt=" + createdAt.toString()));
    }

    @Test
    void testEqualsAndHashCode() {
        UUID userId = UUID.randomUUID();

        // Dua object dengan userId sama → harus sama
        MedicalStaff s1 = new MedicalStaff(userId, UUID.randomUUID(), "A", "123");
        MedicalStaff s2 = new MedicalStaff(userId, UUID.randomUUID(), "B", "456");

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());

        // Object dengan userId beda → harus tidak sama
        MedicalStaff s3 = new MedicalStaff(UUID.randomUUID(), UUID.randomUUID(), "C", "789");
        assertNotEquals(s1, s3);
    }
}
