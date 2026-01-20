package rideshare;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class BackgroundPanel extends JPanel {
    private BufferedImage image;

    public BackgroundPanel(String imagePath) {
        try {
            InputStream in = getClass().getResourceAsStream(imagePath);
            if (in != null) {
                image = ImageIO.read(in);
                System.out.println("Loaded background from resource: " + imagePath);
            } else {
                image = ImageIO.read(new File(imagePath));
                System.out.println("Loaded background from file: " + imagePath);
            }
        } catch (IOException e) {
            System.out.println("Failed to load background: " + e.getMessage());
        }
        setLayout(new BorderLayout());
    }

    @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (image != null) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();
        double panelWidth = getWidth();
        double panelHeight = getHeight();

        double scale = Math.max(panelWidth / imgWidth, panelHeight / imgHeight);
        int newWidth = (int) (imgWidth * scale);
        int newHeight = (int) (imgHeight * scale);

        int x = (int) ((panelWidth - newWidth) / 2);
        int y = (int) ((panelHeight - newHeight) / 2);

        g2d.drawImage(image, x, y, newWidth, newHeight, this);
        g2d.dispose();
    }
}
}