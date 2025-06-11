package services;

import models.WearableDevice;
import utils.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeviceService {

    // CREATE
    public boolean addDevice(WearableDevice device) {
        String sql = "INSERT INTO wearable_device (device_id, model, battery_level, latitude, longitude, lansia_id, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, device.getDeviceId());
            stmt.setString(2, device.getModel());
            stmt.setFloat(3, device.getBatteryLevel());
            stmt.setFloat(4, device.getLatitude());
            stmt.setFloat(5, device.getLongitude());
            stmt.setObject(6, device.getLansiaId());
            stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now())); // or device.getCreatedAt()

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding device: " + e.getMessage());
            return false;
        }
    }

    // READ (by ID)
    public WearableDevice getDeviceById(UUID deviceId) {
        String sql = "SELECT * FROM wearable_device WHERE device_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, deviceId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new WearableDevice(
                        rs.getObject("device_id", UUID.class),
                        rs.getString("model"),
                        rs.getFloat("battery_level"),
                        rs.getFloat("latitude"),
                        rs.getFloat("longitude"),
                        rs.getObject("lansia_id", UUID.class),
                        new ArrayList<>(), // sensors not handled here
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching device by ID: " + e.getMessage());
        }
        return null;
    }

    // UPDATE
    public boolean updateDevice(WearableDevice device) {
        String sql = "UPDATE wearable_device SET model = ?, battery_level = ?, latitude = ?, longitude = ?, lansia_id = ? " +
                "WHERE device_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, device.getModel());
            stmt.setFloat(2, device.getBatteryLevel());
            stmt.setFloat(3, device.getLatitude());
            stmt.setFloat(4, device.getLongitude());
            stmt.setObject(5, device.getLansiaId());
            stmt.setObject(6, device.getDeviceId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating device: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean deleteDevice(UUID deviceId) {
        String sql = "DELETE FROM wearable_device WHERE device_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, deviceId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting device: " + e.getMessage());
            return false;
        }
    }
}
