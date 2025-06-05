package pf.Database;

import java.sql.*;

public class DatabaseManager {
    // Cập nhật URL kết nối cho PostgreSQL
    private static final String URL = "jdbc:postgresql://34.67.92.57:5432/pfmsdemo";
    private static final String USER = "admin_psql";
    private static final String PASSWORD = "adminpsql";

    private static Connection connection;
    private static Statement statement;

    public static Connection getConnection() {
        return connection;
    }

    public static void connect() {
        try {
            // Đăng ký driver PostgreSQL
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the PostgreSQL database");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet executeQuery(String query) {
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
}
