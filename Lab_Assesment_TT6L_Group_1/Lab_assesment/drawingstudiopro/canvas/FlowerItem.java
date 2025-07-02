package drawingstudiopro.canvas;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;

public class FlowerItem implements DrawingItem {
    private Rectangle bounds;
    private double rotationAngle = 0;
    private BufferedImage image;
    private static final String DEFAULT_FLOWER_PATH = "C:/Users/user/Downloads/Lab_assesment(1)/Lab_assesment/drawingstudiopro/canvas/flower.jpg";
    
    // Constructor for default flower (backward compatibility)
    public FlowerItem(int x, int y, int width, int height) {
        this.bounds = new Rectangle(x, y, width, height);
        loadDefaultImage(width, height);
    }
    
    // Constructor for custom flower image (from gallery selection)
    public FlowerItem(int x, int y, int width, int height, BufferedImage customImage) {
        this.bounds = new Rectangle(x, y, width, height);
        if (customImage != null) {
            this.image = customImage;
        } else {
            loadDefaultImage(width, height);
        }
    }
    
    // Constructor for flower with image path (from gallery selection)
    public FlowerItem(int x, int y, int width, int height, String imagePath) {
        this.bounds = new Rectangle(x, y, width, height);
        loadImageFromPath(imagePath, width, height);
    }
    
    private void loadDefaultImage(int width, int height) {
        try {
            File imageFile = new File(DEFAULT_FLOWER_PATH);
            if (imageFile.exists()) {
                this.image = ImageIO.read(imageFile);
            } else {
                // Try to find any flower image in the directory
                File directory = new File("C:/Users/user/Downloads/Lab_assesment(1)/Lab_assesment/drawingstudiopro/canvas/");
                if (directory.exists()) {
                    File[] flowerFiles = directory.listFiles((dir, name) -> {
                        String lowerName = name.toLowerCase();
                        return lowerName.contains("flower") && 
                               (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || 
                                lowerName.endsWith(".png") || lowerName.endsWith(".gif"));
                    });
                    
                    if (flowerFiles != null && flowerFiles.length > 0) {
                        this.image = ImageIO.read(flowerFiles[0]); // Use first found flower image
                    } else {
                        createFallbackImage(width, height);
                    }
                } else {
                    createFallbackImage(width, height);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading flower image: " + e.getMessage());
            createFallbackImage(width, height);
        }
    }
    
    private void loadImageFromPath(String imagePath, int width, int height) {
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    this.image = ImageIO.read(imageFile);
                } else {
                    loadDefaultImage(width, height);
                }
            } else {
                loadDefaultImage(width, height);
            }
        } catch (IOException e) {
            System.err.println("Error loading flower image from path: " + imagePath + " - " + e.getMessage());
            loadDefaultImage(width, height);
        }
    }
    
    private void createFallbackImage(int width, int height) {
        // Create a simple flower drawing as fallback
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = this.image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a simple flower
        int centerX = width / 2;
        int centerY = height / 2;
        int petalRadius = Math.min(width, height) / 4;
        
        // Draw petals
        g2d.setColor(new Color(255, 182, 193)); // Light pink
        for (int i = 0; i < 8; i++) {
            double angle = (2 * Math.PI * i) / 8;
            int petalX = centerX + (int)(Math.cos(angle) * petalRadius);
            int petalY = centerY + (int)(Math.sin(angle) * petalRadius);
            g2d.fillOval(petalX - petalRadius/2, petalY - petalRadius/2, petalRadius, petalRadius);
        }
        
        // Draw center
        g2d.setColor(Color.YELLOW);
        int centerRadius = petalRadius / 2;
        g2d.fillOval(centerX - centerRadius, centerY - centerRadius, centerRadius * 2, centerRadius * 2);
        
        // Draw stem
        g2d.setColor(Color.GREEN);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(centerX, centerY + centerRadius, centerX, height - 5);
        
        // Add leaves
        int leafY = centerY + centerRadius + 10;
        g2d.fillOval(centerX - 15, leafY - 5, 10, 15);
        g2d.fillOval(centerX + 5, leafY - 5, 10, 15);
        
        g2d.dispose();
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (image == null) {
            createFallbackImage(bounds.width, bounds.height);
        }
        
        Graphics2D g2dCopy = (Graphics2D) g2d.create();
        g2dCopy.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2dCopy.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        AffineTransform transform = new AffineTransform();
        transform.translate(bounds.x + bounds.width / 2.0, bounds.y + bounds.height / 2.0);
        transform.rotate(rotationAngle);
        transform.translate(-bounds.width / 2.0, -bounds.height / 2.0);
        g2dCopy.transform(transform);

        g2dCopy.drawImage(image, 0, 0, bounds.width, bounds.height, null);
        g2dCopy.dispose();
    }

    @Override
    public void rotate(double angle) {
        this.rotationAngle += angle;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(bounds); // Return copy to prevent external modification
    }

    @Override
    public void setPosition(int x, int y) {
        bounds.setLocation(x, y);
    }

    @Override
    public boolean contains(Point p) {
        return bounds.contains(p);
    }

    @Override
    public void mirror() {
        // Flip the image horizontally
        if (image != null) {
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth() / 2; x++) {
                    int tmp = image.getRGB(x, y);
                    image.setRGB(x, y, image.getRGB(image.getWidth() - x - 1, y));
                    image.setRGB(image.getWidth() - x - 1, y, tmp);
                }
            }
        }
    }
    // Additional methods for enhanced functionality
    public void setSize(int width, int height) {
        bounds.setSize(width, height);
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    public void setImage(BufferedImage newImage) {
        if (newImage != null) {
            this.image = newImage;
        }
    }
    
    public double getRotationAngle() {
        return rotationAngle;
    }
    
    public void setRotationAngle(double angle) {
        this.rotationAngle = angle;
    }
    
    // Method to create a deep copy of this FlowerItem
    public FlowerItem createCopy() {
        FlowerItem copy = new FlowerItem(bounds.x, bounds.y, bounds.width, bounds.height, this.image);
        copy.setRotationAngle(this.rotationAngle);
        return copy;
    }
    
    // Method to scale the flower item
    public void scale(double factor) {
        int newWidth = (int)(bounds.width * factor);
        int newHeight = (int)(bounds.height * factor);
        bounds.setSize(newWidth, newHeight);
    }
    
    // Method to check if this is using a custom image
    public boolean isUsingCustomImage() {
        try {
            BufferedImage defaultImage = ImageIO.read(new File(DEFAULT_FLOWER_PATH));
            return !imageEquals(this.image, defaultImage);
        } catch (IOException e) {
            return true; // Assume custom if we can't load default
        }
    }
    
    private boolean imageEquals(BufferedImage img1, BufferedImage img2) {
        if (img1 == null || img2 == null) return false;
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) return false;
        
        for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "FlowerItem{" +
                "bounds=" + bounds +
                ", rotationAngle=" + rotationAngle +
                ", hasCustomImage=" + isUsingCustomImage() +
                '}';
    }
}