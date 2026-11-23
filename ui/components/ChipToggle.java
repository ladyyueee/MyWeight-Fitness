package myweigh.ui.components;

import myweigh.ui.theme.Theme;

import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Selectable chip with a check icon (equipment, focus area, etc.). */
public class ChipToggle extends JToggleButton {

    public ChipToggle(String text, boolean selected) {
        super(text, selected);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setFont(Theme.font(Font.BOLD, 13));
        setBorder(new EmptyBorder(11, 12, 11, 12));
        setHorizontalAlignment(SwingConstants.LEFT);
        setIconTextGap(8);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addItemListener(e -> updateLook());
        updateLook();
    }

    private void updateLook() {
        Theme t = Theme.get();
        boolean on = isSelected();
        setIcon(Icons.of(on ? Icons.Kind.CHECK_CIRCLE : Icons.Kind.CIRCLE, 18, on ? t.primary : t.muted));
        setForeground(on ? t.primary : t.text);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Theme t = Theme.get();
        boolean on = isSelected();
        g2.setColor(on ? t.mint : t.card);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        g2.setColor(on ? t.primary : t.border);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        g2.dispose();
        super.paintComponent(g);
    }
}
