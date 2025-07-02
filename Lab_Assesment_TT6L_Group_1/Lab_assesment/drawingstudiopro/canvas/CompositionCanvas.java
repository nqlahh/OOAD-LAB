package drawingstudiopro.canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CompositionCanvas extends JPanel {
    private List<DrawingItem> drawingItems;
    private DrawingItem selectedItem = null;
    private Point lastMousePoint;
    private BufferedImage canvasBuffer;
    private double canvasRotationAngle = 0;

    public CompositionCanvas() {
        drawingItems = new ArrayList<>();
        setBackground(Color.LIGHT_GRAY);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedItem = null;
                for (int i = drawingItems.size() - 1; i >= 0; i--) {
                    DrawingItem item = drawingItems.get(i);
                    if (item.contains(e.getPoint())) {
                        selectedItem = item;
                        lastMousePoint = e.getPoint();
                        break;
                    }
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedItem != null && lastMousePoint != null) {
                    int dx = e.getX() - lastMousePoint.x;
                    int dy = e.getY() - lastMousePoint.y;

                    Rectangle bounds = selectedItem.getBounds();
                    selectedItem.setPosition(bounds.x + dx, bounds.y + dy);

                    lastMousePoint = e.getPoint();
                    repaint();
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                createCanvasBuffer();
                repaint();
            }
        });
    }

    private void createCanvasBuffer() {
        int w = Math.max(1, getWidth());
        int h = Math.max(1, getHeight());
        canvasBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = canvasBuffer.createGraphics();
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, w, h);
        for (DrawingItem item : drawingItems) {
            item.draw(g2d);
        }
        g2d.dispose();
    }

   @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Rotate the entire canvas around its center
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        g2d.rotate(canvasRotationAngle, cx, cy);

        for (DrawingItem item : drawingItems) {
            item.draw(g2d);
            // Highlight selected item
            if (item == selectedItem) {
                Rectangle bounds = selectedItem.getBounds();
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        }
        g2d.dispose();
    }

    public DrawingItem getSelectedItem() {
    return selectedItem;
    }   

    public void addDrawingItem(DrawingItem item) {
        drawingItems.add(item);
        repaint();
    }

    public void clearCanvas() {
        drawingItems.clear();
        canvasRotationAngle = 0;
        repaint();
    }

        public void setCanvasRotationAngle(double angle) {
        this.canvasRotationAngle = angle;
        repaint();
    }

    public void rotateSelectedItem(double angle) {
        if (selectedItem != null) {
            selectedItem.rotate(angle);
            repaint();
        }
    }

    public void removeDrawingItem(DrawingItem item) {
    drawingItems.remove(item);
    if (selectedItem == item) {
        selectedItem = null;
    }
    repaint();
    }

    public Point getCanvasLocation() {
    return this.getLocation();
    }

    public Dimension getCanvasSize() {
        return this.getSize();
    }

    public double getCanvasRotationAngle() {
        return canvasRotationAngle;
    }

    public BufferedImage mergeToNewCanvas() {
        createCanvasBuffer();
        BufferedImage mergedImage = new BufferedImage(canvasBuffer.getWidth(), canvasBuffer.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = mergedImage.createGraphics();
        AffineTransform transform = new AffineTransform();
        transform.translate(getWidth() / 2.0, getHeight() / 2.0);
        transform.rotate(canvasRotationAngle);
        transform.translate(-getWidth() / 2.0, -getHeight() / 2.0);
        g2d.transform(transform);
        g2d.drawImage(canvasBuffer, 0, 0, null);
        g2d.dispose();
        return mergedImage;
    }
}