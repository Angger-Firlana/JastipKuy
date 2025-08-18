package com.example.application.dao;

import com.example.application.connection.Conn;
import com.example.application.model.Titipan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TitipanDAO {
    private final Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public TitipanDAO() {
        conn = Conn.getConnection();
    }

    /** Util: mapping 1 baris ResultSet -> Titipan (handle diambil_oleh NULL) */
    private Titipan mapRow(ResultSet rs) throws SQLException {
        Titipan t = new Titipan();
        t.setId(rs.getInt("id"));
        t.setUser_id(rs.getInt("user_id"));
        t.setHarga_estimasi(rs.getLong("harga_estimasi"));
        t.setStatus(rs.getString("status"));

        int dio = rs.getInt("diambil_oleh"); // jika kolom NULL, getInt -> 0
        t.setDiambil_oleh(rs.wasNull() ? null : dio);

        // created_at: pakai Timestamp -> java.util.Date
        Timestamp ts = rs.getTimestamp("created_at");
        t.setCreated_at(ts != null ? new java.util.Date(ts.getTime()) : null);
        return t;
    }

    public ArrayList<Titipan> getAllTitipan(String name) {
        ArrayList<Titipan> titipList = new ArrayList<>();
        String query = "SELECT * FROM titipan WHERE " +
                "user_id IN (SELECT id FROM users WHERE name LIKE ?) " +
                "ORDER BY created_at DESC";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            String keyword = "%" + name + "%";
            stmt.setString(1, keyword);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                titipList.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return titipList;
    }

    public int getOrderCountThisMonth() {
        String query = "SELECT COUNT(*) FROM titipan " +
                "WHERE MONTH(created_at) = MONTH(CURRENT_DATE()) " +
                "AND YEAR(created_at) = YEAR(CURRENT_DATE())";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException se) {
            System.out.println("getOrderCountThisMonth Error: " + se);
        }
        return 0;
    }

    public long getPendapatanThisMonth() {
        String query = "SELECT SUM(harga_estimasi) FROM titipan " +
                "WHERE status = 'SELESAI' " +
                "AND MONTH(created_at) = MONTH(CURRENT_DATE()) " +
                "AND YEAR(created_at) = YEAR(CURRENT_DATE())";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException se) {
            System.out.println("getPendapatanThisMonth Error: " + se);
        }
        return 0;
    }

    public int insertTitipanReturnId(Titipan t) {
        int id = -1;
        String sql = "INSERT INTO titipan (user_id, status, harga_estimasi, created_at, diambil_oleh) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, t.getUser_id());
            stmt.setString(2, t.getStatus());
            stmt.setLong(3, t.getHarga_estimasi());
            stmt.setTimestamp(4, new Timestamp(t.getCreated_at().getTime()));

            // HANDLE NULL untuk diambil_oleh
            if (t.getDiambil_oleh() == null || t.getDiambil_oleh() == 0) {
                stmt.setNull(5, Types.INTEGER);
            } else {
                stmt.setInt(5, t.getDiambil_oleh());
            }

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) id = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public boolean updateTitipan(Titipan data) {
        String query = "UPDATE titipan SET user_id=?, status=?, harga_estimasi=?, created_at=?, diambil_oleh=? WHERE id=?";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, data.getUser_id());
            ps.setString(2, data.getStatus());
            ps.setLong(3, data.getHarga_estimasi());
            ps.setTimestamp(4, new Timestamp(data.getCreated_at().getTime()));

            // HANDLE NULL untuk diambil_oleh
            if (data.getDiambil_oleh() == null || data.getDiambil_oleh() == 0) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, data.getDiambil_oleh());
            }

            ps.setInt(6, data.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update Titipan Error: " + e.getMessage());
        }
        return false;
    }

    public List<Titipan> getLastTitipan(int limit) {
        List<Titipan> list = new ArrayList<>();
        String sql = "SELECT * FROM titipan ORDER BY created_at DESC LIMIT ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("getLastTitipan Error: " + e);
        }
        return list;
    }

    /** 🔹 Tambahan: ambil pesanan aktif (MENUNGGU/DIPROSES) user terbaru */
    public Titipan getActiveByUser(int userId) {
        String sql = "SELECT * FROM titipan WHERE user_id=? AND status <> 'SELESAI' " +
                "ORDER BY created_at DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("getActiveByUser Error: " + e.getMessage());
        }
        return null;
    }

    /** 🔹 Tambahan: riwayat semua titipan user (terbaru di atas) */
    public List<Titipan> getHistoryByUser(int userId) {
        List<Titipan> list = new ArrayList<>();
        String sql = "SELECT * FROM titipan WHERE user_id=? ORDER BY created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("getHistoryByUser Error: " + e.getMessage());
        }
        return list;
    }
}
