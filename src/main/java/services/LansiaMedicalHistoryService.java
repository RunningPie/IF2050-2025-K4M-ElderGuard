package services;

import models.LansiaMedicalHistory;
import utils.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LansiaMedicalHistoryService {

    private Connection getConnection() throws SQLException {
        return DBUtil.getConnection();
    }

    /**
     * Mengambil semua riwayat medis dari database Supabase
     */
    public List<LansiaMedicalHistory> getAllHistories() {
        List<LansiaMedicalHistory> historyList = new ArrayList<>();

        if (!DBUtil.isDatabaseAvailable()) {
            System.err.println("Database is not available");
            return historyList;
        }

        String sql = "SELECT history_id, user_id, medical_condition, diagnosis_date, description, severity " +
                "FROM lansia_medical_history ORDER BY diagnosis_date DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                LansiaMedicalHistory history = new LansiaMedicalHistory();
                history.setHistoryId(UUID.fromString(rs.getString("history_id")));
                history.setUserId(UUID.fromString(rs.getString("user_id")));
                history.setMedicalCondition(rs.getString("medical_condition"));
                history.setDiagnosisDate(rs.getTimestamp("diagnosis_date"));
                history.setDescription(rs.getString("description"));

                // Fix untuk severity - convert String ke Enum dengan null check
                String severityStr = rs.getString("severity");
                if (severityStr != null && !severityStr.trim().isEmpty()) {
                    try {
                        history.setSeverity(LansiaMedicalHistory.Severity.valueOf(severityStr.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid severity value: " + severityStr + ". Setting to LOW as default.");
                        history.setSeverity(LansiaMedicalHistory.Severity.LOW);
                    }
                } else {
                    history.setSeverity(LansiaMedicalHistory.Severity.LOW); // Default value
                }

                historyList.add(history);
            }

        } catch (SQLException e) {
            System.err.println("Error getting all histories: " + e.getMessage());
            e.printStackTrace();
        }

        return historyList;
    }

    /**
     * Menambah riwayat medis baru ke database Supabase
     */
    public boolean addHistory(LansiaMedicalHistory history) {
        if (!DBUtil.isDatabaseAvailable()) {
            System.err.println("Database is not available, cannot add history");
            return false;
        }

        String sql = "INSERT INTO lansia_medical_history (history_id, user_id, medical_condition, diagnosis_date, description, severity) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (history.getHistoryId() == null) {
                history.setHistoryId(UUID.randomUUID());
            }

            stmt.setObject(1, history.getHistoryId());
            stmt.setObject(2, history.getUserId());
            stmt.setString(3, history.getMedicalCondition());
            stmt.setTimestamp(4, history.getDiagnosisDate());
            stmt.setString(5, history.getDescription());
            stmt.setString(6, history.getSeverity() != null ? history.getSeverity().name() : "LOW");

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding history: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mengupdate riwayat medis di database Supabase
     */
    public boolean updateHistory(LansiaMedicalHistory updatedHistory) {
        if (!DBUtil.isDatabaseAvailable()) {
            System.err.println("Database is not available, cannot update history");
            return false;
        }

        String sql = "UPDATE lansia_medical_history SET " +
                "user_id = ?, medical_condition = ?, diagnosis_date = ?, description = ?, severity = ? " +
                "WHERE history_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, updatedHistory.getUserId());
            stmt.setString(2, updatedHistory.getMedicalCondition());
            stmt.setTimestamp(3, updatedHistory.getDiagnosisDate());
            stmt.setString(4, updatedHistory.getDescription());
            stmt.setString(5, updatedHistory.getSeverity() != null ? updatedHistory.getSeverity().name() : "LOW");
            stmt.setObject(6, updatedHistory.getHistoryId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating history: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Menghapus riwayat medis dari database Supabase berdasarkan ID
     */
    public boolean removeHistory(UUID historyId) {
        if (!DBUtil.isDatabaseAvailable()) {
            System.err.println("Database is not available, cannot remove history");
            return false;
        }

        String sql = "DELETE FROM lansia_medical_history WHERE history_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, historyId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error removing history: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mengambil riwayat medis berdasarkan user ID
     */
    public List<LansiaMedicalHistory> getHistoriesByUserId(UUID userId) {
        List<LansiaMedicalHistory> historyList = new ArrayList<>();
        String sql = "SELECT history_id, user_id, medical_condition, diagnosis_date, description, severity " +
                "FROM lansia_medical_history WHERE user_id = ? ORDER BY diagnosis_date DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LansiaMedicalHistory history = new LansiaMedicalHistory();
                    history.setHistoryId(UUID.fromString(rs.getString("history_id")));
                    history.setUserId(UUID.fromString(rs.getString("user_id")));
                    history.setMedicalCondition(rs.getString("medical_condition"));
                    history.setDiagnosisDate(rs.getTimestamp("diagnosis_date"));
                    history.setDescription(rs.getString("description"));

                    // untuk severity - convert String ke Enum dengan null check
                    String severityStr = rs.getString("severity");
                    if (severityStr != null && !severityStr.trim().isEmpty()) {
                        try {
                            history.setSeverity(LansiaMedicalHistory.Severity.valueOf(severityStr.toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid severity value: " + severityStr + ". Setting to LOW as default.");
                            history.setSeverity(LansiaMedicalHistory.Severity.LOW);
                        }
                    } else {
                        history.setSeverity(LansiaMedicalHistory.Severity.LOW); // Default value
                    }

                    historyList.add(history);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting histories by user ID: " + e.getMessage());
            e.printStackTrace();
        }

        return historyList;
    }

    /**
     * Mengambil riwayat medis berdasarkan tingkat keparahan
     */
    public List<LansiaMedicalHistory> getHistoriesBySeverity(String severity) {
        List<LansiaMedicalHistory> historyList = new ArrayList<>();
        String sql = "SELECT history_id, user_id, medical_condition, diagnosis_date, description, severity " +
                "FROM lansia_medical_history WHERE severity = ? ORDER BY diagnosis_date DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, severity);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LansiaMedicalHistory history = new LansiaMedicalHistory();
                    history.setHistoryId(UUID.fromString(rs.getString("history_id")));
                    history.setUserId(UUID.fromString(rs.getString("user_id")));
                    history.setMedicalCondition(rs.getString("medical_condition"));
                    history.setDiagnosisDate(rs.getTimestamp("diagnosis_date"));
                    history.setDescription(rs.getString("description"));

                    // untuk severity - convert String ke Enum dengan null check
                    String severityValue = rs.getString("severity");
                    if (severityValue != null && !severityValue.trim().isEmpty()) {
                        try {
                            history.setSeverity(LansiaMedicalHistory.Severity.valueOf(severityValue.toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid severity value: " + severityValue + ". Setting to LOW as default.");
                            history.setSeverity(LansiaMedicalHistory.Severity.LOW);
                        }
                    } else {
                        history.setSeverity(LansiaMedicalHistory.Severity.LOW); // Default value
                    }

                    historyList.add(history);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting histories by severity: " + e.getMessage());
            e.printStackTrace();
        }

        return historyList;
    }

    /**
     * Mengambil riwayat medis berdasarkan ID
     */
    public LansiaMedicalHistory getHistoryById(UUID historyId) {
        String sql = "SELECT history_id, user_id, medical_condition, diagnosis_date, description, severity " +
                "FROM lansia_medical_history WHERE history_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, historyId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LansiaMedicalHistory history = new LansiaMedicalHistory();
                    history.setHistoryId(UUID.fromString(rs.getString("history_id")));
                    history.setUserId(UUID.fromString(rs.getString("user_id")));
                    history.setMedicalCondition(rs.getString("medical_condition"));
                    history.setDiagnosisDate(rs.getTimestamp("diagnosis_date"));
                    history.setDescription(rs.getString("description"));

                    String severityStr = rs.getString("severity");
                    if (severityStr != null && !severityStr.trim().isEmpty()) {
                        try {
                            history.setSeverity(LansiaMedicalHistory.Severity.valueOf(severityStr.toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid severity value: " + severityStr + ". Setting to LOW as default.");
                            history.setSeverity(LansiaMedicalHistory.Severity.LOW);
                        }
                    } else {
                        history.setSeverity(LansiaMedicalHistory.Severity.LOW); // Default value
                    }

                    return history;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting history by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}