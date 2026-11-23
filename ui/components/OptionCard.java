package myweigh.ui.components;

import myweigh.ui.theme.Theme;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

/** Selectable option card (user category and fitness level screens). */
public class OptionCard extends RoundedPanel {
    private final boolean filledStyle;
    private final JLabel titleLabel;
    private final WrapText description;
    private final IconBadge icon;
    private final JLabel indicator = new JLabel();

    public OptionCard(Icons.Kind iconKind, String title, String descriptionText, boolean filledStyle, Runnable onSelect) {
        super(new BorderLayout(14, 0), Theme.get().card, Theme.get().border, 18);
        this.filledStyle = filledStyle;
        Theme t = Theme.get();
        setBorder(new EmptyBorder(16, 16, 16, 16));
        if (iconKind != null) {
            icon = new IconBadge(iconKind, 44, 22, t.mint, t.primary, false);
            add(icon, BorderLayout.WEST);
        } else {
            icon = null;
        }
        Stack text = new Stack(4);
        titleLabel = UIFactory.label(title, Font.BOLD, filledStyle ? 18 : 15, t.text);
        description = new WrapText(descriptionText, Theme.font(Font.PLAIN, 13), t.muted);
        text.add(titleLabel);
        text.add(description);
        add(text, BorderLayout.CENTER);
        add(indicator, BorderLayout.EAST);
        UIFactory.makeClickable(this, onSelect);
        setSelected(false);
    }

    public void setSelected(boolean selected) {
        Theme t = Theme.get();
        if (selected && filledStyle) {
            setGradient(t.primary, t.primaryDark);
            setBorderColor(t.primaryDark);
            titleLabel.setForeground(Color.WHITE);
            description.setForeground(Theme.alpha(Color.WHITE, 225));
            indicator.setIcon(Icons.of(Icons.Kind.CHECK_CIRCLE, 22, Color.WHITE));
        } else if (selected) {
            setFill(t.mint);
            setBorderColor(t.primary);
            titleLabel.setForeground(t.text);
            description.setForeground(t.muted);
            indicator.setIcon(Icons.of(Icons.Kind.CHECK_CIRCLE, 22, t.primary));
        } else {
            setFill(t.card);
            setBorderColor(t.border);
            titleLabel.setForeground(t.text);
            description.setForeground(t.muted);
            indicator.setIcon(Icons.of(Icons.Kind.CIRCLE, 22, t.border));
        }
        if (icon != null) {
            if (selected) {
                icon.setColors(t.primary, Color.WHITE);
            } else {
                icon.setColors(t.mint, t.primary);
            }
        }
        repaint();
    }
}
