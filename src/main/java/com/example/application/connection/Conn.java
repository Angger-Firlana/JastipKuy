package com.example.application.connection;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conn {
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection != null) {
            return connection;
        } else {
            String dbUrl = "jdbc:mysql://localhost:3306/jastip?user=root&password";
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(dbUrl);
                System.out.println("Koneksi sukses");

            } catch (Exception e) {
                System.out.println(e.toString());
            }
            return connection;
        }
    }

    public static void main(String[] args) {
        getConnection();
    }
}


