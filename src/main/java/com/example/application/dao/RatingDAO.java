package com.example.application.dao;

import com.example.application.connection.Conn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingDAO {
    private final Connection conn;
    public RatingDAO() { conn = Conn.getConnection(); }

    // Insert rating: driverId = jastiper yang di-rating, rate = 1-10
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
    
    /** Get all ratings for a specific jastiper */
    public List<RatingData> getRatingsByJastiper(Integer jastiperId) {
        List<RatingData> ratings = new ArrayList<>();
        String sql = "SELECT idUser, rate FROM ratiing_user WHERE idUser = ? ORDER BY id DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jastiperId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Get titipan ID from titipan table where diambil_oleh = jastiperId
                    Integer titipanId = getTitipanIdByJastiper(jastiperId);
                    
                    RatingData rating = new RatingData(
                        jastiperId, // This is the jastiper being rated
                        titipanId,
                        rs.getInt("rate"),
                        new java.util.Date() // Since ratiing_user doesn't have created_at, use current date
                    );
                    ratings.add(rating);
                }
            }
        } catch (SQLException e) {
            System.out.println("getRatingsByJastiper Error: " + e.getMessage());
        }
        return ratings;
    }
    
    private Integer getTitipanIdByJastiper(Integer jastiperId) {
        String sql = "SELECT id FROM titipan WHERE diambil_oleh = ? AND status = 'SELESAI' ORDER BY created_at DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jastiperId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.out.println("getTitipanIdByJastiper Error: " + e.getMessage());
        }
        return null;
    }
    
    /** Check if a specific user has rated a specific jastiper */
    public boolean hasUserRatedJastiper(Integer userId, Integer jastiperId) {
        // Since ratiing_user table doesn't store who gave the rating,
        // we'll check if there's a laporan (comment) from this user to this jastiper
        // This is a workaround for the current database structure
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
            System.out.println("hasUserRatedJastiper Error: " + e.getMessage());
        }
        return false;
    }
    
    // Data class untuk rating
    public static class RatingData {
        public final Integer jastiperId; // The jastiper being rated
        public final Integer titipanId;
        public final Integer rating;
        public final java.util.Date date;
        
        public RatingData(Integer jastiperId, Integer titipanId, Integer rating, java.util.Date date) {
            this.jastiperId = jastiperId;
            this.titipanId = titipanId;
            this.rating = rating;
            this.date = date;
        }
    }
}
