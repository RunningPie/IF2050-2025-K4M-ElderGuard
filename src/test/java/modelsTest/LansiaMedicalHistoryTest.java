package modelsTest;

import models.LansiaMedicalHistory;
import models.LansiaMedicalHistory.Severity;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LansiaMedicalHistoryTest {

    @Test
    void testNewMedicalHistoryConstructor() {
        // Data untuk medical history baru
        UUID userId = UUID.randomUUID();
        String condition = "Diabetes";
        String description = "Kondisi gula darah tinggi";
        Severity severity = Severity.HIGH;

        LansiaMedicalHistory history = new LansiaMedicalHistory(userId, condition, description, severity);

        // Diagnosis date dan field lain harus sesuai
        assertNotNull(history.getDiagnosisDate(), "Diagnosis date harus otomatis terbentuk");
        assertEquals(userId, history.getUserId());
        assertEquals(condition, history.getMedicalCondition());
        assertEquals(description, history.getDescription());
        assertEquals(severity, history.getSeverity());
    }

    @Test
    void testExistingMedicalHistoryConstructor() {
        // Data medical history dari database
        UUID userId = UUID.randomUUID();
        String condition = "Hipertensi";
        Timestamp diagnosisDate = new Timestamp(System.currentTimeMillis());
        String description = "Tekanan darah tinggi";
        Severity severity = Severity.MEDIUM;

        LansiaMedicalHistory history = new LansiaMedicalHistory(userId, condition, diagnosisDate, description, severity);

        // Semua data harus sesuai
        assertEquals(userId, history.getUserId());
        assertEquals(condition, history.getMedicalCondition());
        assertEquals(diagnosisDate, history.getDiagnosisDate());
        assertEquals(description, history.getDescription());
        assertEquals(severity, history.getSeverity());
    }

    @Test
    void testSetters() {
        // Buat object
        LansiaMedicalHistory history = new LansiaMedicalHistory(
                UUID.randomUUID(), "Arthritis", "Radang sendi", Severity.LOW);

        // Set data baru
        UUID newUserId = UUID.randomUUID();
        history.setUserId(newUserId);
        history.setMedicalCondition("Osteoporosis");
        Timestamp newDiagnosisDate = new Timestamp(System.currentTimeMillis());
        history.setDiagnosisDate(newDiagnosisDate);
        history.setDescription("Tulang keropos");
        history.setSeverity(Severity.CRITICAL);

        // Cek perubahan
        assertEquals(newUserId, history.getUserId());
        assertEquals("Osteoporosis", history.getMedicalCondition());
        assertEquals(newDiagnosisDate, history.getDiagnosisDate());
        assertEquals("Tulang keropos", history.getDescription());
        assertEquals(Severity.CRITICAL, history.getSeverity());
    }

    @Test
    void testIsCritical() {
        // Buat object dengan severity CRITICAL
        LansiaMedicalHistory criticalHistory = new LansiaMedicalHistory(
                UUID.randomUUID(), "Stroke", "Serangan otak", Severity.CRITICAL);
        assertTrue(criticalHistory.isCritical(), "Harus critical");

        // Buat object dengan severity lain
        LansiaMedicalHistory lowHistory = new LansiaMedicalHistory(
                UUID.randomUUID(), "Pusing", "Ringan", Severity.LOW);
        assertFalse(lowHistory.isCritical(), "Tidak boleh critical");
    }

    @Test
    void testGetSeverityColor() {
        // Tes color untuk setiap severity
        LansiaMedicalHistory low = new LansiaMedicalHistory(UUID.randomUUID(), "A", "B", Severity.LOW);
        LansiaMedicalHistory medium = new LansiaMedicalHistory(UUID.randomUUID(), "A", "B", Severity.MEDIUM);
        LansiaMedicalHistory high = new LansiaMedicalHistory(UUID.randomUUID(), "A", "B", Severity.HIGH);
        LansiaMedicalHistory critical = new LansiaMedicalHistory(UUID.randomUUID(), "A", "B", Severity.CRITICAL);

        assertEquals("#28a745", low.getSeverityColor());
        assertEquals("#ffc107", medium.getSeverityColor());
        assertEquals("#fd7e14", high.getSeverityColor());
        assertEquals("#dc3545", critical.getSeverityColor());
    }

    @Test
    void testToString() {
        UUID userId = UUID.randomUUID();
        String condition = "Asma";
        Timestamp diagnosisDate = new Timestamp(System.currentTimeMillis());
        String description = "Sesak napas";
        Severity severity = Severity.HIGH;

        LansiaMedicalHistory history = new LansiaMedicalHistory(userId, condition, diagnosisDate, description, severity);
        String result = history.toString();

        // Pastikan toString mengandung semua data penting
        assertTrue(result.contains("userId=" + userId.toString()));
        assertTrue(result.contains("medicalCondition='" + condition + "'"));
        assertTrue(result.contains("diagnosisDate=" + diagnosisDate.toString()));
        assertTrue(result.contains("description='" + description + "'"));
        assertTrue(result.contains("severity=" + severity));
    }

    @Test
    void testEqualsAndHashCode() {
        UUID userId = UUID.randomUUID();
        String condition = "Diabetes";

        // Dua object dengan userId dan condition sama → harus sama
        LansiaMedicalHistory h1 = new LansiaMedicalHistory(userId, condition, "A", Severity.LOW);
        LansiaMedicalHistory h2 = new LansiaMedicalHistory(userId, condition, "B", Severity.HIGH);

        assertEquals(h1, h2);
        assertEquals(h1.hashCode(), h2.hashCode());

        // Objek dengan userId beda → harus tidak sama
        LansiaMedicalHistory h3 = new LansiaMedicalHistory(UUID.randomUUID(), condition, "A", Severity.LOW);
        assertNotEquals(h1, h3);
    }
}
