package myweigh.ui.components;

import myweigh.ui.theme.Theme;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/** Themed single-choice dropdown. */
public class Dropdown<T> extends RoundedPanel {
    private final List<T> items = new ArrayList<>();
    private final List<Consumer<T>> listeners = new ArrayList<>();
    private final Function<T, String> labeler;
    private final JLabel label;
    private T selected;
    private boolean active = true;

    public Dropdown(List<T> items, T selected, Function<T, String> labeler) {
        super(new BorderLayout(8, 0), Theme.get().mint, Theme.get().mintBorder, 14);
        Theme t = Theme.get();
        this.labeler = labeler;
        setBorder(new EmptyBorder(12, 14, 12, 14));
        label = new JLabel();
        label.setFont(Theme.font(Font.BOLD, 14));
        label.setForeground(t.text);
        add(label, BorderLayout.CENTER);
        add(new JLabel(Icons.of(Icons.Kind.CHEVRON_DOWN, 16, t.muted)), BorderLayout.EAST);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setItems(items, selected);
        MouseAdapter open = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (active) {
                    showPopup();
                }
            }
        };
        addMouseListener(open);
        label.addMouseListener(open);
    }

    private void showPopup() {
        Theme t = Theme.get();
        UIManager.put("MenuItem.selectionBackground", t.mint);
        UIManager.put("MenuItem.selectionForeground", t.primary);
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(new LineBorder(t.border));
        menu.setBackground(t.card);
        for (T item : items) {
            JMenuItem mi = new JMenuItem(labeler.apply(item));
            mi.setFont(Theme.font(item.equals(selected) ? Font.BOLD : Font.PLAIN, 14));
            mi.setBackground(t.card);
            mi.setForeground(item.equals(selected) ? t.primary : t.text);
            mi.setBorder(new EmptyBorder(9, 12, 9, 12));
            mi.setPreferredSize(new Dimension(Math.max(getWidth(), 160), mi.getPreferredSize().height));
            mi.addActionListener(e -> setSelected(item, true));
            menu.add(mi);
        }
        menu.show(this, 0, getHeight() + 4);
    }

    public final void setItems(List<T> newItems, T newSelected) {
        items.clear();
        items.addAll(newItems);
        setSelected(newSelected != null && items.contains(newSelected) ? newSelected
                : (items.isEmpty() ? null : items.get(0)), false);
    }

    public T getSelected() {
        return selected;
    }

    public void setSelected(T item) {
        setSelected(item, false);
    }

    private void setSelected(T item, boolean notify) {
        selected = item;
        label.setText(item == null ? "" : labeler.apply(item));
        if (notify) {
            for (Consumer<T> l : listeners) {
                l.accept(item);
            }
        }
    }

    public void addChangeListener(Consumer<T> listener) {
        listeners.add(listener);
    }

    public void setActive(boolean active) {
        this.active = active;
        Theme t = Theme.get();
        setFill(active ? t.mint : t.subtle);
        label.setForeground(active ? t.text : t.muted);
        setCursor(Cursor.getPredefinedCursor(active ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
    }
}
