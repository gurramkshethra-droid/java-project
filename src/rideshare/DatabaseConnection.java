package rideshare;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static Connection conn;

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/ride_sharing",
                    "root",
                    "Ksh@#197518"
                );
            }
        } catch (Exception e) {
            System.out.println("❌ Database connection failed in DatabaseConnection.java");
            e.printStackTrace();
        }
        return conn;
    }
}