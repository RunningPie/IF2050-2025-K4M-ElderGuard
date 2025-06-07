package model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Complete user profile data model that includes all database fields
 */
public class UserProfile {
    // Basic user account data
    private UUID userId;
    private String username;
    private String contactInfo; // Stores phone|city|state format
    private Role role;
    private ZonedDateTime createdAt;

    // Lansia-specific data
    private LocalDateTime birthdate;
    private String deviceModel;
    private Float batteryLevel;

    // Medical Staff-specific data
    private String hospitalName;
    private LocalDateTime employmentDate;

    // Family-specific data
    private Integer familyMemberCount;

    // Admin-specific data
    private Integer totalSystemUsers;
    private Integer totalDevices;

    // Constructors
    public UserProfile() {}

    public UserProfile(UserAccount userAccount) {
        this.userId = userAccount.getUserID();
        this.username = userAccount.getUsername();
        this.role = userAccount.getUserRole();
        this.createdAt = userAccount.getCreatedAt();
    }

    // Getters and Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    // Lansia-specific getters/setters
    public LocalDateTime getBirthdate() { return birthdate; }
    public void setBirthdate(LocalDateTime birthdate) { this.birthdate = birthdate; }

    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }

    public Float getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(Float batteryLevel) { this.batteryLevel = batteryLevel; }

    // Medical Staff-specific getters/setters
    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public LocalDateTime getEmploymentDate() { return employmentDate; }
    public void setEmploymentDate(LocalDateTime employmentDate) { this.employmentDate = employmentDate; }

    // Family-specific getters/setters
    public Integer getFamilyMemberCount() { return familyMemberCount; }
    public void setFamilyMemberCount(Integer familyMemberCount) { this.familyMemberCount = familyMemberCount; }

    // Admin-specific getters/setters
    public Integer getTotalSystemUsers() { return totalSystemUsers; }
    public void setTotalSystemUsers(Integer totalSystemUsers) { this.totalSystemUsers = totalSystemUsers; }

    public Integer getTotalDevices() { return totalDevices; }
    public void setTotalDevices(Integer totalDevices) { this.totalDevices = totalDevices; }

    // Utility methods
    public String getPhone() {
        return service.UserProfileService.extractPhone(contactInfo);
    }

    public String[] getLocation() {
        return service.UserProfileService.parseLocation(contactInfo);
    }

    public void setLocationAndPhone(String phone, String city, String state) {
        this.contactInfo = service.UserProfileService.formatContactInfo(phone, city, state);
    }

    @Override
    public String toString() {
        return "UserProfileData{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}