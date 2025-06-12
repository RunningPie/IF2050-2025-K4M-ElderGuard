package modelsTest;

import models.LansiaMedicalHistory;
import models.LansiaMedicalHistory.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the LansiaMedicalHistory class.
 * This test class verifies the constructors, methods, and the equals/hashCode contract.
 */
class LansiaMedicalHistoryTest {

    private UUID userId;
    private String medicalCondition;
    private String description;
    private Severity severity;

    @BeforeEach
    void setUp() {
        // Initialize common test data before each test
        userId = UUID.randomUUID();
        medicalCondition = "Diabetes";
        description = "High blood sugar condition";
        severity = Severity.HIGH;
    }

    @Test
    @DisplayName("Constructor for new history should set fields correctly")
    void testNewMedicalHistoryConstructor() {
        LansiaMedicalHistory history = new LansiaMedicalHistory(userId, medicalCondition, description, severity);

        // A new history should not have a historyId yet
        assertNull(history.getHistoryId(), "History ID should be null for a new entry.");
        assertEquals(userId, history.getUserId());
        assertEquals(medicalCondition, history.getMedicalCondition());
        assertEquals(description, history.getDescription());
        assertEquals(severity, history.getSeverity());

        // Diagnosis date should be set automatically and be very recent
        assertNotNull(history.getDiagnosisDate(), "Diagnosis date should be automatically generated.");
        assertTrue(history.getDiagnosisDate().getTime() <= System.currentTimeMillis());
    }

    @Test
    @DisplayName("Constructor for existing history should set all fields")
    void testExistingMedicalHistoryConstructor() {
        UUID historyId = UUID.randomUUID();
        Timestamp diagnosisDate = Timestamp.from(Instant.now().minusSeconds(60));

        // Use the constructor for entities that already exist (e.g., from a database)
        LansiaMedicalHistory history = new LansiaMedicalHistory(historyId, userId, medicalCondition, diagnosisDate, description, severity);

        // All fields, including historyId, should match the constructor arguments
        assertEquals(historyId, history.getHistoryId());
        assertEquals(userId, history.getUserId());
        assertEquals(medicalCondition, history.getMedicalCondition());
        assertEquals(diagnosisDate, history.getDiagnosisDate());
        assertEquals(description, history.getDescription());
        assertEquals(severity, history.getSeverity());
    }

    @Test
    @DisplayName("Setters should update the object's state")
    void testSetters() {
        LansiaMedicalHistory history = new LansiaMedicalHistory();

        // Set new data using setters
        UUID newHistoryId = UUID.randomUUID();
        UUID newUserId = UUID.randomUUID();
        Timestamp newDiagnosisDate = new Timestamp(System.currentTimeMillis());

        history.setHistoryId(newHistoryId);
        history.setUserId(newUserId);
        history.setMedicalCondition("Osteoporosis");
        history.setDiagnosisDate(newDiagnosisDate);
        history.setDescription("Brittle bones");
        history.setSeverity(Severity.CRITICAL);

        // Verify that all fields were updated correctly
        assertEquals(newHistoryId, history.getHistoryId());
        assertEquals(newUserId, history.getUserId());
        assertEquals("Osteoporosis", history.getMedicalCondition());
        assertEquals(newDiagnosisDate, history.getDiagnosisDate());
        assertEquals("Brittle bones", history.getDescription());
        assertEquals(Severity.CRITICAL, history.getSeverity());
    }

    @Test
    @DisplayName("isCritical should return true only for CRITICAL severity")
    void testIsCritical() {
        LansiaMedicalHistory criticalHistory = new LansiaMedicalHistory(userId, "Stroke", "Brain attack", Severity.CRITICAL);
        assertTrue(criticalHistory.isCritical(), "Should return true for CRITICAL severity.");

        LansiaMedicalHistory highHistory = new LansiaMedicalHistory(userId, "Hypertension", "High blood pressure", Severity.HIGH);
        assertFalse(highHistory.isCritical(), "Should return false for non-CRITICAL severity.");
    }

    @Test
    @DisplayName("toString method should contain key information")
    void testToString() {
        LansiaMedicalHistory history = new LansiaMedicalHistory(userId, medicalCondition, description, severity);
        String result = history.toString();

        // The toString output should be informative and contain essential field values
        assertTrue(result.contains("userId=" + userId));
        assertTrue(result.contains("medicalCondition='" + medicalCondition + "'"));
        assertTrue(result.contains("description='" + description + "'"));
        assertTrue(result.contains("severity=" + severity));
    }

    @Test
    @DisplayName("Equals and HashCode should be based on historyId only")
    void testEqualsAndHashCode() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        // h1 and h2 represent the same database entity, but have different other attributes
        LansiaMedicalHistory h1 = new LansiaMedicalHistory(id1, userId, "Condition A", new Timestamp(System.currentTimeMillis()), "Desc A", Severity.LOW);
        LansiaMedicalHistory h2 = new LansiaMedicalHistory(id1, UUID.randomUUID(), "Condition B", new Timestamp(System.currentTimeMillis()), "Desc B", Severity.HIGH);

        // h3 has a different historyId
        LansiaMedicalHistory h3 = new LansiaMedicalHistory(id2, userId, "Condition A", new Timestamp(System.currentTimeMillis()), "Desc A", Severity.LOW);

        // h4 is a new entity without a historyId
        LansiaMedicalHistory h4 = new LansiaMedicalHistory(userId, "Condition A", "Desc A", Severity.LOW);

        // --- EQUALS Contract ---
        // 1. Reflexive: an object must equal itself
        assertEquals(h1, h1);

        // 2. Symmetric: if h1.equals(h2) is true, then h2.equals(h1) must be true
        assertEquals(h1, h2, "Objects with the same historyId should be equal.");
        assertEquals(h2, h1, "Equality should be symmetric.");

        // 3. Inequality: objects with different historyId should not be equal
        assertNotEquals(h1, h3, "Objects with different historyId should not be equal.");
        assertNotEquals(h2, h3);

        // 4. Inequality with new objects: an existing object should not equal a new one (null historyId)
        assertNotEquals(h1, h4, "Object with historyId should not equal one with a null historyId.");

        // 5. Inequality with null: an object should not equal null
        assertNotEquals(null, h1);

        // --- HASHCODE Contract ---
        // 1. If two objects are equal, their hash codes MUST be the same
        assertEquals(h1.hashCode(), h2.hashCode(), "Equal objects must have the same hashCode.");

        // 2. It is not required that if two objects are unequal, their hash codes must be different.
        // But it's good practice. We won't assert this as it's not a strict requirement.
    }
}
