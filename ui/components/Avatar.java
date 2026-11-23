package myweigh.ui.components;

import myweigh.ui.theme.Theme;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Circular avatar with initials and an optional colored ring. */
public class Avatar extends JComponent {
    private final String initials;
    private final int size;
    private final Color fill;
    private final Color ring;

    public Avatar(String initials, int size, Color fill, Color ring) {
        this.initials = initials;
        this.size = size;
        this.fill = fill;
        this.ring = ring;
        Dimension d = new Dimension(size, size);
        setPreferredSize(d);
        setMinimumSize(d);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;
        int inset = 0;
        if (ring != null) {
            g2.setColor(ring);
            g2.fillOval(x, y, size, size);
            inset = Math.max(3, size / 22);
        }
        g2.setColor(fill);
        g2.fillOval(x + inset, y + inset, size - inset * 2, size - inset * 2);
        g2.setColor(Color.WHITE);
        g2.setFont(Theme.font(Font.BOLD, size * 0.36f));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(initials, x + (size - fm.stringWidth(initials)) / 2,
                y + (size - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }
}
