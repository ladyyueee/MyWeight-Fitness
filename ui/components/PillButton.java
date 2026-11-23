package myweigh.ui.components;

import myweigh.ui.theme.Theme;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Rounded button in the styles used by the reference design. */
public class PillButton extends JButton {
    public enum Style { PRIMARY, DARK, DANGER, OUTLINE, SOFT }

    private final Style style;
    private final boolean small;

    public PillButton(String text, Style style) {
        this(text, style, false);
    }

    public PillButton(String text, Style style, boolean small) {
        super(text);
        this.style = style;
        this.small = small;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setFont(Theme.font(Font.BOLD, small ? 13 : 15));
        setForeground(foreground());
        int v = small ? 8 : 14;
        int h = small ? 14 : 22;
        setBorder(new EmptyBorder(v, h, v, h));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public PillButton withIcon(Icons.Kind kind) {
        setIcon(Icons.of(kind, small ? 16 : 18, foreground()));
        setIconTextGap(8);
        return this;
    }

    private Color foreground() {
        Theme t = Theme.get();
        switch (style) {
            case DANGER:
                return t.danger;
            case OUTLINE:
            case SOFT:
                return t.primary;
            default:
                return Color.WHITE;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Theme t = Theme.get();
        boolean hover = getModel().isRollover();
        boolean pressed = getModel().isPressed();
        int w = getWidth();
        int h = getHeight();
        int arc = small ? 12 : 16;
        Color border = null;
        switch (style) {
            case PRIMARY:
                Color start = pressed ? t.primaryDark : hover ? Theme.lighten(t.primary, 12) : t.primary;
                g2.setPaint(new GradientPaint(0, 0, start, w, h, t.primaryDark));
                break;
            case DARK:
                g2.setColor(hover ? Theme.lighten(t.dark, 25) : t.dark);
                break;
            case DANGER:
                g2.setColor(hover ? Theme.alpha(t.danger, 40) : t.dangerBg);
                border = Theme.alpha(t.danger, 90);
                break;
            case OUTLINE:
                g2.setColor(hover ? t.mint : t.card);
                border = t.primary;
                break;
            default:
                g2.setColor(hover ? t.mintBorder : t.mint);
                border = t.mintBorder;
                break;
        }
        if (!isEnabled()) {
            g2.setColor(t.border);
            border = null;
        }
        g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);
        if (border != null) {
            g2.setColor(border);
            g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
