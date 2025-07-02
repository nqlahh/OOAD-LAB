package drawingstudiopro.canvas;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;

public class AnimalItem implements DrawingItem {
    private Rectangle bounds;
    private double rotationAngle = 0;
    private BufferedImage image;
    private boolean mirrored = false;
    private static final String DEFAULT_ANIMAL_PATH = "C:/Users/user/Downloads/Lab_assesment(1)/Lab_assesment/drawingstudiopro/canvas/animal.jpg";

    // Constructor for default animal
    public AnimalItem(int x, int y, int width, int height) {
        this.bounds = new Rectangle(x, y, width, height);
        loadDefaultImage(width, height);
    }

    // Constructor for custom animal image (from gallery selection)
    public AnimalItem(int x, int y, int width, int height, BufferedImage customImage) {
        this.bounds = new Rectangle(x, y, width, height);
        if (customImage != null) {
            this.image = customImage;
        } else {
            loadDefaultImage(width, height);
        }
    }

    private void loadDefaultImage(int width, int height) {
        try {
            File imageFile = new File(DEFAULT_ANIMAL_PATH);
            if (imageFile.exists()) {
                this.image = ImageIO.read(imageFile);
            } else {
                // Try to find any animal image in the directory
                File directory = new File("C:/Users/user/Downloads/Lab_assesment(1)/Lab_assesment/drawingstudiopro/canvas/");
                if (directory.exists()) {
                    File[] animalFiles = directory.listFiles((dir, name) -> {
                        String lowerName = name.toLowerCase();
                        return lowerName.contains("animal") &&
                               (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") ||
                                lowerName.endsWith(".png") || lowerName.endsWith(".gif"));
                    });

                    if (animalFiles != null && animalFiles.length > 0) {
                        this.image = ImageIO.read(animalFiles[0]); // Use first found animal image
                    } else {
                        createFallbackImage(width, height);
                    }
                } else {
                    createFallbackImage(width, height);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading animal image: " + e.getMessage());
            createFallbackImage(width, height);
        }
    }

    private void createFallbackImage(int width, int height) {
        // Create a simple animal face as fallback
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = this.image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw face
        g2d.setColor(new Color(200, 160, 100));
        g2d.fillOval(0, 0, width, height);

        // Draw ears
        g2d.setColor(new Color(180, 140, 90));
        g2d.fillOval(width / 8, height / 8, width / 4, height / 4);
        g2d.fillOval(width - width / 8 - width / 4, height / 8, width / 4, height / 4);

        // Draw eyes
        g2d.setColor(Color.BLACK);
        g2d.fillOval(width / 3, height / 2, width / 10, height / 10);
        g2d.fillOval(width - width / 3 - width / 10, height / 2, width / 10, height / 10);

        // Draw nose
        g2d.setColor(Color.PINK);
        g2d.fillOval(width / 2 - width / 20, height * 2 / 3, width / 10, height / 15);

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
        if (mirrored) {
            transform.scale(-1, 1);
        }
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
        mirrored = !mirrored;
    }

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

    // Method to create a deep copy of this AnimalItem
    public AnimalItem createCopy() {
        AnimalItem copy = new AnimalItem(bounds.x, bounds.y, bounds.width, bounds.height, this.image);
        copy.setRotationAngle(this.rotationAngle);
        return copy;
    }

    // Method to scale the animal item
    public void scale(double factor) {
        int newWidth = (int)(bounds.width * factor);
        int newHeight = (int)(bounds.height * factor);
        bounds.setSize(newWidth, newHeight);
    }

    // Method to check if this is using a custom image
    public boolean isUsingCustomImage() {
        try {
            BufferedImage defaultImage = ImageIO.read(new File(DEFAULT_ANIMAL_PATH));
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
        return "AnimalItem{" +
                "bounds=" + bounds +
                ", rotationAngle=" + rotationAngle +
                ", hasCustomImage=" + isUsingCustomImage() +
                '}';
    }
}