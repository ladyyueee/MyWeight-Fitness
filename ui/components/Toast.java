package myweigh.ui.components;

import myweigh.ui.theme.Theme;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Short message that appears near the bottom of the window and fades away. */
public final class Toast {
    private Toast() {
    }

    public static void show(Component anchor, String message) {
        if (anchor == null) {
            return;
        }
        JRootPane root = SwingUtilities.getRootPane(anchor);
        if (root == null) {
            return;
        }
        JLayeredPane layer = root.getLayeredPane();
        Color bg = Theme.get().isDark() ? new Color(0x2A3A44) : new Color(0x0F172A);
        JLabel toast = new JLabel(message, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        toast.setForeground(Color.WHITE);
        toast.setFont(Theme.font(Font.BOLD, 13));
        toast.setBorder(new EmptyBorder(11, 18, 11, 18));
        Dimension d = toast.getPreferredSize();
        int width = Math.min(d.width, layer.getWidth() - 32);
        toast.setBounds((layer.getWidth() - width) / 2, layer.getHeight() - d.height - 90, width, d.height);
        layer.add(toast, JLayeredPane.POPUP_LAYER);
        layer.repaint();
        Timer timer = new Timer(2600, e -> {
            layer.remove(toast);
            layer.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }
}
