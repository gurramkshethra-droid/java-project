package rideshare;

import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class RiderDashboard extends JFrame {
    private JTextField pickupField, dropoffField;
    private JButton bookBtn, checkStatusBtn, completeRideBtn, logoutBtn;
    private User rider;

    // Store OTPs temporarily in memory
    public static Map<Integer, String> otpStore = new HashMap<>();

    public RiderDashboard(User rider) {
        this.rider = rider;

        setTitle("Rider Dashboard - RideShare");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        BackgroundPanel bgPanel = new BackgroundPanel("src/rideshare/images/5543.jpg");
        setContentPane(bgPanel);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(255, 255, 255, 210));
        content.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        content.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Hey " + rider.getName() + " 👋", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel pickupLbl = new JLabel("📍 Pickup Location:");
        pickupLbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));

        JLabel dropoffLbl = new JLabel("🏁 Dropoff Location:");
        dropoffLbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));

        pickupField = new JTextField(15);
        pickupField.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        pickupField.setMaximumSize(new Dimension(300, 35));

        dropoffField = new JTextField(15);
        dropoffField.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        dropoffField.setMaximumSize(new Dimension(300, 35));

        content.add(title);
        content.add(Box.createVerticalStrut(20));
        content.add(pickupLbl);
        content.add(pickupField);
        content.add(Box.createVerticalStrut(10));
        content.add(dropoffLbl);
        content.add(dropoffField);
        content.add(Box.createVerticalStrut(20));

        bookBtn = createStyledButton("🚗 Book Ride", new Color(144, 238, 144));
        checkStatusBtn = createStyledButton("🔍 Check Status", new Color(173, 216, 230));
        completeRideBtn = createStyledButton("✅ Complete Ride", new Color(255, 228, 181));
        logoutBtn = createStyledButton("🚪 Logout", new Color(255, 182, 193));

        content.add(bookBtn);
        content.add(Box.createVerticalStrut(10));
        content.add(checkStatusBtn);
        content.add(Box.createVerticalStrut(10));
        content.add(completeRideBtn);
        content.add(Box.createVerticalStrut(10));
        content.add(logoutBtn);

        bgPanel.setLayout(new GridBagLayout());
        bgPanel.add(content);

        bookBtn.addActionListener(e -> bookRide());
        checkStatusBtn.addActionListener(e -> checkRideStatus());
        completeRideBtn.addActionListener(e -> completeRide());
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(240, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setForeground(Color.DARK_GRAY);
        return btn;
    }

    private void bookRide() {
        String pickup = pickupField.getText().trim();
        String dropoff = dropoffField.getText().trim();

        if (pickup.isEmpty() || dropoff.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Please fill both pickup and dropoff!");
            return;
        }

        double fare = 30 + Math.abs(pickup.length() - dropoff.length()) * 10;
        int otp = (int) (Math.random() * 9000) + 1000;

        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO rides (rider_id, pickup_location, dropoff_location, fare, status) VALUES (?, ?, ?, ?, 'requested')";
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, rider.getId());
            ps.setString(2, pickup);
            ps.setString(3, dropoff);
            ps.setDouble(4, fare);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int rideId = rs.getInt(1);
                    otpStore.put(rideId, String.valueOf(otp));
                }
                JOptionPane.showMessageDialog(this,
                        "✅ Ride booked successfully!\n\n💰 Fare: ₹" + fare + "\n🔢 Your OTP: " + otp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkRideStatus() {
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, status, fare FROM rides WHERE rider_id = ? ORDER BY id DESC LIMIT 1";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, rider.getId());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int rideId = rs.getInt("id");
                String otp = otpStore.getOrDefault(rideId, "N/A");
                JOptionPane.showMessageDialog(this,
                        "🆔 Ride ID: " + rideId +
                                "\n💰 Fare: ₹" + rs.getDouble("fare") +
                                "\n📶 Status: " + rs.getString("status") +
                                "\n🔢 OTP: " + otp);
            } else {
                JOptionPane.showMessageDialog(this, "❌ No rides found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void completeRide() {
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "UPDATE rides SET status='completed' WHERE rider_id=? AND status IN ('accepted','in progress') ORDER BY id DESC LIMIT 1";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, rider.getId());
            int rows = ps.executeUpdate();
            if (rows > 0)
                JOptionPane.showMessageDialog(this, "✅ Ride completed successfully!");
            else
                JOptionPane.showMessageDialog(this, "⚠️ No ongoing rides!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}