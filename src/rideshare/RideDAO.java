package rideshare;

import java.sql.*;

public class RideDAO {
    
    public boolean bookRide(int riderId, String pickupLocation, String dropoffLocation, double fare) {
        boolean status = false;
        Connection con = null;
        
        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false); // Start transaction
            
            String sql = "INSERT INTO rides(rider_id, pickup_location, dropoff_location, fare, status) VALUES (?, ?, ?, ?, 'requested')";
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, riderId);
            ps.setString(2, pickupLocation);
            ps.setString(3, dropoffLocation);
            ps.setDouble(4, fare);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                status = true;
                con.commit(); // Commit transaction
            } else {
                con.rollback(); // Rollback if failed
            }

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    public boolean acceptRide(int rideId, int driverId) {
        boolean status = false;
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "UPDATE rides SET driver_id = ?, status = 'accepted' WHERE id = ? AND status = 'requested'";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, driverId);
            ps.setInt(2, rideId);
            int rows = ps.executeUpdate();
            status = rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public boolean startRide(int rideId) {
        boolean status = false;
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "UPDATE rides SET status = 'ongoing' WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, rideId);
            int rows = ps.executeUpdate();
            status = rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public boolean completeRide(int rideId) {
        boolean status = false;
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "UPDATE rides SET status = 'completed' WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, rideId);
            int rows = ps.executeUpdate();
            status = rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public String getRideStatus(int rideId) {
        String status = null;
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT status FROM rides WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, rideId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                status = rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public boolean updateOTP(int rideId, String otp) {
        boolean status = false;
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "UPDATE rides SET otp = ? WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, otp);
            ps.setInt(2, rideId);
            int rows = ps.executeUpdate();
            status = rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public String getOTP(int rideId) {
        String otp = null;
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT otp FROM rides WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, rideId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                otp = rs.getString("otp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return otp;
    }
}