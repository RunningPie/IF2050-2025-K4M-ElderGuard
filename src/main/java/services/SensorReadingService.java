package services;

import utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

public class SensorReadingService {

    /**
     * Insert a new sensor reading into the database
     */
    public boolean insertSensorReading(UUID sensorId, float readingValue, Timestamp readingTime) {
        String sql = """
            INSERT INTO sensor_readings (id, sensor_id, reading_value, reading_at)
            VALUES (gen_random_uuid(), ?, ?, ?)
            """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, sensorId);
            stmt.setFloat(2, readingValue);
            stmt.setTimestamp(3, readingTime);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting sensor reading: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
