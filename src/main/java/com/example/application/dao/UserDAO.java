package com.example.application.dao;

import com.example.application.connection.Conn;
import com.example.application.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class UserDAO {
    private final Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    public UserDAO(){
        conn = Conn.getConnection();
    }

    public String getUserNameById(int id) {
        String name = "-";
        String query = "SELECT name FROM users WHERE id = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }
        } catch (SQLException e) {
            System.out.println("getUserNameById Error: " + e);
        }
        return name;
    }




    public int getTotalUsers() {
        String query = "SELECT COUNT(*) FROM users";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException se) {
            System.out.println("getTotalUsers Error: " + se);
        }
        return 0;
    }



    public ArrayList<User> getAllUser(String nama){
        ArrayList<User> listUser = new ArrayList<User>();
        String query = "Select * from users where name LIKE ?";
        try{
            ps = conn.prepareStatement(query);
            ps.setString(1, nama+"%");
            rs =ps.executeQuery();
            while (rs.next()){
                User user = new User();
                user.setNisn(rs.getString("nisn"));
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setCreated_at(rs.getDate("created_at"));

                listUser.add(user);
            }
        }catch (SQLException se){
            System.out.println("Error : " + se);
        }

        return listUser;
    }

    public int login(String email, String password) {
        String query = "SELECT id, password FROM users WHERE email = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                String hashed = rs.getString("password");
                if (Objects.equals(password, hashed)) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException se) {
            System.out.println("Error: " + se);
        }
        return 0;
    }


    public User getUserById(int id) {
        User user = null;
        String query = "SELECT * FROM users WHERE id = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                User logUser = new User();
                logUser.setNisn(rs.getString("nisn")); // tambahin ini
                logUser.setRole(rs.getString("role"));
                logUser.setName(rs.getString("name"));
                logUser.setPassword(rs.getString("password"));
                logUser.setEmail(rs.getString("email"));
                logUser.setCreated_at(rs.getDate("created_at"));
                user = logUser;
            }
        } catch (SQLException se) {
            System.out.println("Error : " + se);
        }
        return user;
    }


    public boolean insert(String nisn, String name, String email, String password, String role) {
        String query = "INSERT INTO users (nisn, name, email, password, role, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, nisn);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, role);
            return ps.executeUpdate() > 0;
        } catch (SQLException se) {
            System.out.println("Insert Error: " + se);
        }
        return false;
    }

    public boolean update(int id, String nisn, String name, String email, String password, String role) {
        String query = "UPDATE users SET nisn = ?, name = ?, email = ?, password = ?, role = ? WHERE id = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, nisn);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, role);
            ps.setInt(6, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException se) {
            System.out.println("Update Error: " + se);
        }
        return false;
    }

    public boolean delete(int id) {
        String query = "DELETE FROM users WHERE id = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException se) {
            System.out.println("Delete Error: " + se);
        }
        return false;
    }

    public void register(User user) throws Exception {
        String sql = "INSERT INTO users (nisn, name, email, password, role) VALUES (?, ?, ?, ?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getNisn());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getRole());

            stmt.executeUpdate();
        }
    }

}
