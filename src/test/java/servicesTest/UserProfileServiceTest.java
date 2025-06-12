package servicesTest;

import models.Role;
import models.UserAccount;
import models.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import services.UserProfileService;
import utils.DBUtil;

import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    private UserProfileService service;

    @BeforeEach
    void setUp() {
        service = new UserProfileService();
    }

    @Test
    void testGetUserProfile_successfulLansia() throws Exception {
        UUID userId = UUID.randomUUID();
        Timestamp createdAt = Timestamp.from(Instant.now());
        Timestamp birthdate = Timestamp.valueOf("1950-01-01 00:00:00");

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        try (MockedStatic<DBUtil> mockedDB = mockStatic(DBUtil.class)) {
            mockedDB.when(DBUtil::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);

            when(rs.getString("user_id")).thenReturn(userId.toString());
            when(rs.getString("username")).thenReturn("elder");
            when(rs.getString("contact_info")).thenReturn("08123|Bandung|Jawa Barat");
            when(rs.getString("role")).thenReturn("LANSIA");
            when(rs.getTimestamp("created_at")).thenReturn(createdAt);
            when(rs.getTimestamp("birthdate")).thenReturn(birthdate);
            when(rs.getString("device_model")).thenReturn("ElderBand");
            when(rs.getFloat("battery_level")).thenReturn(80.0f);
            when(rs.wasNull()).thenReturn(false); // battery_level & family_member_count valid
            when(rs.getInt("family_member_count")).thenReturn(2);

            UserProfile profile = service.getUserProfile(userId);

            assertNotNull(profile);
            assertEquals("elder", profile.getUsername());
            assertEquals(Role.LANSIA, profile.getRole());
            assertEquals(2, profile.getFamilyMemberCount());
            assertEquals(80.0f, profile.getBatteryLevel());
        }
    }

    @Test
    void testGetUserProfile_adminLoadsTotalCounts() throws Exception {
        UUID userId = UUID.randomUUID();
        Timestamp createdAt = Timestamp.from(Instant.now());

        Connection conn = mock(Connection.class);
        PreparedStatement profileStmt = mock(PreparedStatement.class);
        ResultSet profileRs = mock(ResultSet.class);

        PreparedStatement usersStmt = mock(PreparedStatement.class);
        ResultSet usersRs = mock(ResultSet.class);

        PreparedStatement devicesStmt = mock(PreparedStatement.class);
        ResultSet devicesRs = mock(ResultSet.class);

        try (MockedStatic<DBUtil> mockedDB = mockStatic(DBUtil.class)) {
            mockedDB.when(DBUtil::getConnection).thenReturn(conn);

            // 🟢 Ini yang penting! Mock untuk query utama getUserProfile
            when(conn.prepareStatement(startsWith("SELECT"))).thenReturn(profileStmt);
            when(profileStmt.executeQuery()).thenReturn(profileRs);
            when(profileRs.next()).thenReturn(true);
            when(profileRs.getString("user_id")).thenReturn(userId.toString());
            when(profileRs.getString("username")).thenReturn("admin");
            when(profileRs.getString("role")).thenReturn("ADMIN");
            when(profileRs.getTimestamp("created_at")).thenReturn(createdAt);
            when(profileRs.getInt("family_member_count")).thenReturn(0);
            when(profileRs.wasNull()).thenReturn(false);

            // 🟢 Mock query total users
            when(conn.prepareStatement(contains("SELECT COUNT(*) FROM user_account"))).thenReturn(usersStmt);
            when(usersStmt.executeQuery()).thenReturn(usersRs);
            when(usersRs.next()).thenReturn(true);
            when(usersRs.getInt(1)).thenReturn(100);

            // 🟢 Mock query total devices
            when(conn.prepareStatement(contains("SELECT COUNT(*) FROM wearable_device"))).thenReturn(devicesStmt);
            when(devicesStmt.executeQuery()).thenReturn(devicesRs);
            when(devicesRs.next()).thenReturn(true);
            when(devicesRs.getInt(1)).thenReturn(50);

            UserProfile profile = service.getUserProfile(userId);
            assertNotNull(profile);
            assertEquals(Role.ADMIN, profile.getRole());
            assertEquals(100, profile.getTotalSystemUsers());
            assertEquals(50, profile.getTotalDevices());
        }
    }

    @Test
    void testGetUserProfile_noUserFound() throws Exception {
        UUID userId = UUID.randomUUID();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        try (MockedStatic<DBUtil> mockedDB = mockStatic(DBUtil.class)) {
            mockedDB.when(DBUtil::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(false);

            UserProfile profile = service.getUserProfile(userId);
            assertNull(profile);
        }
    }

    @Test
    void testGetUserByUsername_success() throws Exception {
        String username = "elder";
        UUID userId = UUID.randomUUID();
        Timestamp createdAt = Timestamp.from(Instant.now());

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        try (MockedStatic<DBUtil> mockedDB = mockStatic(DBUtil.class)) {
            mockedDB.when(DBUtil::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(true);
            when(rs.getString("user_id")).thenReturn(userId.toString());
            when(rs.getString("username")).thenReturn(username);
            when(rs.getString("password")).thenReturn("secret");
            when(rs.getString("contact_info")).thenReturn("phone|city|state");
            when(rs.getString("role")).thenReturn("LANSIA");
            when(rs.getTimestamp("created_at")).thenReturn(createdAt);

            UserAccount account = service.getUserByUsername(username);
            assertNotNull(account);
            assertEquals(username, account.getUsername());
            assertEquals(Role.LANSIA, account.getUserRole());
            assertEquals(ZonedDateTime.ofInstant(createdAt.toInstant(), ZoneId.systemDefault()), account.getCreatedAt());
        }
    }

    @Test
    void testParseAndFormatContactInfo() {
        String phone = "08123";
        String city = "Bandung";
        String state = "Jawa Barat";

        String formatted = UserProfileService.formatContactInfo(phone, city, state);
        assertEquals("08123|Bandung|Jawa Barat", formatted);

        String[] location = UserProfileService.parseLocation(formatted);
        assertEquals(city, location[0]);
        assertEquals(state, location[1]);
    }
}