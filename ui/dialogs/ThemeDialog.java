package myweigh.ui.dialogs;

import myweigh.model.User;
import myweigh.service.Session;
import myweigh.ui.MainFrame;
import myweigh.ui.components.Badge;
import myweigh.ui.components.ListCard;
import myweigh.ui.components.Toast;
import myweigh.ui.theme.Theme;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Theme customization: pick a color theme for the whole app. */
public class ThemeDialog extends AppDialog {

    public ThemeDialog(Component parent, Session session) {
        super(parent, "Choose a Theme");
        Theme current = Theme.get();
        for (Theme theme : Theme.all()) {
            boolean active = theme.name.equals(current.name);
            content.add(new ListCard(swatch(theme), theme.name, theme.isDark() ? "Dark mode" : "Light mode",
                    active ? Badge.success("CURRENT") : null, active, () -> {
                        User u = session.getCurrentUser();
                        u.setThemeName(theme.name);
                        session.save();
                        MainFrame frame = MainFrame.of(this);
                        dispose();
                        frame.rebuildApp();
                        Toast.show(frame, theme.name + " theme applied");
                    }));
        }
    }

    @Override
    protected int dialogWidth() {
        return 400;
    }

    private static JComponent swatch(Theme theme) {
        JComponent c = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color[] colors = {theme.background, theme.accent, theme.header, theme.primary};
                int y = (getHeight() - 26) / 2;
                for (int i = 0; i < colors.length; i++) {
                    g2.setColor(colors[i]);
                    g2.fillOval(i * 14, y, 26, 26);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(i * 14, y, 26, 26);
                }
                g2.dispose();
            }
        };
        c.setPreferredSize(new Dimension(70, 28));
        return c;
    }
}
