package myweigh.ui.components;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

/** Panel with a rounded fill, optional gradient and optional border. */
public class RoundedPanel extends JPanel {
    private Color fill;
    private Color gradientEnd;
    private Color borderColor;
    private final int radius;

    public RoundedPanel(LayoutManager layout, Color fill, Color borderColor, int radius) {
        super(layout);
        this.fill = fill;
        this.borderColor = borderColor;
        this.radius = radius;
        setOpaque(false);
    }

    public void setFill(Color fill) {
        this.fill = fill;
        this.gradientEnd = null;
        repaint();
    }

    public void setGradient(Color start, Color end) {
        this.fill = start;
        this.gradientEnd = end;
        repaint();
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        if (fill != null) {
            if (gradientEnd != null) {
                g2.setPaint(new GradientPaint(0, 0, fill, w, h, gradientEnd));
            } else {
                g2.setColor(fill);
            }
            g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);
        }
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
