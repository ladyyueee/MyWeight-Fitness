package myweigh.ui.components;

import myweigh.ui.theme.Theme;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Small pill label such as NORMAL, Done, Missed or Logged. */
public class Badge extends JLabel {
    private final Color background;

    public Badge(String text, Color background, Color foreground) {
        super(text);
        this.background = background;
        setFont(Theme.font(Font.BOLD, 11));
        setForeground(foreground);
        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(new EmptyBorder(3, 9, 3, 9));
        setOpaque(false);
    }

    public static Badge success(String text) {
        Theme t = Theme.get();
        return new Badge(text, t.isDark() ? Theme.alpha(t.primary, 60) : Theme.lighten(t.mintBorder, 18), t.primary);
    }

    public static Badge danger(String text) {
        Theme t = Theme.get();
        return new Badge(text, t.dangerBg, t.danger);
    }

    public static Badge warning(String text) {
        Theme t = Theme.get();
        return t.isDark() ? new Badge(text, Theme.alpha(t.accent, 50), t.accent)
                : new Badge(text, new Color(255, 243, 222), new Color(176, 112, 20));
    }

    public static Badge neutral(String text) {
        Theme t = Theme.get();
        return new Badge(text, t.subtle, t.muted);
    }

    public static Badge forBmi(String category) {
        String text = category.toUpperCase();
        if ("Normal".equals(category)) {
            return success(text);
        }
        if ("Underweight".equals(category)) {
            Theme t = Theme.get();
            return new Badge(text, Theme.alpha(t.water, 40), t.water.darker());
        }
        if ("Overweight".equals(category)) {
            return warning(text);
        }
        return danger(text);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(background);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
