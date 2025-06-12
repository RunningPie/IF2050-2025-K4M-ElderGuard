package servicesTest;

import models.EmergencyAlert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import services.EmergencyAlertService;
import utils.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmergencyAlertServiceTest {

    private EmergencyAlertService emergencyAlertService;

    @BeforeEach
    void setUp() {
        emergencyAlertService = new EmergencyAlertService();
    }

    @Test
    void testGetAllAlertsSuccess() throws Exception {
        EmergencyAlert sampleAlert = new EmergencyAlert(
                UUID.randomUUID().toString(),
                "HIGH",
                "John Doe",
                "FALL",
                "patient-123",
                "Room 101",
                LocalDateTime.now(),
                "PENDING",
                "doctor-456"
        );

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            Connection conn = mock(Connection.class);
            PreparedStatement stmt = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);

            mockedDBUtil.when(DBUtil::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);

            // Mock one row in the result set
            when(rs.next()).thenReturn(true, false);
            when(rs.getString("alert_id")).thenReturn(sampleAlert.getAlertId());
            when(rs.getString("priority")).thenReturn(sampleAlert.getPriority());
            when(rs.getString("patient_name")).thenReturn(sampleAlert.getPatientName());
            when(rs.getString("alert_type")).thenReturn(sampleAlert.getAlertType());
            when(rs.getString("patient_id")).thenReturn(sampleAlert.getPatientId());
            when(rs.getString("location")).thenReturn(sampleAlert.getLocation());
            when(rs.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(sampleAlert.getCreatedAt()));
            when(rs.getString("status")).thenReturn(sampleAlert.getStatus());
            when(rs.getString("assigned_to")).thenReturn(sampleAlert.getAssignedTo());

            List<EmergencyAlert> result = emergencyAlertService.getAllAlerts();

            assertNotNull(result);
            assertEquals(1, result.size());

            EmergencyAlert alert = result.get(0);
            assertEquals(sampleAlert.getAlertId(), alert.getAlertId());
            assertEquals(sampleAlert.getPriority(), alert.getPriority());
            assertEquals(sampleAlert.getPatientName(), alert.getPatientName());
            assertEquals(sampleAlert.getAlertType(), alert.getAlertType());
            assertEquals(sampleAlert.getPatientId(), alert.getPatientId());
            assertEquals(sampleAlert.getLocation(), alert.getLocation());
            assertEquals(sampleAlert.getCreatedAt(), alert.getCreatedAt());
            assertEquals(sampleAlert.getStatus(), alert.getStatus());
            assertEquals(sampleAlert.getAssignedTo(), alert.getAssignedTo());

            verify(stmt).executeQuery();
        }
    }

    @Test
    void testGetAllAlertsEmpty() throws Exception {
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            Connection conn = mock(Connection.class);
            PreparedStatement stmt = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);

            mockedDBUtil.when(DBUtil::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(false); // No data

            List<EmergencyAlert> result = emergencyAlertService.getAllAlerts();

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(stmt).executeQuery();
        }
    }

    @Test
    void testGetAllAlertsSQLException() throws Exception {
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenThrow(new SQLException("DB Error"));

            List<EmergencyAlert> result = emergencyAlertService.getAllAlerts();

            assertNotNull(result);
            assertTrue(result.isEmpty()); // Should return empty list
        }
    }

    @Test
    void testInsertAlertSuccess() throws Exception {
        EmergencyAlert alert = new EmergencyAlert(
                UUID.randomUUID().toString(),
                "MEDIUM",
                "Jane Doe",
                "HEART_RATE",
                "patient-789",
                "Room 202",
                LocalDateTime.now(),
                "NEW",
                "nurse-101"
        );

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            Connection conn = mock(Connection.class);
            PreparedStatement stmt = mock(PreparedStatement.class);

            mockedDBUtil.when(DBUtil::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeUpdate()).thenReturn(1); // Success

            boolean result = emergencyAlertService.insertAlert(alert);

            assertTrue(result);
            verify(stmt).setObject(1, UUID.fromString(alert.getAlertId()));
            verify(stmt).setString(2, alert.getPriority());
            verify(stmt).setString(3, alert.getPatientName());
            verify(stmt).setString(4, alert.getAlertType());
            verify(stmt).setString(5, alert.getPatientId());
            verify(stmt).setString(6, alert.getLocation());
            verify(stmt).setTimestamp(7, Timestamp.valueOf(alert.getCreatedAt()));
            verify(stmt).setString(8, alert.getStatus());
            verify(stmt).setString(9, alert.getAssignedTo());
            verify(stmt).executeUpdate();
        }
    }

    @Test
    void testInsertAlertFail() throws Exception {
        EmergencyAlert alert = new EmergencyAlert(
                UUID.randomUUID().toString(),
                "LOW",
                "Test User",
                "TEMP",
                "patient-999",
                "Room 303",
                LocalDateTime.now(),
                "NEW",
                "nurse-102"
        );

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            Connection conn = mock(Connection.class);
            PreparedStatement stmt = mock(PreparedStatement.class);

            mockedDBUtil.when(DBUtil::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeUpdate()).thenReturn(0); // Insert failed

            boolean result = emergencyAlertService.insertAlert(alert);

            assertFalse(result);
            verify(stmt).executeUpdate();
        }
    }

    @Test
    void testInsertAlertSQLException() throws Exception {
        EmergencyAlert alert = new EmergencyAlert(
                UUID.randomUUID().toString(),
                "LOW",
                "Error User",
                "TEMP",
                "patient-000",
                "Room 000",
                LocalDateTime.now(),
                "NEW",
                "nurse-000"
        );

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenThrow(new SQLException("Insert Error"));

            boolean result = emergencyAlertService.insertAlert(alert);

            assertFalse(result);
        }
    }
}