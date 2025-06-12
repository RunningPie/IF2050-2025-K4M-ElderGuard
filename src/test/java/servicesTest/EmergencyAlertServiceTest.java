package servicesTest;

import models.EmergencyAlert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import services.EmergencyAlertService;
import utils.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmergencyAlertServiceTest {

    // Mock objek Connection untuk mengisolasi tes dari database sungguhan
    @Mock
    private Connection mockConnection;
    // Mock objek PreparedStatement
    @Mock
    private PreparedStatement mockPreparedStatement;
    // Mock objek ResultSet
    @Mock
    private ResultSet mockResultSet;

    // Instance dari service yang akan diuji
    private EmergencyAlertService emergencyAlertService;

    @BeforeEach
    void setUp() throws SQLException {
        // Menginisialisasi mock objek sebelum setiap tes
        MockitoAnnotations.openMocks(this);
        // Menginisialisasi EmergencyAlertService
        emergencyAlertService = new EmergencyAlertService();

        // Mengatur perilaku DBUtil.getConnection() untuk mengembalikan mockConnection
        // Ini penting karena DBUtil.getConnection() adalah static method
        // Kita menggunakan try-with-resources untuk memastikan mock dibersihkan setelah digunakan
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            // Ini hanya untuk memastikan mockConnection digunakan dalam setup,
            // penggunaan sebenarnya akan terjadi di method tes
        }
    }

    @Test
    void getAllAlerts_shouldReturnListOfAlerts() throws SQLException {
        // Deskripsi: Menguji fungsi getAllAlerts() untuk memastikan ia mengembalikan daftar EmergencyAlert
        // dan berinteraksi dengan database dengan benar.

        // Data dummy untuk mock ResultSet
        String alertId1 = UUID.randomUUID().toString();
        String alertId2 = UUID.randomUUID().toString();
        UUID patientId1 = UUID.randomUUID();
        UUID patientId2 = UUID.randomUUID();
        LocalDateTime createdAt1 = LocalDateTime.now().minusHours(1);
        LocalDateTime createdAt2 = LocalDateTime.now();

        // Mengatur perilaku mockConnection: saat prepareStatement dipanggil, kembalikan mockPreparedStatement
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        // Mengatur perilaku mockPreparedStatement: saat executeQuery dipanggil, kembalikan mockResultSet
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Mengatur perilaku mockResultSet:
        // Panggilan pertama next() mengembalikan true (ada baris pertama)
        when(mockResultSet.next())
                .thenReturn(true) // Untuk baris pertama
                .thenReturn(true) // Untuk baris kedua
                .thenReturn(false); // Tidak ada baris lagi

        // Mengatur data untuk baris pertama
        when(mockResultSet.getString("alert_id"))
                .thenReturn(alertId1)
                .thenReturn(alertId2);
        when(mockResultSet.getString("priority"))
                .thenReturn("High")
                .thenReturn("Medium");
        when(mockResultSet.getString("patient_name"))
                .thenReturn("John Doe")
                .thenReturn("Jane Smith");
        when(mockResultSet.getString("alert_type"))
                .thenReturn("Cardiac Arrest")
                .thenReturn("Fall Detection");
        // Karena patient_id adalah UUID, kita perlu mock getObject dan cast ke UUID
        when(mockResultSet.getObject("patient_id"))
                .thenReturn(patientId1)
                .thenReturn(patientId2);
        when(mockResultSet.getString("location"))
                .thenReturn("Room 101")
                .thenReturn("Lobby");
        when(mockResultSet.getTimestamp("created_at"))
                .thenReturn(Timestamp.valueOf(createdAt1))
                .thenReturn(Timestamp.valueOf(createdAt2));
        when(mockResultSet.getString("status"))
                .thenReturn("Pending")
                .thenReturn("Acknowledged");
        when(mockResultSet.getString("assigned_to"))
                .thenReturn("Dr. Alex")
                .thenReturn("Nurse Budi");

        // Memanggil metode yang diuji
        List<EmergencyAlert> alerts = null;
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            alerts = emergencyAlertService.getAllAlerts();
        }

        // Memverifikasi interaksi
        // Memastikan getConnection dipanggil sekali
        verify(mockConnection).prepareStatement("SELECT alert_id, priority, patient_name, alert_type, patient_id, location, created_at, status, assigned_to FROM emergency_alert ORDER BY created_at DESC");
        // Memastikan executeQuery dipanggil sekali
        verify(mockPreparedStatement).executeQuery();
        // Memastikan next() dipanggil 3 kali (dua kali true, satu kali false)
        verify(mockResultSet, times(3)).next();
        // Memastikan ResultSet ditutup
        verify(mockResultSet).close();
        // Memastikan PreparedStatement ditutup
        verify(mockPreparedStatement).close();
        // Memastikan Connection ditutup
        verify(mockConnection).close();

        // Memverifikasi hasil
        assertNotNull(alerts); // Memastikan daftar tidak null
        assertEquals(2, alerts.size()); // Memastikan ada 2 alert

        // Memverifikasi data alert pertama
        EmergencyAlert alert1 = alerts.get(0);
        assertEquals(alertId1, alert1.getAlertId());
        assertEquals("High", alert1.getPriority());
        assertEquals("John Doe", alert1.getPatientName());
        assertEquals("Cardiac Arrest", alert1.getAlertType());
        assertEquals(patientId1, alert1.getPatientId());
        assertEquals("Room 101", alert1.getLocation());
        assertEquals(createdAt1, alert1.getCreatedAt());
        assertEquals("Pending", alert1.getStatus());
        assertEquals("Dr. Alex", alert1.getAssignedTo());

        // Memverifikasi data alert kedua
        EmergencyAlert alert2 = alerts.get(1);
        assertEquals(alertId2, alert2.getAlertId());
        assertEquals("Medium", alert2.getPriority());
        assertEquals("Jane Smith", alert2.getPatientName());
        assertEquals("Fall Detection", alert2.getAlertType());
        assertEquals(patientId2, alert2.getPatientId());
        assertEquals("Lobby", alert2.getLocation());
        assertEquals(createdAt2, alert2.getCreatedAt());
        assertEquals("Acknowledged", alert2.getStatus());
        assertEquals("Nurse Budi", alert2.getAssignedTo());
    }

    @Test
    void getAllAlerts_shouldReturnEmptyListOnError() throws SQLException {
        // Deskripsi: Menguji fungsi getAllAlerts() ketika terjadi SQLException saat fetching data.
        // Seharusnya mengembalikan daftar kosong dan mencetak error.

        // Mengatur perilaku mockConnection untuk melempar SQLException saat prepareStatement dipanggil
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database connection error"));

        List<EmergencyAlert> alerts = null;
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            alerts = emergencyAlertService.getAllAlerts();
        }

        // Memverifikasi interaksi
        // Memastikan getConnection dipanggil sekali
        verify(mockConnection).prepareStatement(anyString());
        // Memastikan tidak ada interaksi dengan executeQuery atau ResultSet
        verifyNoMoreInteractions(mockPreparedStatement);
        verifyNoMoreInteractions(mockResultSet);
        // Memastikan Connection ditutup meskipun ada error
        verify(mockConnection).close();

        // Memverifikasi hasil
        assertNotNull(alerts); // Memastikan daftar tidak null
        assertTrue(alerts.isEmpty()); // Memastikan daftar kosong
    }

    @Test
    void insertAlert_shouldReturnTrueOnSuccessfulInsert() throws SQLException {
        // Deskripsi: Menguji fungsi insertAlert() untuk memastikan ia mengembalikan true
        // ketika operasi insert berhasil dilakukan ke database.

        // Data alert yang akan disisipkan
        EmergencyAlert alert = new EmergencyAlert(
                UUID.randomUUID().toString(),
                "Critical",
                "Patient XYZ",
                "Stroke",
                UUID.randomUUID(),
                "ER",
                LocalDateTime.now(),
                "New",
                "Dr. Cinta"
        );

        // Mengatur perilaku mockConnection: saat prepareStatement dipanggil, kembalikan mockPreparedStatement
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        // Mengatur perilaku mockPreparedStatement: saat executeUpdate dipanggil, kembalikan 1 (menandakan 1 baris terpengaruh)
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Memanggil metode yang diuji
        boolean result = false;
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            result = emergencyAlertService.insertAlert(alert);
        }

        // Memverifikasi interaksi
        // Memastikan prepareStatement dipanggil dengan query insert yang benar
        verify(mockConnection).prepareStatement(anyString());
        // Memastikan semua setter dipanggil dengan nilai yang benar
        verify(mockPreparedStatement).setObject(eq(1), any(UUID.class)); // alert_id
        verify(mockPreparedStatement).setString(2, alert.getPriority());
        verify(mockPreparedStatement).setString(3, alert.getPatientName());
        verify(mockPreparedStatement).setString(4, alert.getAlertType());
        verify(mockPreparedStatement).setObject(eq(5), eq(alert.getPatientId()), eq(java.sql.Types.OTHER));
        verify(mockPreparedStatement).setString(6, alert.getLocation());
        verify(mockPreparedStatement).setTimestamp(eq(7), any(Timestamp.class)); // created_at
        verify(mockPreparedStatement).setString(8, alert.getStatus());
        verify(mockPreparedStatement).setString(9, alert.getAssignedTo());
        // Memastikan executeUpdate dipanggil sekali
        verify(mockPreparedStatement).executeUpdate();
        // Memastikan PreparedStatement ditutup
        verify(mockPreparedStatement).close();
        // Memastikan Connection ditutup
        verify(mockConnection).close();

        // Memverifikasi hasil
        assertTrue(result); // Memastikan insert berhasil
    }

    @Test
    void insertAlert_shouldReturnFalseOnFailedInsert() throws SQLException {
        // Deskripsi: Menguji fungsi insertAlert() untuk memastikan ia mengembalikan false
        // ketika operasi insert gagal (misalnya, executeUpdate mengembalikan 0).

        // Data alert yang akan disisipkan
        EmergencyAlert alert = new EmergencyAlert(
                UUID.randomUUID().toString(),
                "Low",
                "Patient ABC",
                "Headache",
                UUID.randomUUID(),
                "Clinic",
                LocalDateTime.now(),
                "Closed",
                "Nurse Dita"
        );

        // Mengatur perilaku mockConnection: saat prepareStatement dipanggil, kembalikan mockPreparedStatement
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        // Mengatur perilaku mockPreparedStatement: saat executeUpdate dipanggil, kembalikan 0 (menandakan tidak ada baris terpengaruh)
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        // Memanggil metode yang diuji
        boolean result = false;
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            result = emergencyAlertService.insertAlert(alert);
        }

        // Memverifikasi interaksi (mirip dengan tes berhasil, hanya hasil executeUpdate yang berbeda)
        verify(mockConnection).prepareStatement(anyString());
        verify(mockPreparedStatement).setObject(eq(1), any(UUID.class));
        verify(mockPreparedStatement).setString(2, alert.getPriority());
        verify(mockPreparedStatement).setString(3, alert.getPatientName());
        verify(mockPreparedStatement).setString(4, alert.getAlertType());
        verify(mockPreparedStatement).setObject(eq(5), eq(alert.getPatientId()), eq(java.sql.Types.OTHER));
        verify(mockPreparedStatement).setString(6, alert.getLocation());
        verify(mockPreparedStatement).setTimestamp(eq(7), any(Timestamp.class));
        verify(mockPreparedStatement).setString(8, alert.getStatus());
        verify(mockPreparedStatement).setString(9, alert.getAssignedTo());
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();

        // Memverifikasi hasil
        assertFalse(result); // Memastikan insert gagal
    }

    @Test
    void insertAlert_shouldReturnFalseOnError() throws SQLException {
        // Deskripsi: Menguji fungsi insertAlert() untuk memastikan ia mengembalikan false
        // ketika terjadi SQLException selama proses insert.

        // Data alert yang akan disisipkan
        EmergencyAlert alert = new EmergencyAlert(
                UUID.randomUUID().toString(),
                "High",
                "Patient DEF",
                "Seizure",
                UUID.randomUUID(),
                "ICU",
                LocalDateTime.now(),
                "Active",
                "Dr. Yoga"
        );

        // Mengatur perilaku mockConnection untuk melempar SQLException saat prepareStatement dipanggil
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database write error"));

        // Memanggil metode yang diuji
        boolean result = false;
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            result = emergencyAlertService.insertAlert(alert);
        }

        // Memverifikasi interaksi
        // Memastikan prepareStatement dipanggil
        verify(mockConnection).prepareStatement(anyString());
        // Memastikan tidak ada interaksi dengan executeUpdate atau setter lainnya karena error terjadi lebih awal
        verifyNoMoreInteractions(mockPreparedStatement);
        // Memastikan Connection ditutup meskipun ada error
        verify(mockConnection).close();

        // Memverifikasi hasil
        assertFalse(result); // Memastikan insert gagal
    }
}