package com.example.application.dao;

import com.example.application.connection.Conn;
import com.example.application.model.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocationDAO {
    private final Connection conn;

    public LocationDAO() {
        this.conn = Conn.getConnection();
    }

    public List<Location> findAll() {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT id, name, coordinate_x, coordinate_y FROM locations ORDER BY name";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                locations.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("findAll locations error: " + e.getMessage());
        }
        return locations;
    }

    public Optional<Location> findByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        String sql = "SELECT id, name, coordinate_x, coordinate_y FROM locations WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("findByName location error: " + e.getMessage());
        }
        return Optional.empty();
    }

    private Location mapRow(ResultSet rs) throws SQLException {
        Location location = new Location();
        location.setId(rs.getInt("id"));
        location.setName(rs.getString("name"));
        location.setCoordinateX(rs.getInt("coordinate_x"));
        location.setCoordinateY(rs.getInt("coordinate_y"));
        return location;
    }
}
