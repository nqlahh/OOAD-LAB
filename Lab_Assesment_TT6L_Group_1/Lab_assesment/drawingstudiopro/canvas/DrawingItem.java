package drawingstudiopro.canvas;

import java.awt.*;

public interface DrawingItem {
    void draw(Graphics2D g2d);
    void rotate(double angle);
    void setSize(int width, int height);
    void mirror();
    Rectangle getBounds();
    void setPosition(int x, int y);
    boolean contains(Point p);
    
}