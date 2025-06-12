package servicesTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import services.DeviceService;
import utils.DBUtil;

import java.sql.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeviceServiceTest {

    private DeviceService deviceService;

    @BeforeEach
    void setUp() {
        deviceService = new DeviceService();
    }

    @Test
    void testGetDeviceIdByLansiaIdSuccess() throws Exception {
        UUID lansiaId = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            Connection conn = mock(Connection.class);
            PreparedStatement stmt = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);

            mockedDBUtil.when(DBUtil::getConnection).thenReturn(conn);

            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(true);
            when(rs.getString("device_id")).thenReturn(deviceId.toString());

            UUID result = deviceService.getDeviceIdByLansiaId(lansiaId);

            assertNotNull(result);
            assertEquals(deviceId, result);

            verify(stmt).setString(1, lansiaId.toString());
            verify(stmt).executeQuery();
        }
    }

    @Test
    void testGetDeviceIdByLansiaIdNotFound() throws Exception {
        UUID lansiaId = UUID.randomUUID();

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            Connection conn = mock(Connection.class);
            PreparedStatement stmt = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);

            mockedDBUtil.when(DBUtil::getConnection).thenReturn(conn);

            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(false);

            UUID result = deviceService.getDeviceIdByLansiaId(lansiaId);

            assertNull(result);
            verify(stmt).setString(1, lansiaId.toString());
            verify(stmt).executeQuery();
        }
    }

    @Test
    void testGetDeviceIdByLansiaIdSQLException() throws Exception {
        UUID lansiaId = UUID.randomUUID();

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenThrow(new SQLException("DB Error"));

            UUID result = deviceService.getDeviceIdByLansiaId(lansiaId);

            assertNull(result);
        }
    }

    @Test
    void testGetDeviceIdByModelSuccess() throws Exception {
        String model = "SmartWatch X";
        UUID deviceId = UUID.randomUUID();

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            Connection conn = mock(Connection.class);
            PreparedStatement stmt = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);

            mockedDBUtil.when(DBUtil::getConnection).thenReturn(conn);

            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(true);
            when(rs.getString("device_id")).thenReturn(deviceId.toString());

            UUID result = deviceService.getDeviceIdByModel(model);

            assertNotNull(result);
            assertEquals(deviceId, result);

            verify(stmt).setString(1, model);
            verify(stmt).executeQuery();
        }
    }

    @Test
    void testGetDeviceIdByModelNotFound() throws Exception {
        String model = "Unknown";

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            Connection conn = mock(Connection.class);
            PreparedStatement stmt = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);

            mockedDBUtil.when(DBUtil::getConnection).thenReturn(conn);

            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(false);

            UUID result = deviceService.getDeviceIdByModel(model);

            assertNull(result);
            verify(stmt).setString(1, model);
            verify(stmt).executeQuery();
        }
    }

    @Test
    void testGetDeviceIdByModelSQLException() throws Exception {
        String model = "ErrorModel";

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenThrow(new SQLException("DB Error"));

            UUID result = deviceService.getDeviceIdByModel(model);

            assertNull(result);
        }
    }
}
