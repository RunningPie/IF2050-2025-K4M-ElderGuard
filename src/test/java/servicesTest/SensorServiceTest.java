package servicesTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import services.SensorService;
import utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SensorServiceTest {

    private SensorService sensorService;

    @BeforeEach
    void setUp() {
        sensorService = new SensorService();
    }

    @Test
    void testGetSensorIdByTypeAndDeviceSuccess() throws Exception {
        String sensorType = "heart rate";
        UUID deviceId = UUID.randomUUID();
        UUID sensorId = UUID.randomUUID();

        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Mock DBUtil.getConnection() agar mengembalikan mockConnection
        try (MockedStatic<DBUtil> dbUtilMockedStatic = mockStatic(DBUtil.class)) {
            dbUtilMockedStatic.when(DBUtil::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            // Simulasi ada hasil dari database
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getString("sensor_id")).thenReturn(sensorId.toString());

            UUID result = sensorService.getSensorIdByTypeAndDevice(sensorType, deviceId);

            assertNotNull(result);
            assertEquals(sensorId, result);
            verify(mockStatement).setString(1, sensorType);
            verify(mockStatement).setString(2, deviceId.toString());
            verify(mockStatement).executeQuery();
        }
    }

    @Test
    void testGetSensorIdByTypeAndDeviceNoResult() throws Exception {
        String sensorType = "body temp";
        UUID deviceId = UUID.randomUUID();

        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        try (MockedStatic<DBUtil> dbUtilMockedStatic = mockStatic(DBUtil.class)) {
            dbUtilMockedStatic.when(DBUtil::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            // Simulasi hasilnya tidak ada
            when(mockResultSet.next()).thenReturn(false);

            UUID result = sensorService.getSensorIdByTypeAndDevice(sensorType, deviceId);

            assertNull(result);
            verify(mockStatement).executeQuery();
        }
    }

    @Test
    void testGetSensorIdByTypeAndDeviceSQLException() throws Exception {
        String sensorType = "blood pressure";
        UUID deviceId = UUID.randomUUID();

        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        try (MockedStatic<DBUtil> dbUtilMockedStatic = mockStatic(DBUtil.class)) {
            dbUtilMockedStatic.when(DBUtil::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

            // Simulasi lempar SQLException
            when(mockStatement.executeQuery()).thenThrow(new SQLException("Database error"));

            UUID result = sensorService.getSensorIdByTypeAndDevice(sensorType, deviceId);

            assertNull(result); // Seharusnya null karena exception
            verify(mockStatement).executeQuery();
        }
    }
}
