package myweigh.ui.components;

import myweigh.ui.theme.Theme;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;

/** BMI gradient scale with a marker (Underweight / Normal / Overweight). */
public class BmiScale extends JComponent {
    private double bmi;

    public BmiScale(double bmi) {
        this.bmi = bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(220, 42);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Theme t = Theme.get();
        int w = getWidth();
        int barY = 8;
        int barH = 8;
        g2.setPaint(new LinearGradientPaint(0, 0, w, 0, new float[]{0f, 0.45f, 0.75f, 1f},
                new Color[]{new Color(0x2BB3E6), new Color(0x2FB37A), new Color(0x9DBB3C), new Color(0xE3A92B)}));
        g2.fillRoundRect(0, barY, w, barH, barH, barH);

        double ratio = Math.max(0, Math.min(1, (bmi - 15) / 20.0));
        int cx = (int) Math.round(8 + ratio * (w - 16));
        g2.setColor(Color.WHITE);
        g2.fillOval(cx - 7, barY + barH / 2 - 7, 14, 14);
        g2.setColor(t.dark);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(cx - 7, barY + barH / 2 - 7, 14, 14);

        g2.setFont(Theme.font(Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();
        int ty = barY + barH + 10 + fm.getAscent();
        g2.setColor(t.muted);
        g2.drawString("Underweight", 0, ty);
        String right = "Overweight";
        g2.drawString(right, w - fm.stringWidth(right), ty);
        g2.setFont(Theme.font(Font.BOLD, 11));
        fm = g2.getFontMetrics();
        String mid = w < 300 ? "Normal" : "Normal (18.5–24.9)";
        g2.setColor(t.text);
        g2.drawString(mid, (w - fm.stringWidth(mid)) / 2, ty);
        g2.dispose();
    }
}
