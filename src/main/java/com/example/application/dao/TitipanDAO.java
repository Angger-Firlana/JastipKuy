package com.example.application.dao;

import com.example.application.connection.Conn;
import com.example.application.model.Titipan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

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
        
        // Handle harga_estimasi as BigDecimal from database
        BigDecimal harga = rs.getBigDecimal("harga_estimasi");
        t.setHarga_estimasi(harga != null ? harga.longValue() : 0L);
        
        t.setStatus(rs.getString("status"));

        // Fix: set nama_barang, lokasi_jemput, lokasi_antar
        t.setNama_barang(rs.getString("nama_barang"));
        t.setLokasi_jemput(rs.getString("lokasi_jemput"));
        t.setLokasi_antar(rs.getString("lokasi_antar"));

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

    public ArrayList<Titipan> getAllTitipanJastiper(String name) {
        ArrayList<Titipan> titipList = new ArrayList<>();
        String query = "SELECT * FROM titipan WHERE " +
                "user_id IN (SELECT id FROM users WHERE name LIKE ?) and status = 'MENUNGGU'" +
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
            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal(1);
                return result != null ? result.longValue() : 0L;
            }
        } catch (SQLException se) {
            System.out.println("getPendapatanThisMonth Error: " + se);
        }
        return 0;
    }

    public int insertTitipanReturnId(Titipan t) {
        int id = -1;
        String sql = "INSERT INTO titipan (user_id, nama_barang, status, harga_estimasi, created_at, diambil_oleh, lokasi_jemput, lokasi_antar) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, t.getUser_id());
            stmt.setString(2, t.getNama_barang());
            stmt.setString(3, t.getStatus());
            stmt.setBigDecimal(4, new java.math.BigDecimal(t.getHarga_estimasi()));
            stmt.setTimestamp(5, new Timestamp(t.getCreated_at().getTime()));

            // HANDLE NULL untuk diambil_oleh
            if (t.getDiambil_oleh() == null || t.getDiambil_oleh() == 0) {
                stmt.setNull(6, Types.INTEGER);
            } else {
                stmt.setInt(6, t.getDiambil_oleh());
            }
            // Set lokasi_jemput dan lokasi_antar
            stmt.setString(7, t.getLokasi_jemput());
            stmt.setString(8, t.getLokasi_antar());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) id = rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error inserting titipan: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error inserting titipan: " + e.getMessage());
            e.printStackTrace();
        }
        return id;
    }

    public boolean updateTitipan(Titipan data) {
        boolean status = false;
        StringBuilder query = new StringBuilder("UPDATE titipan SET ");
        List<Object> values = new ArrayList<>();
        List<Integer> types = new ArrayList<>();

        if (data.getUser_id() != null && data.getUser_id() != 0) {
            query.append("user_id=?, ");
            values.add(data.getUser_id());
            types.add(Types.INTEGER);
        }

        if (data.getStatus() != null) {
            query.append("status=?, ");
            values.add(data.getStatus());
            types.add(Types.VARCHAR);
        }

        if (data.getHarga_estimasi() != null && data.getHarga_estimasi() != 0) {
            query.append("harga_estimasi=?, ");
            values.add(new BigDecimal(data.getHarga_estimasi()));
            types.add(Types.DECIMAL);
        }

        if (data.getCreated_at() != null) {
            query.append("created_at=?, ");
            values.add(new Timestamp(data.getCreated_at().getTime()));
            types.add(Types.TIMESTAMP);
        }

        if (data.getDiambil_oleh() != null && data.getDiambil_oleh() != 0) {
            query.append("diambil_oleh=?, ");
            values.add(data.getDiambil_oleh());
            types.add(Types.INTEGER);
        }

        // hapus koma terakhir
        if (query.toString().endsWith(", ")) {
            query.setLength(query.length() - 2);
        }

        query.append(" WHERE id=?");
        values.add(data.getId());
        types.add(Types.INTEGER);

        try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < values.size(); i++) {
                if (values.get(i) == null) {
                    ps.setNull(i + 1, types.get(i));
                } else {
                    ps.setObject(i + 1, values.get(i));
                }
            }
            status = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update Titipan Error: " + e.getMessage());
        }
        return status;
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

    /** 🔹 Tambahan: ambil semua pesanan yang diambil oleh jastiper tertentu */
    public List<Titipan> getOrdersByJastiper(int jastiperId) {
        List<Titipan> list = new ArrayList<>();
        String sql = "SELECT * FROM titipan WHERE diambil_oleh=? ORDER BY created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jastiperId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("getOrdersByJastiper Error: " + e.getMessage());
        }
        return list;
    }
}
