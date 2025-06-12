package servicesTest;

import models.WearableDevice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import services.WearableDeviceService;
import utils.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WearableDeviceServiceTest {

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
    private WearableDeviceService wearableDeviceService;

    @BeforeEach
    void setUp() throws SQLException {
        // Menginisialisasi mock objek sebelum setiap tes
        MockitoAnnotations.openMocks(this);
        // Menginisialisasi WearableDeviceService
        wearableDeviceService = new WearableDeviceService();

        // Mengatur perilaku DBUtil.getConnection() untuk mengembalikan mockConnection
        // Ini penting karena DBUtil.getConnection() adalah static method
        // Kita menggunakan try-with-resources untuk memastikan mock dibersihkan setelah digunakan
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            // Ini hanya untuk memastikan mockConnection digunakan dalam setup,
            // penggunaan sebenarnya akan terjadi di method tes
        }
    }


   //  ## Unit Test untuk `addDevice`

    @Test
    void addDevice_shouldReturnTrueOnSuccessfulInsert() throws SQLException {
        // Deskripsi: Menguji fungsi addDevice() untuk memastikan ia mengembalikan true
        // ketika operasi insert berhasil dilakukan ke database.

        // Data device yang akan ditambahkan
        UUID deviceId = UUID.randomUUID();
        UUID lansiaId = UUID.randomUUID();
        WearableDevice device = new WearableDevice(
                deviceId,
                "SmartWatch X",
                85.5f,
                -6.123f,
                106.456f,
                lansiaId,
                new ArrayList<>(),
                LocalDateTime.now()
        );

        // Mengatur perilaku mockConnection: saat prepareStatement dipanggil, kembalikan mockPreparedStatement
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        // Mengatur perilaku mockPreparedStatement: saat executeUpdate dipanggil, kembalikan 1 (menandakan 1 baris terpengaruh)
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Memanggil metode yang diuji
        boolean result = false;
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            result = wearableDeviceService.addDevice(device);
        }

        // Memverifikasi interaksi
        // Memastikan prepareStatement dipanggil dengan query insert yang benar
        verify(mockConnection).prepareStatement(anyString());
        // Memastikan semua setter dipanggil dengan nilai yang benar
        verify(mockPreparedStatement).setObject(1, device.getDeviceId());
        verify(mockPreparedStatement).setString(2, device.getModel());
        verify(mockPreparedStatement).setFloat(3, device.getBatteryLevel());
        verify(mockPreparedStatement).setFloat(4, device.getLatitude());
        verify(mockPreparedStatement).setFloat(5, device.getLongitude());
        verify(mockPreparedStatement).setObject(6, device.getLansiaId());
        verify(mockPreparedStatement).setTimestamp(eq(7), any(Timestamp.class)); // created_at
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
    void addDevice_shouldReturnFalseOnFailedInsert() throws SQLException {
        // Deskripsi: Menguji fungsi addDevice() untuk memastikan ia mengembalikan false
        // ketika operasi insert gagal (misalnya, executeUpdate mengembalikan 0).

        // Data device yang akan ditambahkan
        UUID deviceId = UUID.randomUUID();
        UUID lansiaId = UUID.randomUUID();
        WearableDevice device = new WearableDevice(
                deviceId,
                "Band 2",
                60.0f,
                -7.5f,
                112.0f,
                lansiaId,
                new ArrayList<>(),
                LocalDateTime.now()
        );

        // Mengatur perilaku mockConnection: saat prepareStatement dipanggil, kembalikan mockPreparedStatement
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        // Mengatur perilaku mockPreparedStatement: saat executeUpdate dipanggil, kembalikan 0 (menandakan tidak ada baris terpengaruh)
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        // Memanggil metode yang diuji
        boolean result = false;
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            result = wearableDeviceService.addDevice(device);
        }

        // Memverifikasi interaksi (mirip dengan tes berhasil, hanya hasil executeUpdate yang berbeda)
        verify(mockConnection).prepareStatement(anyString());
        verify(mockPreparedStatement).setObject(1, device.getDeviceId());
        verify(mockPreparedStatement).setString(2, device.getModel());
        verify(mockPreparedStatement).setFloat(3, device.getBatteryLevel());
        verify(mockPreparedStatement).setFloat(4, device.getLatitude());
        verify(mockPreparedStatement).setFloat(5, device.getLongitude());
        verify(mockPreparedStatement).setObject(6, device.getLansiaId());
        verify(mockPreparedStatement).setTimestamp(eq(7), any(Timestamp.class));
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();

        // Memverifikasi hasil
        assertFalse(result); // Memastikan insert gagal
    }

    @Test
    void addDevice_shouldReturnFalseOnError() throws SQLException {
        // Deskripsi: Menguji fungsi addDevice() untuk memastikan ia mengembalikan false
        // ketika terjadi SQLException selama proses insert.

        // Data device yang akan ditambahkan
        UUID deviceId = UUID.randomUUID();
        UUID lansiaId = UUID.randomUUID();
        WearableDevice device = new WearableDevice(
                deviceId,
                "SmartWatch Y",
                70.0f,
                -6.5f,
                107.0f,
                lansiaId,
                new ArrayList<>(),
                LocalDateTime.now()
        );

        // Mengatur perilaku mockConnection untuk melempar SQLException saat prepareStatement dipanggil
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database write error"));

        // Memanggil metode yang diuji
        boolean result = false;
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            result = wearableDeviceService.addDevice(device);
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

    @Test
    void getDeviceById_shouldReturnNullOnError() throws SQLException {
        // Deskripsi: Menguji fungsi getDeviceById() untuk memastikan ia mengembalikan null
        // ketika terjadi SQLException saat fetching data.

        UUID deviceId = UUID.randomUUID();

        // Mengatur perilaku mockConnection untuk melempar SQLException saat prepareStatement dipanggil
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database read error"));

        // Memanggil metode yang diuji
        WearableDevice foundDevice = null;
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            foundDevice = wearableDeviceService.getDeviceById(deviceId);
        }

        // Memverifikasi interaksi
        verify(mockConnection).prepareStatement(anyString());
        // Memastikan tidak ada interaksi dengan executeQuery atau ResultSet
        verifyNoMoreInteractions(mockPreparedStatement);
        verifyNoMoreInteractions(mockResultSet);
        // Memastikan Connection ditutup meskipun ada error
        verify(mockConnection).close();

        // Memverifikasi hasil
        assertNull(foundDevice); // Seharusnya null karena error
    }

    @Test
    void getAllDevices_shouldReturnEmptyListOnError() throws SQLException {
        // Deskripsi: Menguji fungsi getAllDevices() ketika terjadi SQLException saat fetching data.
        // Seharusnya mengembalikan daftar kosong dan mencetak error.

        // Mengatur perilaku mockConnection untuk melempar SQLException saat prepareStatement dipanggil
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        List<WearableDevice> devices = null;
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            devices = wearableDeviceService.getAllDevices();
        }

        // Memverifikasi interaksi
        verify(mockConnection).prepareStatement(anyString());
        verifyNoMoreInteractions(mockPreparedStatement);
        verifyNoMoreInteractions(mockResultSet);
        verify(mockConnection).close();

        // Memverifikasi hasil
        assertNotNull(devices);
        assertTrue(devices.isEmpty());
    }

    // ## Unit Test untuk `updateDevice`

    @Test
    void updateDevice_shouldReturnTrueOnSuccessfulUpdate() throws SQLException {
        // Deskripsi: Menguji fungsi updateDevice() untuk memastikan ia mengembalikan true
        // ketika operasi update berhasil dilakukan ke database.

        // Data device yang akan diupdate
        UUID deviceId = UUID.randomUUID();
        UUID lansiaId = UUID.randomUUID();
        WearableDevice device = new WearableDevice(
                deviceId,
                "Updated Model",
                50.0f,
                -8.0f,
                108.0f,
                lansiaId,
                new ArrayList<>(),
                LocalDateTime.now().minusDays(10)
        );

        // Mengatur perilaku mockConnection dan mockPreparedStatement
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // 1 baris terpengaruh

        // Memanggil metode yang diuji
        boolean result = false;
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            result = wearableDeviceService.updateDevice(device);
        }

        // Memverifikasi interaksi
        verify(mockConnection).prepareStatement(anyString());
        verify(mockPreparedStatement).setString(1, device.getModel());
        verify(mockPreparedStatement).setFloat(2, device.getBatteryLevel());
        verify(mockPreparedStatement).setFloat(3, device.getLatitude());
        verify(mockPreparedStatement).setFloat(4, device.getLongitude());
        verify(mockPreparedStatement).setObject(5, device.getLansiaId());
        verify(mockPreparedStatement).setObject(6, device.getDeviceId());
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();

        // Memverifikasi hasil
        assertTrue(result);
    }

    @Test
    void updateDevice_shouldReturnFalseOnFailedUpdate() throws SQLException {
        // Deskripsi: Menguji fungsi updateDevice() untuk memastikan ia mengembalikan false
        // ketika operasi update gagal (misalnya, executeUpdate mengembalikan 0).

        // Data device
        UUID deviceId = UUID.randomUUID();
        UUID lansiaId = UUID.randomUUID();
        WearableDevice device = new WearableDevice(
                deviceId,
                "Model Gagal",
                10.0f,
                -9.0f,
                109.0f,
                lansiaId,
                new ArrayList<>(),
                LocalDateTime.now().minusDays(20)
        );

        // Mengatur perilaku mockConnection dan mockPreparedStatement
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0); // 0 baris terpengaruh

        // Memanggil metode yang diuji
        boolean result = false;
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            result = wearableDeviceService.updateDevice(device);
        }

        // Memverifikasi interaksi
        verify(mockConnection).prepareStatement(anyString());
        verify(mockPreparedStatement).setString(1, device.getModel());
        verify(mockPreparedStatement).setFloat(2, device.getBatteryLevel());
        verify(mockPreparedStatement).setFloat(3, device.getLatitude());
        verify(mockPreparedStatement).setFloat(4, device.getLongitude());
        verify(mockPreparedStatement).setObject(5, device.getLansiaId());
        verify(mockPreparedStatement).setObject(6, device.getDeviceId());
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();

        // Memverifikasi hasil
        assertFalse(result);
    }

    @Test
    void updateDevice_shouldReturnFalseOnError() throws SQLException {
        // Deskripsi: Menguji fungsi updateDevice() untuk memastikan ia mengembalikan false
        // ketika terjadi SQLException selama proses update.

        // Data device
        UUID deviceId = UUID.randomUUID();
        UUID lansiaId = UUID.randomUUID();
        WearableDevice device = new WearableDevice(
                deviceId,
                "Model Error",
                30.0f,
                -10.0f,
                110.0f,
                lansiaId,
                new ArrayList<>(),
                LocalDateTime.now().minusDays(30)
        );

        // Mengatur perilaku mockConnection untuk melempar SQLException saat prepareStatement dipanggil
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database update error"));

        // Memanggil metode yang diuji
        boolean result = false;
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            result = wearableDeviceService.updateDevice(device);
        }

        // Memverifikasi interaksi
        verify(mockConnection).prepareStatement(anyString());
        verifyNoMoreInteractions(mockPreparedStatement);
        verifyNoMoreInteractions(mockResultSet);
        verify(mockConnection).close();

        // Memverifikasi hasil
        assertFalse(result);
    }

    //## Unit Test untuk `deleteDevice`

    @Test
    void deleteDevice_shouldReturnTrueOnSuccessfulDelete() throws SQLException {
        // Deskripsi: Menguji fungsi deleteDevice() untuk memastikan ia mengembalikan true
        // ketika operasi delete berhasil dilakukan ke database.

        UUID deviceId = UUID.randomUUID();

        // Mengatur perilaku mockConnection dan mockPreparedStatement
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // 1 baris terpengaruh

        // Memanggil metode yang diuji
        boolean result = false;
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            result = wearableDeviceService.deleteDevice(deviceId);
        }

        // Memverifikasi interaksi
        verify(mockConnection).prepareStatement(anyString());
        verify(mockPreparedStatement).setObject(1, deviceId);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();

        // Memverifikasi hasil
        assertTrue(result);
    }

    @Test
    void deleteDevice_shouldReturnFalseOnFailedDelete() throws SQLException {
        // Deskripsi: Menguji fungsi deleteDevice() untuk memastikan ia mengembalikan false
        // ketika operasi delete gagal (misalnya, executeUpdate mengembalikan 0).

        UUID deviceId = UUID.randomUUID();

        // Mengatur perilaku mockConnection dan mockPreparedStatement
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0); // 0 baris terpengaruh

        // Memanggil metode yang diuji
        boolean result = false;
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            result = wearableDeviceService.deleteDevice(deviceId);
        }

        // Memverifikasi interaksi
        verify(mockConnection).prepareStatement(anyString());
        verify(mockPreparedStatement).setObject(1, deviceId);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();

        // Memverifikasi hasil
        assertFalse(result);
    }

    @Test
    void deleteDevice_shouldReturnFalseOnError() throws SQLException {
        // Deskripsi: Menguji fungsi deleteDevice() untuk memastikan ia mengembalikan false
        // ketika terjadi SQLException selama proses delete.

        UUID deviceId = UUID.randomUUID();

        // Mengatur perilaku mockConnection untuk melempar SQLException saat prepareStatement dipanggil
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database delete error"));

        // Memanggil metode yang diuji
        boolean result = false;
        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            result = wearableDeviceService.deleteDevice(deviceId);
        }

        // Memverifikasi interaksi
        verify(mockConnection).prepareStatement(anyString());
        verifyNoMoreInteractions(mockPreparedStatement);
        verifyNoMoreInteractions(mockResultSet);
        verify(mockConnection).close();

        // Memverifikasi hasil
        assertFalse(result);
    }
}