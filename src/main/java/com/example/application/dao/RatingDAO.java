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

    // Insert rating dengan struktur baru: idUser = user yang kasih rating, idDriver = jastiper yang di-rating
    public boolean insertRating(Integer userId, Integer jastiperId, int ratingKetepatan, int ratingPelayanan, String deskripsi) {
        if (jastiperId == null || jastiperId == 0 || userId == null || userId == 0) return false;
        String sql = "INSERT INTO ratiing_user (idUser, idDriver, rating_ketepatan, rating_pelayanan, deskripsi) VALUES (?, ?, ?, ?, ?)";

        System.out.println("DEBUG: Inserting rating - UserID: " + userId + ", JastiperID: " + jastiperId +
                ", Ketepatan: " + ratingKetepatan + ", Pelayanan: " + ratingPelayanan);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);      // idUser = user yang kasih rating
            ps.setInt(2, jastiperId);  // idDriver = jastiper yang di-rating
            ps.setInt(3, ratingKetepatan);
            ps.setInt(4, ratingPelayanan);
            ps.setString(5, deskripsi);
            boolean result = ps.executeUpdate() > 0;
            System.out.println("DEBUG: Insert rating result: " + result);
            return result;
        } catch (SQLException e) {
            System.out.println("insertRating Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /** Get all ratings for a specific jastiper */
    public List<RatingData> getRatingsByJastiper(Integer jastiperId) {
        List<RatingData> ratings = new ArrayList<>();
        String sql = "SELECT id, idUser, rating_ketepatan, rating_pelayanan, deskripsi FROM ratiing_user WHERE idDriver = ? ORDER BY id DESC";

        System.out.println("DEBUG: getRatingsByJastiper called with jastiperId: " + jastiperId);
        System.out.println("DEBUG: Executing query: " + sql + " with parameter: " + jastiperId);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jastiperId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int ratingId = rs.getInt("id");
                    int userId = rs.getInt("idUser");
                    int ketepatan = rs.getInt("rating_ketepatan");
                    int pelayanan = rs.getInt("rating_pelayanan");
                    String deskripsi = rs.getString("deskripsi");

                    System.out.println("DEBUG: Raw data from DB - ID: " + ratingId +
                            ", UserID: " + userId +
                            ", Ketepatan: " + ketepatan +
                            ", Pelayanan: " + pelayanan +
                            ", Deskripsi: " + deskripsi);

                    RatingData rating = new RatingData(
                            userId,
                            jastiperId,
                            ratingId,
                            ketepatan,
                            pelayanan,
                            deskripsi,
                            new java.util.Date() // Since ratiing_user doesn't have created_at, use current date
                    );
                    ratings.add(rating);
                }
            }
        } catch (SQLException e) {
            System.out.println("getRatingsByJastiper Error: " + e.getMessage());
            e.printStackTrace();
        }

        // Debug: Print what we found
        System.out.println("DEBUG: Found " + ratings.size() + " ratings for jastiper " + jastiperId);
        for (RatingData rating : ratings) {
            System.out.println("DEBUG: Created RatingData - ID: " + rating.ratingId +
                    ", Ketepatan: " + rating.ratingKetepatan +
                    ", Pelayanan: " + rating.ratingPelayanan +
                    ", Deskripsi: " + rating.deskripsi);
        }

        return ratings;
    }

    /** Get ALL ratings from database (untuk debugging) */
    public List<RatingData> getAllRatings() {
        List<RatingData> ratings = new ArrayList<>();
        String sql = "SELECT id, idUser, idDriver, rating_ketepatan, rating_pelayanan, deskripsi FROM ratiing_user ORDER BY id DESC";

        System.out.println("DEBUG: getAllRatings called");
        System.out.println("DEBUG: Executing query: " + sql);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int ratingId = rs.getInt("id");
                    int userId = rs.getInt("idUser");
                    int jastiperId = rs.getInt("idDriver");
                    int ketepatan = rs.getInt("rating_ketepatan");
                    int pelayanan = rs.getInt("rating_pelayanan");
                    String deskripsi = rs.getString("deskripsi");

                    System.out.println("DEBUG: All ratings - ID: " + ratingId +
                            ", UserID: " + userId +
                            ", JastiperID: " + jastiperId +
                            ", Ketepatan: " + ketepatan +
                            ", Pelayanan: " + pelayanan +
                            ", Deskripsi: " + deskripsi);

                    RatingData rating = new RatingData(
                            userId,
                            jastiperId,
                            ratingId,
                            ketepatan,
                            pelayanan,
                            deskripsi,
                            new java.util.Date()
                    );
                    ratings.add(rating);
                }
            }
        } catch (SQLException e) {
            System.out.println("getAllRatings Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("DEBUG: Total ratings in database: " + ratings.size());
        return ratings;
    }

    /** Check if a specific user has rated a specific jastiper for a specific order */
    public boolean hasUserRatedJastiperForOrder(Integer userId, Integer jastiperId, Integer titipanId) {
        // Periksa rating berdasarkan prefix [ORDER#titipanId] di kolom deskripsi
        String sql = "SELECT COUNT(*) FROM ratiing_user WHERE idUser = ? AND idDriver = ? AND deskripsi LIKE ?";

        System.out.println("DEBUG: hasUserRatedJastiperForOrder - UserID: " + userId + ", JastiperID: " + jastiperId + ", TitipanID: " + titipanId);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, jastiperId);
            ps.setString(3, "[ORDER#" + titipanId + "]%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("DEBUG: Found " + count + " ratings from user " + userId + " to jastiper " + jastiperId + " for order " + titipanId);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("hasUserRatedJastiperForOrder Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /** Check if a specific user has rated a specific jastiper (legacy method) */
    public boolean hasUserRatedJastiper(Integer userId, Integer jastiperId) {
        return hasUserRatedJastiperForOrder(userId, jastiperId, null);
    }

    // Data class untuk rating dengan struktur baru
    public static class RatingData {
        public final Integer userId;      // The user who gave the rating
        public final Integer jastiperId;  // The jastiper being rated
        public final Integer ratingId;
        public final Integer ratingKetepatan;
        public final Integer ratingPelayanan;
        public final String deskripsi;
        public final java.util.Date date;

        public RatingData(Integer userId, Integer jastiperId, Integer ratingId, Integer ratingKetepatan, Integer ratingPelayanan, String deskripsi, java.util.Date date) {
            this.userId = userId;
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