package rideshare;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set system look and feel - CORRECTED VERSION
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        new LoginFrame();
    }
}