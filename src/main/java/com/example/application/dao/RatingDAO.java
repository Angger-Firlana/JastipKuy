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

    // Insert rating dengan struktur baru: rating ketepatan, rating pelayanan, dan deskripsi
    public boolean insertRating(Integer jastiperId, Integer userId, int ratingKetepatan, int ratingPelayanan, String deskripsi) {
        if (jastiperId == null || jastiperId == 0 || userId == null || userId == 0) return false;
        String sql = "INSERT INTO ratiing_user (idUser, rating_ketepatan, rating_pelayanan, deskripsi) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jastiperId);
            ps.setInt(2, ratingKetepatan);
            ps.setInt(3, ratingPelayanan);
            ps.setString(4, deskripsi);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("insertRating Error: " + e.getMessage());
            return false;
        }
    }
    
    /** Get all ratings for a specific jastiper */
    public List<RatingData> getRatingsByJastiper(Integer jastiperId) {
        List<RatingData> ratings = new ArrayList<>();
        String sql = "SELECT id, rating_ketepatan, rating_pelayanan, deskripsi FROM ratiing_user WHERE idUser = ? ORDER BY id DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jastiperId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RatingData rating = new RatingData(
                        jastiperId,
                        rs.getInt("id"),
                        rs.getInt("rating_ketepatan"),
                        rs.getInt("rating_pelayanan"),
                        rs.getString("deskripsi"),
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
    
    /** Check if a specific user has rated a specific jastiper */
    public boolean hasUserRatedJastiper(Integer userId, Integer jastiperId) {
        // Since ratiing_user table doesn't store who gave the rating,
        // we'll check if there's a rating for this jastiper
        // This is a workaround for the current database structure
        String sql = "SELECT COUNT(*) FROM ratiing_user WHERE idUser = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jastiperId);
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
    
    // Data class untuk rating dengan struktur baru
    public static class RatingData {
        public final Integer jastiperId; // The jastiper being rated
        public final Integer ratingId;
        public final Integer ratingKetepatan;
        public final Integer ratingPelayanan;
        public final String deskripsi;
        public final java.util.Date date;
        
        public RatingData(Integer jastiperId, Integer ratingId, Integer ratingKetepatan, Integer ratingPelayanan, String deskripsi, java.util.Date date) {
            this.jastiperId = jastiperId;
            this.ratingId = ratingId;
            this.ratingKetepatan = ratingKetepatan;
            this.ratingPelayanan = ratingPelayanan;
            this.deskripsi = deskripsi;
            this.date = date;
        }
        
        // Hitung overall rating (rata-rata dari ketepatan dan pelayanan)
        public double getOverallRating() {
            return (ratingKetepatan + ratingPelayanan) / 2.0;
        }
    }
}
