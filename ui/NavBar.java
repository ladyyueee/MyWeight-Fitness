package myweigh.ui;

import myweigh.model.User;
import myweigh.ui.components.Avatar;
import myweigh.ui.components.Icons;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.RoundedPanel;
import myweigh.ui.components.Stack;
import myweigh.ui.components.UIFactory;
import myweigh.ui.theme.Theme;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.Map;

/** Navigation: a bottom tab bar (phone) or a sidebar (laptop). */
public class NavBar extends JPanel {
    private static final Icons.Kind[] ICONS = {
            Icons.Kind.HOME, Icons.Kind.PLANS, Icons.Kind.PROGRESS, Icons.Kind.PROFILE, Icons.Kind.SETTINGS
    };

    private final AppShell shell;
    private final boolean vertical;
    private final Map<String, NavItem> items = new LinkedHashMap<>();
    private final JPanel userSlot;

    public NavBar(AppShell shell, boolean vertical) {
        super(new BorderLayout());
        this.shell = shell;
        this.vertical = vertical;
        Theme t = Theme.get();
        setBackground(t.card);

        if (vertical) {
            setPreferredSize(new Dimension(236, 10));
            setBorder(new MatteBorder(0, 0, 0, 1, t.border));
            JPanel brand = UIFactory.transparent(new BorderLayout());
            brand.setBorder(new EmptyBorder(22, 18, 18, 18));
            brand.add(UIFactory.brandRow(), BorderLayout.CENTER);
            add(brand, BorderLayout.NORTH);
            Stack list = new Stack(6, new Insets(6, 12, 6, 12));
            for (int i = 0; i < AppShell.TABS.length; i++) {
                NavItem item = new NavItem(AppShell.TABS[i], ICONS[i]);
                items.put(AppShell.TABS[i], item);
                list.add(item);
            }
            add(list, BorderLayout.CENTER);
            userSlot = UIFactory.transparent(new BorderLayout());
            add(userSlot, BorderLayout.SOUTH);
            refreshUser();
        } else {
            setBorder(new MatteBorder(1, 0, 0, 0, t.border));
            setPreferredSize(new Dimension(10, 66));
            JPanel grid = UIFactory.transparent(new GridLayout(1, AppShell.TABS.length));
            for (int i = 0; i < AppShell.TABS.length; i++) {
                NavItem item = new NavItem(AppShell.TABS[i], ICONS[i]);
                items.put(AppShell.TABS[i], item);
                grid.add(item);
            }
            add(grid, BorderLayout.CENTER);
            userSlot = null;
        }
    }

    public void setSelected(String tab) {
        for (Map.Entry<String, NavItem> e : items.entrySet()) {
            e.getValue().setSelected(e.getKey().equals(tab));
        }
    }

    /** Sidebar footer: user chip and log out button. */
    public void refreshUser() {
        if (userSlot == null) {
            return;
        }
        userSlot.removeAll();
        User u = shell.getSession().getCurrentUser();
        if (u == null) {
            return;
        }
        Theme t = Theme.get();
        RoundedPanel chip = new RoundedPanel(new BorderLayout(10, 0), t.mint, t.mintBorder, 16);
        chip.setBorder(new EmptyBorder(8, 10, 8, 10));
        chip.add(new Avatar(u.getInitials(), 36, t.accent, null), BorderLayout.WEST);
        JPanel text = UIFactory.transparent(new GridLayout(0, 1));
        text.add(UIFactory.label(u.getFullName(), Font.BOLD, 13, t.text));
        text.add(UIFactory.label(u.getCategoryName(), Font.PLAIN, 11, t.muted));
        chip.add(text, BorderLayout.CENTER);
        UIFactory.makeClickable(chip, () -> shell.select(AppShell.PROFILE));

        PillButton logout = new PillButton("Log Out", PillButton.Style.DANGER, true).withIcon(Icons.Kind.LOGOUT);
        logout.addActionListener(e -> shell.logout());

        Stack s = new Stack(10, new Insets(12, 14, 18, 14));
        s.add(chip);
        s.add(logout);
        userSlot.add(s, BorderLayout.CENTER);
        userSlot.revalidate();
        userSlot.repaint();
    }

    private final class NavItem extends JPanel {
        private final Icons.Kind kind;
        private final JLabel label;
        private boolean selected;

        NavItem(String tab, Icons.Kind kind) {
            super(new BorderLayout());
            this.kind = kind;
            setOpaque(false);
            label = new JLabel(tab);
            if (vertical) {
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setIconTextGap(12);
                setBorder(new EmptyBorder(11, 14, 11, 14));
            } else {
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setHorizontalTextPosition(SwingConstants.CENTER);
                label.setVerticalTextPosition(SwingConstants.BOTTOM);
                label.setIconTextGap(4);
                setBorder(new EmptyBorder(8, 0, 8, 0));
            }
            add(label, BorderLayout.CENTER);
            UIFactory.makeClickable(this, () -> shell.select(tab));
            setSelected(false);
        }

        void setSelected(boolean selected) {
            this.selected = selected;
            Theme t = Theme.get();
            java.awt.Color color = selected ? t.primary : t.muted;
            label.setIcon(Icons.of(kind, vertical ? 20 : 22, color));
            label.setForeground(color);
            label.setFont(Theme.font(selected ? Font.BOLD : Font.PLAIN, vertical ? 14 : 11.5f));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (vertical && selected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.get().mint);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }
}
