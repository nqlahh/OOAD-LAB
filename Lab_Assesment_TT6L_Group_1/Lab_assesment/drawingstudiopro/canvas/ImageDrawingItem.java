package drawingstudiopro.canvas;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageDrawingItem implements DrawingItem {
    private BufferedImage image;
    private Rectangle bounds;
    private double rotationAngle = 0;
    private boolean mirrored = false;

    public ImageDrawingItem(BufferedImage image, int x, int y, int width, int height) {
        this.image = image;
        this.bounds = new Rectangle(x, y, width, height);
    }

    @Override
    public void draw(Graphics2D g2d) {
        Graphics2D g2dCopy = (Graphics2D) g2d.create();
        AffineTransform transform = new AffineTransform();

        // Move to center of item
        transform.translate(bounds.x + bounds.width / 2.0, bounds.y + bounds.height / 2.0);
        // Apply rotation
        transform.rotate(rotationAngle);
        // Apply mirroring if needed
        if (mirrored) {
            transform.scale(-1, 1);
        }
        // Move back to top-left of image
        transform.translate(-bounds.width / 2.0, -bounds.height / 2.0);

        g2dCopy.setTransform(transform);
        g2dCopy.drawImage(image, 0, 0, bounds.width, bounds.height, null);
        g2dCopy.dispose();
    }

    @Override
    public void rotate(double angle) {
        this.rotationAngle += angle;
    }

    @Override
    public void mirror() {
        mirrored = !mirrored;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(bounds);
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
    public void setSize(int width, int height) {
        bounds.setSize(width, height);
    }
}