package model;

import java.sql.Timestamp;
import java.util.UUID;

public class MedicalStaff {
    private final UUID userId;
    private UUID hospitalId;
    private String specialization;
    private String licenseNumber;
    private final Timestamp createdAt;

    // Constructor for new medical staff
    public MedicalStaff(UUID userId, UUID hospitalId, String specialization, String licenseNumber) {
        this.userId = userId;
        this.hospitalId = hospitalId;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Constructor for existing medical staff (from database)
    public MedicalStaff(UUID userId, UUID hospitalId, String specialization, String licenseNumber, Timestamp createdAt) {
        this.userId = userId;
        this.hospitalId = hospitalId;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getUserId() { return userId; }
    public UUID getHospitalId() { return hospitalId; }
    public String getSpecialization() { return specialization; }
    public String getLicenseNumber() { return licenseNumber; }
    public Timestamp getCreatedAt() { return createdAt; }

    // Setters
    public void setHospitalId(UUID hospitalId) { this.hospitalId = hospitalId; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    @Override
    public String toString() {
        return "MedicalStaff{" +
                "userId=" + userId +
                ", hospitalId=" + hospitalId +
                ", specialization='" + specialization + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MedicalStaff that = (MedicalStaff) obj;
        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }
}