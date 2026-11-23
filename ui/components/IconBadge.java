package myweigh.ui.components;

import javax.swing.Icon;
import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** An icon on a rounded-square or circular colored background. */
public class IconBadge extends JComponent {
    private final Icons.Kind kind;
    private final int size;
    private final int iconSize;
    private final boolean circle;
    private Color background;
    private Color foreground;
    private Color borderColor;

    public IconBadge(Icons.Kind kind, int size, int iconSize, Color background, Color foreground, boolean circle) {
        this.kind = kind;
        this.size = size;
        this.iconSize = iconSize;
        this.background = background;
        this.foreground = foreground;
        this.circle = circle;
        Dimension d = new Dimension(size, size);
        setPreferredSize(d);
        setMinimumSize(d);
    }

    public void setColors(Color background, Color foreground) {
        this.background = background;
        this.foreground = foreground;
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
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;
        int arc = circle ? size : Math.round(size * 0.34f);
        if (background != null) {
            g2.setColor(background);
            g2.fillRoundRect(x, y, size - 1, size - 1, arc, arc);
        }
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(x, y, size - 1, size - 1, arc, arc);
        }
        Icon icon = Icons.of(kind, iconSize, foreground);
        icon.paintIcon(this, g2, x + (size - iconSize) / 2, y + (size - iconSize) / 2);
        g2.dispose();
    }
}
