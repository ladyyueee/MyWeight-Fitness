package myweigh.ui.components;

import myweigh.ui.theme.Theme;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/** Slider look from the reference: thin track, green fill, round thumb. */
public class ThemedSliderUI extends BasicSliderUI {

    public ThemedSliderUI(JSlider slider) {
        super(slider);
    }

    public static JSlider create(int min, int max, int value) {
        JSlider slider = new JSlider(min, max, value);
        slider.setOpaque(false);
        slider.setUI(new ThemedSliderUI(slider));
        slider.setFocusable(false);
        return slider;
    }

    @Override
    protected Dimension getThumbSize() {
        return new Dimension(22, 22);
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Theme t = Theme.get();
        Rectangle r = trackRect;
        int h = 6;
        int y = r.y + r.height / 2 - h / 2;
        g2.setColor(t.border);
        g2.fillRoundRect(r.x, y, r.width, h, h, h);
        int pos = xPositionForValue(slider.getValue());
        g2.setColor(slider.isEnabled() ? t.primary : t.muted);
        g2.fillRoundRect(r.x, y, Math.max(h, pos - r.x), h, h, h);
        g2.dispose();
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Theme t = Theme.get();
        Rectangle r = thumbRect;
        int d = 20;
        int x = r.x + (r.width - d) / 2;
        int y = r.y + (r.height - d) / 2;
        g2.setColor(Color.WHITE);
        g2.fillOval(x, y, d, d);
        g2.setColor(slider.isEnabled() ? t.primary : t.muted);
        g2.fillOval(x + 3, y + 3, d - 6, d - 6);
        g2.dispose();
    }

    @Override
    public void paintFocus(Graphics g) {
        // no focus ring
    }
}
