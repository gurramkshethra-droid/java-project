package rideshare;

import java.awt.*;
import javax.swing.*;

public class RegisterFrame extends JFrame {
    private JTextField nameField, emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JButton registerButton, backButton;

    public RegisterFrame() {
        setTitle("Register - RideShare");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);

        // Background
        BackgroundPanel bg = new BackgroundPanel("src/rideshare/images/app_bg.jpg");
        bg.setLayout(new GridBagLayout());
        setContentPane(bg);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        bg.add(title, gbc);

        // Name
        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        bg.add(nameLabel, gbc);

        nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        bg.add(nameField, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        bg.add(emailLabel, gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        bg.add(emailField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        bg.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        bg.add(passwordField, gbc);

        // Role
        JLabel roleLabel = new JLabel("Role:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        bg.add(roleLabel, gbc);

        roleBox = new JComboBox<>(new String[]{"rider", "driver"});
        gbc.gridx = 1;
        gbc.gridy = 4;
        bg.add(roleBox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");

        styleButton(registerButton, new Color(144, 238, 144)); // Light green
        styleButton(backButton, new Color(255, 182, 193));     // Light pink

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        bg.add(buttonPanel, gbc);

        // Button actions
        registerButton.addActionListener(e -> registerUser());
        backButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        setVisible(true);
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(140, 35));
    }

    private void registerUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "Password must be at least 4 characters long!");
            return;
        }

        // Create user and register
        User newUser = new User(name, email, password, role);
        UserDAO dao = new UserDAO();
        boolean success = dao.register(newUser);

        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Registration successful!\nYou can now login with your credentials.");
            dispose();
            new LoginFrame();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Registration failed!\nEmail might already be registered.");
        }
    }
}