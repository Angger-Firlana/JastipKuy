package com.example.application.dao;

import com.example.application.connection.Conn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LaporanDAO {
    private final Connection conn;
    public LaporanDAO() { conn = Conn.getConnection(); }

    // idPenitip = user yang kasih rating (yang login), idPengambil = driver
    public boolean insertLaporan(int idPenitip, Integer idPengambil, String teks) {
        if (idPengambil == null || idPengambil == 0) return false;
        String sql = "INSERT INTO laporan (id_penitip, id_pengambil, laporan) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPenitip);
            ps.setInt(2, idPengambil);
            ps.setString(3, (teks == null ? "" : teks.trim()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("insertLaporan Error: " + e.getMessage());
            return false;
        }
    }
    
    /** 🔹 Tambahan: ambil komentar dari user tertentu untuk jastiper tertentu */
    public String getCommentByUserAndJastiper(Integer userId, Integer jastiperId) {
        String sql = "SELECT laporan FROM laporan WHERE id_penitip = ? AND id_pengambil = ? ORDER BY id DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, jastiperId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("laporan");
                }
            }
        } catch (SQLException e) {
            System.out.println("getCommentByUserAndJastiper Error: " + e.getMessage());
        }
        return null;
    }
    
    /** 🔹 Tambahan: check apakah user sudah comment jastiper tertentu */
    public boolean hasUserCommentedJastiper(Integer userId, Integer jastiperId) {
        String sql = "SELECT COUNT(*) FROM laporan WHERE id_penitip = ? AND id_pengambil = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, jastiperId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("hasUserCommentedJastiper Error: " + e.getMessage());
        }
        return false;
    }
}
