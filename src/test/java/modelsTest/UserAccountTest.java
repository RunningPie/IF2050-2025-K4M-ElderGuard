package modelsTest;

import models.Role;
import models.UserAccount;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserAccountTest {

    @Test
    void testNewUserAccountConstructor() {
        // Data untuk user baru
        String username = "john_doe";
        String password = "securePass123";
        Role role = Role.LANSIA;

        // Membuat object UserAccount dengan constructor user baru
        UserAccount user = new UserAccount(username, password, role);

        // userID dan createdAt harus otomatis terisi
        assertNotNull(user.getUserID(), "UserID harus otomatis dibuat");
        assertNotNull(user.getCreatedAt(), "createdAt harus otomatis dibuat");

        // Field lain harus sesuai
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(role, user.getUserRole());
        assertNull(user.getContactInfo(), "contactInfo default-nya harus null");
    }

    @Test
    void testExistingUserAccountConstructor() {
        // Data untuk user dari database
        UUID userId = UUID.randomUUID();
        String username = "alice";
        String password = "passAlice";
        Role role = Role.ADMIN;
        ZonedDateTime createdAt = ZonedDateTime.now();

        // Membuat object UserAccount dengan constructor existing
        UserAccount user = new UserAccount(userId, username, password, role, createdAt);

        // Semua data harus sama
        assertEquals(userId, user.getUserID());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(role, user.getUserRole());
        assertEquals(createdAt, user.getCreatedAt());
        assertNull(user.getContactInfo(), "contactInfo default-nya harus null");
    }

    @Test
    void testFullConstructor() {
        // Data lengkap
        UUID userId = UUID.randomUUID();
        String username = "bob";
        String password = "bobPass";
        String contactInfo = "bob@example.com";
        Role role = Role.FAMILY;
        ZonedDateTime createdAt = ZonedDateTime.now();

        // Membuat object UserAccount dengan constructor full
        UserAccount user = new UserAccount(userId, username, password, contactInfo, role, createdAt);

        // Semua data harus sesuai
        assertEquals(userId, user.getUserID());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(contactInfo, user.getContactInfo());
        assertEquals(role, user.getUserRole());
        assertEquals(createdAt, user.getCreatedAt());
    }

    @Test
    void testSetters() {
        // Buat user
        UserAccount user = new UserAccount("testUser", "testPass", Role.MEDICAL_STAFF);

        // Update data pakai setter
        user.setUsername("updatedUser");
        user.setPassword("newPass");
        user.setContactInfo("newEmail@example.com");

        // Pastikan data berubah
        assertEquals("updatedUser", user.getUsername());
        assertEquals("newPass", user.getPassword());
        assertEquals("newEmail@example.com", user.getContactInfo());
    }

    @Test
    void testToString() {
        UUID userId = UUID.randomUUID();
        String username = "charlie";
        String password = "passCharlie";
        String contactInfo = "charlie@example.com";
        Role role = Role.LANSIA;
        ZonedDateTime createdAt = ZonedDateTime.now();

        UserAccount user = new UserAccount(userId, username, password, contactInfo, role, createdAt);
        String result = user.toString();

        // Pastikan toString mengandung semua data penting
        assertTrue(result.contains("userID=" + userId.toString()));
        assertTrue(result.contains("username='" + username + "'"));
        assertTrue(result.contains("userRole=" + role.toString()));
        assertTrue(result.contains("contactInfo='" + contactInfo + "'"));
        assertTrue(result.contains("createdAt=" + createdAt.toString()));
    }
}

