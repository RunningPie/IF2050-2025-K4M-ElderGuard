package services;

import utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SensorService {

    /**
     * Fetch sensor ID by sensor type and device ID
     */
    public UUID getSensorIdByTypeAndDevice(String sensorType, UUID deviceId) {
        String sql = "SELECT sensor_id FROM sensor WHERE type = ? AND device_id = ?::uuid";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sensorType);
            stmt.setString(2, deviceId.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return UUID.fromString(rs.getString("sensor_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching sensor by type and device: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
