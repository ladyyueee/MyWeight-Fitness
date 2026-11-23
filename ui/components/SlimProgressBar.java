package myweigh.ui.components;

import myweigh.ui.theme.Theme;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Thin rounded progress bar. */
public class SlimProgressBar extends JComponent {
    private double value;
    private final Color color;
    private final int barHeight;

    public SlimProgressBar(double value, Color color, int barHeight) {
        this.value = value;
        this.color = color;
        this.barHeight = barHeight;
    }

    public void setValue(double value) {
        this.value = value;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(60, barHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int y = (getHeight() - barHeight) / 2;
        g2.setColor(Theme.get().border);
        g2.fillRoundRect(0, y, getWidth(), barHeight, barHeight, barHeight);
        int filled = (int) Math.round(getWidth() * Math.max(0, Math.min(1, value)));
        if (filled > 0) {
            g2.setColor(color);
            g2.fillRoundRect(0, y, Math.max(filled, barHeight), barHeight, barHeight, barHeight);
        }
        g2.dispose();
    }
}
