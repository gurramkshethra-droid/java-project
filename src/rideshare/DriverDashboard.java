package rideshare;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DriverDashboard extends JFrame {
    private JTable rideTable;
    private JButton refreshBtn, acceptBtn, startRideBtn, logoutBtn;
    private User driver;
    private DefaultTableModel model;

    public DriverDashboard(User driver) {
        this.driver = driver;

        setTitle("Driver Dashboard - RideShare");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        BackgroundPanel bgPanel = new BackgroundPanel("src/rideshare/images/5544.jpg");
        setContentPane(bgPanel);
        bgPanel.setLayout(new GridBagLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Welcome Driver " + driver.getName() + " 🚗", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(title);
        contentPanel.add(Box.createVerticalStrut(20));

        model = new DefaultTableModel(new String[]{"Ride ID", "Pickup", "Dropoff", "Fare", "Status"}, 0);
        rideTable = new JTable(model);
        rideTable.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        rideTable.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(rideTable);
        scrollPane.setPreferredSize(new Dimension(700, 350));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(15));

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);

        refreshBtn = new JButton("🔄 Refresh");
        acceptBtn = new JButton("✅ Accept Ride");
        startRideBtn = new JButton("🚦 Start Ride (OTP)");
        logoutBtn = new JButton("🚪 Logout");

        styleButton(refreshBtn, new Color(173, 216, 230));
        styleButton(acceptBtn, new Color(144, 238, 144));
        styleButton(startRideBtn, new Color(255, 255, 153));
        styleButton(logoutBtn, new Color(255, 182, 193));

        btnPanel.add(refreshBtn);
        btnPanel.add(acceptBtn);
        btnPanel.add(startRideBtn);
        btnPanel.add(logoutBtn);

        contentPanel.add(btnPanel);
        bgPanel.add(contentPanel);

        refreshBtn.addActionListener(e -> loadRides());
        acceptBtn.addActionListener(e -> acceptRide());
        startRideBtn.addActionListener(e -> startRide());
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        loadRides();
        setVisible(true);
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void loadRides() {
        model.setRowCount(0);
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM rides WHERE status IN ('requested','accepted','in progress')";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("pickup_location"),
                        rs.getString("dropoff_location"),
                        rs.getDouble("fare"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void acceptRide() {
        int selectedRow = rideTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select a ride to accept!");
            return;
        }

        int rideId = (int) model.getValueAt(selectedRow, 0);
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "UPDATE rides SET driver_id = ?, status = 'accepted' WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, driver.getId());
            ps.setInt(2, rideId);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Ride accepted successfully!");
                loadRides();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void startRide() {
        int selectedRow = rideTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select a ride first!");
            return;
        }

        int rideId = (int) model.getValueAt(selectedRow, 0);
        String otpEntered = JOptionPane.showInputDialog(this, "🔢 Enter OTP to start ride:");

        String correctOtp = RiderDashboard.otpStore.get(rideId);
        if (correctOtp == null) {
            JOptionPane.showMessageDialog(this, "❌ OTP not found! Ask rider to rebook.");
            return;
        }

        if (otpEntered != null && otpEntered.equals(correctOtp)) {
            try (Connection con = DatabaseConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("UPDATE rides SET status='in progress' WHERE id=?");
                ps.setInt(1, rideId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "✅ OTP Verified! Ride started!");
                RiderDashboard.otpStore.remove(rideId);
                loadRides();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "❌ Invalid OTP! Try again!");
        }
    }
}