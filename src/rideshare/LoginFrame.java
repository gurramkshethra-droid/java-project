package rideshare;

import java.awt.*;
import javax.swing.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JButton loginBtn, registerBtn;

    public LoginFrame() {
        setTitle("Login - RideShare");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Background image panel
        BackgroundPanel bgPanel = new BackgroundPanel("src/rideshare/images/app_bg.jpg");
        setContentPane(bgPanel);
        bgPanel.setLayout(new GridBagLayout());

        // Main login box panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        panel.setMaximumSize(new Dimension(420, 360));

        // ✨ Transparent background
        panel.setBackground(new Color(255, 255, 255, 150)); // white with 150 alpha
        panel.setOpaque(true);

        JLabel title = new JLabel("Welcome to RideShare 🚗", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));
        title.setForeground(new Color(30, 30, 30));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));

        roleBox = new JComboBox<>(new String[]{"rider", "driver"});
        roleBox.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));

        loginBtn = new JButton("🚗 Login");
        registerBtn = new JButton("📝 Register");

        styleButton(loginBtn, new Color(135, 206, 250));
        styleButton(registerBtn, new Color(255, 182, 193));

        // Add components to the transparent panel
        panel.add(title);
        panel.add(Box.createVerticalStrut(18));
        panel.add(labeledField("📧 Email:", emailField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(labeledField("🔒 Password:", passwordField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(labeledField("🧍 Role:", roleBox));
        panel.add(Box.createVerticalStrut(15));
        panel.add(loginBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(registerBtn);

        // Add to center of screen
        bgPanel.add(panel, new GridBagConstraints());

        // Action listeners
        loginBtn.addActionListener(e -> loginUser());
        registerBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "📝 Registration feature coming soon!")
        );

        setVisible(true);
    }

    private JPanel labeledField(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setOpaque(false);
        JLabel l = new JLabel(labelText);
        l.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        l.setForeground(Color.DARK_GRAY);
        l.setPreferredSize(new Dimension(100, 25));
        p.add(l, BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(220, 38));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1, true));
    }

    private void loginUser() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        UserDAO dao = new UserDAO();
        User user = dao.login(email, password);

        if (user != null && user.getRole().equals(role)) {
            JOptionPane.showMessageDialog(this, "✅ Login successful!");
            dispose();
            if (role.equals("rider"))
                new RiderDashboard(user);
            else
                new DriverDashboard(user);
        } else {
            JOptionPane.showMessageDialog(this, "❌ Invalid credentials!");
        }
    }
}