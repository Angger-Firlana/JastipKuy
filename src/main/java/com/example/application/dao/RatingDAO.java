package com.example.application.dao;

import com.example.application.connection.Conn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RatingDAO {
    private final Connection conn;
    public RatingDAO() { conn = Conn.getConnection(); }

    // driverId = idUser (kolom di ratiing_user), rate = 0..10
    public boolean insertRating(Integer driverId, int rate) {
        if (driverId == null || driverId == 0) return false;
        String sql = "INSERT INTO ratiing_user (idUser, rate) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverId);
            ps.setInt(2, rate); // DECIMAL(10,0) aman di-setInt
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("insertRating Error: " + e.getMessage());
            return false;
        }
    }
}
