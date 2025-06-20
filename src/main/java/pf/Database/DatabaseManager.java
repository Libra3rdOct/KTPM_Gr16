package pf.Database;

import java.sql.*;

public class DatabaseManager {
    // ✅ Dùng MySQL JDBC URL
    private static final String URL = "jdbc:mysql://localhost:3306/ChuongNguyen";
    private static final String USER = "root";
    private static final String PASSWORD = "titansword";

    private static Connection connection;
    private static Statement statement;

    public static Connection getConnection() {
        return connection;
    }

    public static void connect() {
        try {
            // ✅ Dùng đúng MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to the MySQL database");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to MySQL. Check credentials or DB status.");
            e.printStackTrace();
        }
    }

    public static ResultSet executeQuery(String query) {
        ResultSet resultSet = null;
        try {
            if (connection == null) {
                System.err.println("❗ No database connection. Did you forget to call connect()?");
                return null;
            }
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
}
