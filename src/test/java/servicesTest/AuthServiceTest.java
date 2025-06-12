package servicesTest;

import models.Role;
import models.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import services.AuthService;
import utils.DBUtil;
import utils.SessionManager;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService();
    }

    @Test
    void testAuthenticateSuccess() throws Exception {
        String username = "testuser";
        String password = "123";
        UUID userId = UUID.randomUUID();
        String roleString = "LANSIA";
        Timestamp createdAt = Timestamp.from(ZonedDateTime.now().toInstant());

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            Connection conn = mock(Connection.class);
            PreparedStatement stmt = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);

            mockedDBUtil.when(DBUtil::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(true);
            when(rs.getString("password")).thenReturn(password);
            when(rs.getString("user_id")).thenReturn(userId.toString());
            when(rs.getString("role")).thenReturn(roleString);
            when(rs.getTimestamp("created_at")).thenReturn(createdAt);

            UserAccount user = authService.authenticate(username, password);

            assertNotNull(user);
            assertEquals(username, user.getUsername());
            assertEquals(password, user.getPassword());
            assertEquals(Role.valueOf(roleString), user.getUserRole());
            assertEquals(userId, user.getUserID());
            assertEquals(createdAt.toInstant().atZone(ZoneId.systemDefault()), user.getCreatedAt());
        }
    }

    @Test
    void testAuthenticateFailWrongPassword() throws Exception {
        String username = "user";
        String inputPassword = "wrongpass";

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            Connection conn = mock(Connection.class);
            PreparedStatement stmt = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);

            mockedDBUtil.when(DBUtil::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(true);
            when(rs.getString("password")).thenReturn("correctpass");

            UserAccount user = authService.authenticate(username, inputPassword);

            assertNull(user);
        }
    }

    @Test
    void testAuthenticateFailUserNotFound() throws Exception {
        String username = "nonexistent";
        String password = "anypass";

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            Connection conn = mock(Connection.class);
            PreparedStatement stmt = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);

            mockedDBUtil.when(DBUtil::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(false);

            UserAccount user = authService.authenticate(username, password);

            assertNull(user);
        }
    }

    @Test
    void testCreateAccountSQLExceptionOnLansiaInsert() throws Exception {
        String username = "sql_error_lansia";
        String password = "pass";
        Role role = Role.LANSIA;

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            Connection conn = mock(Connection.class);
            PreparedStatement stmtUser = mock(PreparedStatement.class);
            PreparedStatement stmtLansia = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);

            mockedDBUtil.when(DBUtil::getConnection).thenReturn(conn);

            when(conn.prepareStatement(contains("SELECT COUNT(*) FROM user_account WHERE username = ?"))).thenReturn(stmtUser);
            when(stmtUser.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);
            when(rs.getInt(1)).thenReturn(0);

            when(conn.prepareStatement(contains("INSERT INTO user_account"))).thenReturn(stmtUser);
            when(stmtUser.executeUpdate()).thenReturn(1);

            when(conn.prepareStatement(contains("INSERT INTO lansia"))).thenReturn(stmtLansia);
            when(stmtLansia.executeUpdate()).thenThrow(new SQLException("Lansia Insert Error"));

            boolean result = authService.createAccount(username, password, role);

            // Tetap true meskipun createLansiaEntry gagal (hanya log error)
            assertTrue(result);
        }
    }

    @Test
    void testUpdateProfileSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        String newUsername = "new_username";

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            Connection conn = mock(Connection.class);
            PreparedStatement stmtCount = mock(PreparedStatement.class);
            PreparedStatement stmtUpdate = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);

            mockedDBUtil.when(DBUtil::getConnection).thenReturn(conn);

            when(conn.prepareStatement(contains("SELECT COUNT(*) FROM user_account WHERE username = ? AND user_id != ?"))).thenReturn(stmtCount);
            when(stmtCount.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);
            when(rs.getInt(1)).thenReturn(0);

            when(conn.prepareStatement(contains("UPDATE user_account SET username"))).thenReturn(stmtUpdate);
            when(stmtUpdate.executeUpdate()).thenReturn(1);

            boolean result = authService.updateProfile(userId, newUsername);

            assertTrue(result);
            verify(stmtUpdate).setString(1, newUsername);
            verify(stmtUpdate).setString(2, userId.toString());
            verify(stmtUpdate).executeUpdate();
        }
    }

    @Test
    void testUpdateProfileFailNoRowsAffected() throws Exception {
        UUID userId = UUID.randomUUID();
        String newUsername = "valid_new_username";

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            Connection conn = mock(Connection.class);
            PreparedStatement stmtCount = mock(PreparedStatement.class);
            PreparedStatement stmtUpdate = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);

            mockedDBUtil.when(DBUtil::getConnection).thenReturn(conn);

            when(conn.prepareStatement(contains("SELECT COUNT(*) FROM user_account WHERE username = ? AND user_id != ?"))).thenReturn(stmtCount);
            when(stmtCount.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);
            when(rs.getInt(1)).thenReturn(0);

            when(conn.prepareStatement(contains("UPDATE user_account SET username"))).thenReturn(stmtUpdate);
            when(stmtUpdate.executeUpdate()).thenReturn(0);

            boolean result = authService.updateProfile(userId, newUsername);

            assertFalse(result);
            verify(stmtUpdate).setString(1, newUsername);
            verify(stmtUpdate).setString(2, userId.toString());
            verify(stmtUpdate).executeUpdate();
        }
    }

    @Test
    void testLogout() {
        try (MockedStatic<SessionManager> mockedSessionManager = mockStatic(SessionManager.class)) {
            authService.logout();
            mockedSessionManager.verify(SessionManager::clearSession);
        }
    }
}
