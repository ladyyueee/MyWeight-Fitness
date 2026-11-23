package myweigh.ui.components;

import myweigh.ui.theme.Theme;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

/** Card row with a leading icon, title, subtitle and trailing badge/chevron. */
public class ListCard extends RoundedPanel {

    public ListCard(JComponent leading, String title, String subtitle, JComponent trailing,
                    boolean tinted, Runnable onClick) {
        super(new BorderLayout(12, 0), tinted ? Theme.get().mint : Theme.get().card,
                tinted ? Theme.get().mintBorder : Theme.get().border, 18);
        Theme t = Theme.get();
        setBorder(new EmptyBorder(14, 14, 14, 14));
        if (leading != null) {
            add(leading, BorderLayout.WEST);
        }
        JPanel text = UIFactory.transparent(new GridLayout(0, 1, 0, 2));
        JLabel titleLabel = UIFactory.label(title, Font.BOLD, 15, t.text);
        text.add(titleLabel);
        if (subtitle != null) {
            text.add(UIFactory.label(subtitle, Font.PLAIN, 12.5f, t.muted));
        }
        add(text, BorderLayout.CENTER);
        if (trailing != null) {
            add(UIFactory.wrapCenter(trailing), BorderLayout.EAST);
        }
        if (onClick != null) {
            UIFactory.makeClickable(this, onClick);
        }
    }
}
