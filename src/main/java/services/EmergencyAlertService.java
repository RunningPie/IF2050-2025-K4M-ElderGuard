package services;

import models.EmergencyAlert;
import utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EmergencyAlertService {
    public List<EmergencyAlert> getAllAlerts() {
        List<EmergencyAlert> alerts = new ArrayList<>();
        String sql = "SELECT alert_id, priority, patient_name, alert_type, patient_id, location, created_at, status, assigned_to FROM emergency_alert ORDER BY created_at DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                EmergencyAlert alert = new EmergencyAlert(
                        rs.getString("alert_id"),
                        rs.getString("priority"),
                        rs.getString("patient_name"),
                        rs.getString("alert_type"),
                        rs.getString("patient_id"),
                        rs.getString("location"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("status"),
                        rs.getString("assigned_to")
                );
                alerts.add(alert);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching emergency alerts: " + e.getMessage());
            e.printStackTrace();
        }

        return alerts;
    }

    public boolean insertAlert(EmergencyAlert alert) {
        String sql = """
            INSERT INTO emergency_alert (alert_id, priority, patient_name, alert_type, patient_id, location, created_at, status, assigned_to)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, UUID.fromString(alert.getAlertId()));
            stmt.setString(2, alert.getPriority());
            stmt.setString(3, alert.getPatientName());
            stmt.setString(4, alert.getAlertType());
            stmt.setString(5, alert.getPatientId());
            stmt.setString(6, alert.getLocation());
            stmt.setTimestamp(7, Timestamp.valueOf(alert.getCreatedAt()));
            stmt.setString(8, alert.getStatus());
            stmt.setString(9, alert.getAssignedTo());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting alert: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
