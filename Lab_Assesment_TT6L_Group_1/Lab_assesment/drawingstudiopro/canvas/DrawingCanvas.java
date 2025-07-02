package drawingstudiopro.canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Stack;

public class DrawingCanvas extends JPanel {
    private BufferedImage canvasImage;
    Color currentColor = Color.BLACK;
    private Point lastPoint;
    private BasicStroke currentStroke = new BasicStroke(2);

    public Color getCurrentColor() {
    return currentColor;
    }
    
    public void setColor(java.awt.Color color) {
        // You should have a field like 'private Color currentColor;' in DrawingCanvas
        this.currentColor = color;
        // Optionally, repaint or update as needed
        repaint();
    }
    private Stack<BufferedImage> undoStack = new Stack<>();

    // Call this before making any change to the canvas
    private void saveStateForUndo() {
        if (canvasImage != null) {
            BufferedImage copy = new BufferedImage(
                canvasImage.getWidth(), canvasImage.getHeight(), canvasImage.getType());
            Graphics g = copy.getGraphics();
            g.drawImage(canvasImage, 0, 0, null);
            g.dispose();
            undoStack.push(copy);
        }
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            canvasImage = undoStack.pop();
            repaint();
        }
    }

    public void insertImage(ImageIcon imageIcon) {
        if (imageIcon != null) {
            Graphics g = this.getGraphics();
            g.drawImage(imageIcon.getImage(), 50, 50, this);
            repaint();
        }
    }

     public ImageIcon captureSnapshot() {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        paintAll(g2);
        g2.dispose();
        return new ImageIcon(image);
    }

    public DrawingCanvas() {
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            saveStateForUndo(); 
            lastPoint = e.getPoint();
    }
});

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (canvasImage == null) {
                    initCanvas();
                }
                Graphics2D g2 = (Graphics2D) canvasImage.getGraphics();
                g2.setColor(currentColor);
                g2.setStroke(currentStroke);
                g2.drawLine(lastPoint.x, lastPoint.y, e.getX(), e.getY());
                g2.dispose();
                lastPoint = e.getPoint();
                repaint();
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (canvasImage != null && (canvasImage.getWidth() != getWidth() || canvasImage.getHeight() != getHeight())) {
                    initCanvas();
                    repaint();
                } else if (canvasImage == null) {
                    initCanvas();
                    repaint();
                }
            }
        });

        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (canvasImage == null) {
            initCanvas();
        }
        g.drawImage(canvasImage, 0, 0, null);
    }

    private void initCanvas() {
        canvasImage = new BufferedImage(Math.max(1, getWidth()), Math.max(1, getHeight()), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = canvasImage.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    public void clearCanvas() {
        saveStateForUndo();
        canvasImage = null;
        repaint();
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public void setStrokeSize(int size) {
        this.currentStroke = new BasicStroke(size);
    }

    public BufferedImage getCanvasImage() {
        return canvasImage;
    }
}