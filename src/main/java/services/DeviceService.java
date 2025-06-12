package services;

import utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DeviceService {

    /**
     * Fetch device ID by Lansia ID
     */
    public UUID getDeviceIdByLansiaId(UUID lansiaId) {
        String sql = "SELECT device_id FROM wearable_device WHERE lansia_id = ?::uuid";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, lansiaId.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return UUID.fromString(rs.getString("device_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching device by lansiaId: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fetch device ID by model name
     */
    public UUID getDeviceIdByModel(String model) {
        String sql = "SELECT device_id FROM wearable_device WHERE model = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, model);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return UUID.fromString(rs.getString("device_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching device by model: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
