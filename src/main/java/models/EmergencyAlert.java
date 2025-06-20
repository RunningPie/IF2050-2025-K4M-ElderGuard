package models;

import java.time.LocalDateTime;
import java.util.UUID;

public class EmergencyAlert {
    private String alertId;
    private String priority;
    private String patientName;
    private String alertType;
    private UUID patientId;
    private String location;
    private LocalDateTime createdAt;
    private String status;
    private String assignedTo; // <--- Add this field

    // Constructor with assignedTo
    public EmergencyAlert(String alertId, String priority, String patientName, String alertType,
                          UUID patientId, String location, LocalDateTime createdAt, String status, String assignedTo) {
        this.alertId = alertId;
        this.priority = priority;
        this.patientName = patientName;
        this.alertType = alertType;
        this.patientId = patientId;
        this.location = location;
        this.createdAt = createdAt;
        this.status = status;
        this.assignedTo = assignedTo;
    }

    // Getters and Setters
    public String getAlertId() { return alertId; }
    public void setAlertId(String alertId) { this.alertId = alertId; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }

    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

}