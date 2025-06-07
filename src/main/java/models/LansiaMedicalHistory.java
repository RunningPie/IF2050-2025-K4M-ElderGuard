package model;

import java.sql.Timestamp;
import java.util.UUID;

public class LansiaMedicalHistory {
    private UUID userId;
    private String medicalCondition;
    private Timestamp diagnosisDate;
    private String description;
    private Severity severity;

    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    // Constructor for new medical history entry
    public LansiaMedicalHistory(UUID userId, String medicalCondition, String description, Severity severity) {
        this.userId = userId;
        this.medicalCondition = medicalCondition;
        this.description = description;
        this.severity = severity;
        this.diagnosisDate = new Timestamp(System.currentTimeMillis());
    }

    // Constructor for existing medical history (from database)
    public LansiaMedicalHistory(UUID userId, String medicalCondition, Timestamp diagnosisDate, String description, Severity severity) {
        this.userId = userId;
        this.medicalCondition = medicalCondition;
        this.diagnosisDate = diagnosisDate;
        this.description = description;
        this.severity = severity;
    }

    // Getters
    public UUID getUserId() { return userId; }
    public String getMedicalCondition() { return medicalCondition; }
    public Timestamp getDiagnosisDate() { return diagnosisDate; }
    public String getDescription() { return description; }
    public Severity getSeverity() { return severity; }

    // Setters
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setMedicalCondition(String medicalCondition) { this.medicalCondition = medicalCondition; }
    public void setDiagnosisDate(Timestamp diagnosisDate) { this.diagnosisDate = diagnosisDate; }
    public void setDescription(String description) { this.description = description; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    // Utility methods
    public boolean isCritical() {
        return severity == Severity.CRITICAL;
    }

    public String getSeverityColor() {
        switch (severity) {
            case LOW: return "#28a745";      // Green
            case MEDIUM: return "#ffc107";   // Yellow
            case HIGH: return "#fd7e14";     // Orange
            case CRITICAL: return "#dc3545"; // Red
            default: return "#6c757d";       // Gray
        }
    }

    @Override
    public String toString() {
        return "LansiaMedicalHistory{" +
                "userId=" + userId +
                ", medicalCondition='" + medicalCondition + '\'' +
                ", diagnosisDate=" + diagnosisDate +
                ", description='" + description + '\'' +
                ", severity=" + severity +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LansiaMedicalHistory that = (LansiaMedicalHistory) obj;
        return userId.equals(that.userId) && medicalCondition.equals(that.medicalCondition);
    }

    @Override
    public int hashCode() {
        return userId.hashCode() + medicalCondition.hashCode();
    }
}