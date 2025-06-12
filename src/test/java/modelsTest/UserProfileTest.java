package modelsTest;

import models.Role;
import models.UserAccount;
import models.UserProfile;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileTest {

    @Test
    void testConstructorWithUserAccount() {
        // Buat UserAccount
        UUID userId = UUID.randomUUID();
        String username = "john_doe";
        String password = "pass";
        Role role = Role.LANSIA;
        ZonedDateTime createdAt = ZonedDateTime.now();
        UserAccount userAccount = new UserAccount(userId, username, password, role, createdAt);

        // Buat UserProfile dari UserAccount
        UserProfile profile = new UserProfile(userAccount);

        // Semua field harus sama
        assertEquals(userId, profile.getUserId());
        assertEquals(username, profile.getUsername());
        assertEquals(role, profile.getRole());
        assertEquals(createdAt, profile.getCreatedAt());
    }

    @Test
    void testSettersAndGetters() {
        UserProfile profile = new UserProfile();

        UUID userId = UUID.randomUUID();
        String username = "alice";
        String contactInfo = "08123|Jakarta|DKI";
        Role role = Role.FAMILY;
        ZonedDateTime createdAt = ZonedDateTime.now();
        LocalDateTime birthdate = LocalDateTime.of(1950, 5, 20, 0, 0);
        String deviceModel = "Xiaomi Watch";
        Float batteryLevel = 85.5f;
        String hospitalName = "City Hospital";
        LocalDateTime employmentDate = LocalDateTime.of(2020, 1, 15, 0, 0);
        Integer familyMemberCount = 4;
        Integer totalSystemUsers = 100;
        Integer totalDevices = 50;

        profile.setUserId(userId);
        profile.setUsername(username);
        profile.setContactInfo(contactInfo);
        profile.setRole(role);
        profile.setCreatedAt(createdAt);
        profile.setBirthdate(birthdate);
        profile.setDeviceModel(deviceModel);
        profile.setBatteryLevel(batteryLevel);
        profile.setHospitalName(hospitalName);
        profile.setEmploymentDate(employmentDate);
        profile.setFamilyMemberCount(familyMemberCount);
        profile.setTotalSystemUsers(totalSystemUsers);
        profile.setTotalDevices(totalDevices);

        // Cek semua getter
        assertEquals(userId, profile.getUserId());
        assertEquals(username, profile.getUsername());
        assertEquals(contactInfo, profile.getContactInfo());
        assertEquals(role, profile.getRole());
        assertEquals(createdAt, profile.getCreatedAt());
        assertEquals(birthdate, profile.getBirthdate());
        assertEquals(deviceModel, profile.getDeviceModel());
        assertEquals(batteryLevel, profile.getBatteryLevel());
        assertEquals(hospitalName, profile.getHospitalName());
        assertEquals(employmentDate, profile.getEmploymentDate());
        assertEquals(familyMemberCount, profile.getFamilyMemberCount());
        assertEquals(totalSystemUsers, profile.getTotalSystemUsers());
        assertEquals(totalDevices, profile.getTotalDevices());
    }

    @Test
    void testToString() {
        UUID userId = UUID.randomUUID();
        String username = "charlie";
        Role role = Role.ADMIN;
        ZonedDateTime createdAt = ZonedDateTime.now();

        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setUsername(username);
        profile.setRole(role);
        profile.setCreatedAt(createdAt);

        String result = profile.toString();

        // Pastikan toString mengandung data penting
        assertTrue(result.contains("userId=" + userId.toString()));
        assertTrue(result.contains("username='" + username + "'"));
        assertTrue(result.contains("role=" + role.toString()));
        assertTrue(result.contains("createdAt=" + createdAt.toString()));
    }
}

// belum ada pengujian utility methods karena masih unit testing (utility methods butuh UserProfileServices)