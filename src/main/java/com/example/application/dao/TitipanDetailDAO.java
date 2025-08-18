package com.example.application.dao;

import com.example.application.connection.Conn;
import com.example.application.model.TitipanDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TitipanDetailDAO {
    private final Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    public TitipanDetailDAO(){
        conn = Conn.getConnection();
    }

    public void insertDetail(TitipanDetail detail) {
        String sql = "INSERT INTO titipan_detail (idTransaksi, deskripsi, catatan_opsional) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detail.getIdTransaksi());
            stmt.setString(2, detail.getDeskripsi());
            stmt.setString(3, detail.getCatatan_opsional());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<TitipanDetail> getDetailsByTransaksiId(int idTransaksi) {
        List<TitipanDetail> detailList = new ArrayList<>();
        String query = "SELECT * FROM titipan_detail WHERE idTransaksi = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idTransaksi);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TitipanDetail detail = new TitipanDetail();
                detail.setId(rs.getInt("id"));
                detail.setIdTransaksi(rs.getInt("idTransaksi"));
                detail.setDeskripsi(rs.getString("deskripsi"));
                detail.setCatatan_opsional(rs.getString("catatan_opsional"));
                detailList.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return detailList;
    }

    public boolean deleteByTitipanId(int titipanId) {
        String query = "DELETE FROM titipan_detail WHERE id_transaksi = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, titipanId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Delete TitipanDetail Error: " + e.getMessage());
        }
        return false;
    }

    public int countByTransaksiId(int idTransaksi) {
        String q = "SELECT COUNT(*) FROM titipan_detail WHERE idTransaksi = ?";
        try (PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setInt(1, idTransaksi);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("countByTransaksiId Error: " + e.getMessage());
        }
        return 0;
    }
}
