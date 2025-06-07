package model;

import java.sql.Timestamp;
import java.util.UUID;

public class Hospital {
    private final UUID hospitalId;
    private String hospitalName;
    private Float latitude;
    private Float longitude;
    private final Timestamp createdAt;

    // Constructor for new hospital
    public Hospital(String hospitalName, Float latitude, Float longitude) {
        this.hospitalId = UUID.randomUUID();
        this.hospitalName = hospitalName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Constructor for existing hospital (from database)
    public Hospital(UUID hospitalId, String hospitalName, Float latitude, Float longitude, Timestamp createdAt) {
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getHospitalId() { return hospitalId; }
    public String getHospitalName() { return hospitalName; }
    public Float getLatitude() { return latitude; }
    public Float getLongitude() { return longitude; }
    public Timestamp getCreatedAt() { return createdAt; }

    // Setters
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    public void setLatitude(Float latitude) { this.latitude = latitude; }
    public void setLongitude(Float longitude) { this.longitude = longitude; }

    @Override
    public String toString() {
        return "Hospital{" +
                "hospitalId=" + hospitalId +
                ", hospitalName='" + hospitalName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Hospital hospital = (Hospital) obj;
        return hospitalId.equals(hospital.hospitalId);
    }

    @Override
    public int hashCode() {
        return hospitalId.hashCode();
    }
}