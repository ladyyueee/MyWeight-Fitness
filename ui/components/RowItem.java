package myweigh.ui.components;

import myweigh.ui.theme.Theme;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

/** A row inside a grouped card (settings, profile details, today's activities). */
public class RowItem extends JPanel {

    public RowItem(JComponent leading, String title, String subtitle, JComponent trailing, Runnable onClick) {
        super(new BorderLayout(12, 0));
        Theme t = Theme.get();
        setOpaque(false);
        setDivider(false);
        if (leading != null) {
            add(leading, BorderLayout.WEST);
        }
        JPanel text = UIFactory.transparent(new GridLayout(0, 1, 0, 2));
        text.add(UIFactory.label(title, subtitle == null ? Font.PLAIN : Font.BOLD, 14, t.text));
        if (subtitle != null) {
            text.add(UIFactory.label(subtitle, Font.PLAIN, 12, t.muted));
        }
        add(text, BorderLayout.CENTER);
        if (trailing != null) {
            add(UIFactory.wrapCenter(trailing), BorderLayout.EAST);
        }
        if (onClick != null) {
            UIFactory.makeClickable(this, onClick);
        }
    }

    public void setDivider(boolean divider) {
        EmptyBorder pad = new EmptyBorder(12, 0, 12, 0);
        setBorder(divider ? new CompoundBorder(new MatteBorder(0, 0, 1, 0, Theme.get().border), pad) : pad);
    }
}
