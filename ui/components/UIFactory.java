package myweigh.ui.components;

import myweigh.AppInfo;
import myweigh.ui.theme.Theme;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

/** Small factory helpers so screens stay short and consistent. */
public final class UIFactory {
    /** Client property: component handles its own clicks (skipped by makeClickable). */
    public static final String OWN_CLICK = "myweigh.ownClick";
    public static final int MAX_CONTENT_WIDTH = 1100;

    private UIFactory() {
    }

    public static JLabel label(String text, int style, float size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.font(style, size));
        l.setForeground(color);
        return l;
    }

    public static JLabel title(String text, float size) {
        return label(text, Font.BOLD, size, Theme.get().text);
    }

    public static JLabel muted(String text, float size) {
        return label(text, Font.PLAIN, size, Theme.get().muted);
    }

    public static JLabel section(String text) {
        return label(text.toUpperCase(), Font.BOLD, 12, Theme.get().muted);
    }

    public static JLabel centered(JLabel l) {
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    public static JLabel iconLabel(Icons.Kind kind, int size, Color color) {
        return new JLabel(Icons.of(kind, size, color));
    }

    public static JPanel transparent(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setOpaque(false);
        return p;
    }

    /** Left content stretches, right content keeps its size. */
    public static JPanel row(Component left, Component right) {
        JPanel p = transparent(new BorderLayout(10, 0));
        if (left != null) {
            p.add(left, BorderLayout.CENTER);
        }
        if (right != null) {
            p.add(wrapCenter(right), BorderLayout.EAST);
        }
        return p;
    }

    /** Centers a component without stretching it. */
    public static JPanel wrapCenter(Component c) {
        JPanel p = transparent(new GridBagLayout());
        p.add(c);
        return p;
    }

    /** Horizontal group with 8px gaps, vertically centered. */
    public static JPanel hbox(Component... components) {
        JPanel p = transparent(new FlowLayout(FlowLayout.LEFT, 0, 0));
        for (int i = 0; i < components.length; i++) {
            if (i > 0) {
                p.add(Box.createHorizontalStrut(8));
            }
            p.add(components[i]);
        }
        return p;
    }

    public static JComponent gap(int height) {
        JPanel p = transparent(null);
        p.setPreferredSize(new Dimension(1, height));
        return p;
    }

    public static JScrollPane scroll(Component view) {
        JScrollPane sp = new JScrollPane(view, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setViewportBorder(null);
        sp.getViewport().setBackground(Theme.get().background);
        sp.getVerticalScrollBar().setUI(new ThinScrollBarUI());
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        sp.getVerticalScrollBar().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(18);
        return sp;
    }

    public static JLabel link(String text, Runnable action) {
        JLabel l = label(text, Font.BOLD, 13, Theme.get().primary);
        makeClickable(l, action);
        return l;
    }

    /** Makes a component (and its children) clickable with a hand cursor. */
    public static void makeClickable(JComponent component, Runnable action) {
        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.getComponent().contains(e.getPoint())) {
                    action.run();
                }
            }
        };
        attach(component, listener);
    }

    private static void attach(Component c, MouseListener listener) {
        if (c instanceof AbstractButton) {
            return;
        }
        if (c instanceof JComponent && Boolean.TRUE.equals(((JComponent) c).getClientProperty(OWN_CLICK))) {
            return;
        }
        c.addMouseListener(listener);
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) {
                attach(child, listener);
            }
        }
    }

    public static RoundedPanel card(LayoutManager layout) {
        Theme t = Theme.get();
        RoundedPanel p = new RoundedPanel(layout, t.card, t.border, 18);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        return p;
    }

    public static RoundedPanel tintedCard(LayoutManager layout) {
        Theme t = Theme.get();
        RoundedPanel p = new RoundedPanel(layout, t.mint, t.mintBorder, 18);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        return p;
    }

    /** Card holding rows separated by dividers. */
    public static RoundedPanel groupCard(List<RowItem> rows) {
        RoundedPanel card = card(new BorderLayout());
        card.setBorder(new EmptyBorder(2, 16, 2, 16));
        Stack stack = new Stack(0);
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).setDivider(i < rows.size() - 1);
            stack.add(rows.get(i));
        }
        card.add(stack, BorderLayout.CENTER);
        return card;
    }

    /** Pink full-width action row (Logout, Log Out, Delete Account). */
    public static JComponent dangerAction(String text, Runnable action) {
        Theme t = Theme.get();
        RoundedPanel p = new RoundedPanel(new BorderLayout(10, 0), t.dangerBg, Theme.alpha(t.danger, 80), 18);
        p.setBorder(new EmptyBorder(14, 16, 14, 16));
        JLabel label = label(text, Font.BOLD, 15, t.danger);
        label.setIcon(Icons.of(Icons.Kind.LOGOUT, 20, t.danger));
        label.setIconTextGap(10);
        p.add(label, BorderLayout.CENTER);
        p.add(iconLabel(Icons.Kind.CHEVRON_RIGHT, 18, t.danger), BorderLayout.EAST);
        makeClickable(p, action);
        return p;
    }

    public static JComponent fieldGroup(String labelText, JComponent field) {
        Stack s = new Stack(7);
        s.add(label(labelText, Font.BOLD, 13, Theme.get().text));
        s.add(field);
        return s;
    }

    /** Small label/value tile. */
    public static JComponent infoTile(String labelText, String value) {
        Theme t = Theme.get();
        RoundedPanel tile = new RoundedPanel(new BorderLayout(), t.mint, t.mintBorder, 14);
        tile.setBorder(new EmptyBorder(10, 12, 10, 12));
        Stack s = new Stack(3);
        s.add(label(labelText, Font.PLAIN, 11.5f, t.muted));
        s.add(new WrapText(value, Theme.font(Font.BOLD, 14), t.text));
        tile.add(s, BorderLayout.CENTER);
        return tile;
    }

    public static JComponent sectionRow(String title, JComponent right) {
        return row(label(title, Font.BOLD, 16, Theme.get().text), right);
    }

    public static JComponent statusIcon(boolean done) {
        Theme t = Theme.get();
        return iconLabel(done ? Icons.Kind.CHECK_CIRCLE : Icons.Kind.CIRCLE, 22, done ? t.primary : t.border);
    }

    public static JComponent emptyState(Icons.Kind kind, String title, String message) {
        Theme t = Theme.get();
        RoundedPanel card = card(new BorderLayout());
        Stack s = new Stack(8, new Insets(10, 6, 10, 6));
        s.add(wrapCenter(new IconBadge(kind, 48, 24, t.mint, t.primary, true)));
        s.add(centered(label(title, Font.BOLD, 15, t.text)));
        WrapText msg = new WrapText(message, Theme.font(Font.PLAIN, 13), t.muted);
        s.add(msg);
        card.add(s, BorderLayout.CENTER);
        return card;
    }

    /** Green header bar used by History, Profile and Settings. */
    public static JComponent greenHeader(String title, String subtitle, int padding, JComponent... actions) {
        Theme t = Theme.get();
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(t.header);
        Stack inner = new Stack(0, new Insets(18, padding, 18, padding));
        inner.setMaxContentWidth(MAX_CONTENT_WIDTH);
        JPanel text = transparent(new GridLayout(0, 1, 0, 3));
        text.add(label(title, Font.BOLD, 22, Color.WHITE));
        text.add(label(subtitle, Font.PLAIN, 12.5f, Theme.alpha(Color.WHITE, 215)));
        JPanel row = transparent(new BorderLayout(12, 0));
        row.add(text, BorderLayout.CENTER);
        if (actions.length > 0) {
            JPanel actionPanel = transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            for (JComponent a : actions) {
                actionPanel.add(a);
            }
            row.add(wrapCenter(actionPanel), BorderLayout.EAST);
        }
        inner.add(row);
        outer.add(inner, BorderLayout.CENTER);
        return outer;
    }

    /** Translucent button for green headers. */
    public static JComponent headerButton(Icons.Kind kind, String text, String tooltip, Runnable action) {
        RoundedPanel b = new RoundedPanel(new FlowLayout(FlowLayout.CENTER, 6, 0),
                Theme.alpha(Color.WHITE, 40), Theme.alpha(Color.WHITE, 70), 14);
        b.setBorder(new EmptyBorder(9, text == null ? 7 : 12, 9, text == null ? 7 : 14));
        b.add(iconLabel(kind, 18, Color.WHITE));
        if (text != null) {
            b.add(label(text, Font.BOLD, 13, Color.WHITE));
        }
        b.setToolTipText(tooltip);
        makeClickable(b, action);
        return b;
    }

    /** Rounded "MW" logo badge. */
    public static JComponent logoBadge(int size) {
        JComponent c = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                Theme t = Theme.get();
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.setPaint(new GradientPaint(x, y, t.primary, x + size, y + size, t.primaryDark));
                g2.fillRoundRect(x, y, size, size, size / 3, size / 3);
                g2.setColor(Color.WHITE);
                g2.setFont(Theme.font(Font.BOLD, size * 0.38f));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("MW", x + (size - fm.stringWidth("MW")) / 2, y + (size - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        c.setPreferredSize(new Dimension(size, size));
        return c;
    }

    /** Large app icon used on the login screen. */
    public static JComponent appIcon(int size) {
        JComponent c = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Theme t = Theme.get();
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.setColor(Theme.alpha(t.primary, 40));
                g2.fillRoundRect(x + 2, y + 6, size - 4, size - 2, size / 3, size / 3);
                g2.setPaint(new GradientPaint(x, y, t.primary, x + size, y + size, t.primaryDark));
                g2.fillRoundRect(x, y, size, size - 4, size / 3, size / 3);
                int icon = size / 2;
                Icons.of(Icons.Kind.DUMBBELL, icon, Color.WHITE).paintIcon(this, g2, x + (size - icon) / 2, y + (size - 4 - icon) / 2);
                g2.dispose();
            }
        };
        c.setPreferredSize(new Dimension(size + 4, size + 6));
        return c;
    }

    /** Logo badge + app name + tagline. */
    public static JComponent brandRow() {
        Theme t = Theme.get();
        JPanel row = transparent(new BorderLayout(10, 0));
        row.add(logoBadge(36), BorderLayout.WEST);
        JPanel text = transparent(new GridLayout(0, 1, 0, 0));
        text.add(label(AppInfo.NAME.toUpperCase(), Font.BOLD, 13.5f, t.text));
        text.add(label(AppInfo.TAGLINE, Font.PLAIN, 10.5f, t.muted));
        row.add(text, BorderLayout.CENTER);
        return row;
    }
}
