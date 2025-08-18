package com.example.application.dao;

import com.example.application.connection.Conn;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
}
