package servicesTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import services.SensorReadingService;
import utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SensorReadingServiceTest {

    private SensorReadingService sensorReadingService;

    @BeforeEach
    void setUp() {
        sensorReadingService = new SensorReadingService();
    }

    @Test
    void testInsertSensorReadingSuccess() throws Exception {
        // Siapkan data dummy
        UUID sensorId = UUID.randomUUID();
        float readingValue = 72.5f;
        Timestamp readingTime = new Timestamp(System.currentTimeMillis());

        // Mock Connection, PreparedStatement, dan DBUtil.getConnection()
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        // Mock DBUtil.getConnection() agar mengembalikan mockConnection
        try (MockedStatic<DBUtil> dbUtilMockedStatic = mockStatic(DBUtil.class)) {
            dbUtilMockedStatic.when(DBUtil::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

            // Mock executeUpdate() agar mengembalikan 1 (berhasil insert)
            when(mockStatement.executeUpdate()).thenReturn(1);

            // Panggil method yang akan diuji
            boolean result = sensorReadingService.insertSensorReading(sensorId, readingValue, readingTime);

            // Verifikasi bahwa query SQL dijalankan
            assertTrue(result);
            verify(mockStatement).setObject(1, sensorId);
            verify(mockStatement).setFloat(2, readingValue);
            verify(mockStatement).setTimestamp(3, readingTime);
            verify(mockStatement).executeUpdate();
        }
    }

    @Test
    void testInsertSensorReadingFailure() throws Exception {
        UUID sensorId = UUID.randomUUID();
        float readingValue = 90.0f;
        Timestamp readingTime = new Timestamp(System.currentTimeMillis());

        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        try (MockedStatic<DBUtil> dbUtilMockedStatic = mockStatic(DBUtil.class)) {
            dbUtilMockedStatic.when(DBUtil::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

            // Mock executeUpdate() agar mengembalikan 0 (gagal insert)
            when(mockStatement.executeUpdate()).thenReturn(0);

            boolean result = sensorReadingService.insertSensorReading(sensorId, readingValue, readingTime);

            assertFalse(result);
            verify(mockStatement).executeUpdate();
        }
    }

    @Test
    void testInsertSensorReadingSQLException() throws Exception {
        UUID sensorId = UUID.randomUUID();
        float readingValue = 100.0f;
        Timestamp readingTime = new Timestamp(System.currentTimeMillis());

        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        try (MockedStatic<DBUtil> dbUtilMockedStatic = mockStatic(DBUtil.class)) {
            dbUtilMockedStatic.when(DBUtil::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

            // Buat executeUpdate() lempar SQLException
            when(mockStatement.executeUpdate()).thenThrow(new SQLException("Database error"));

            boolean result = sensorReadingService.insertSensorReading(sensorId, readingValue, readingTime);

            assertFalse(result); // Seharusnya false karena exception
            verify(mockStatement).executeUpdate();
        }
    }
}
