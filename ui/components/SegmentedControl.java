package myweigh.ui.components;

import myweigh.ui.theme.Theme;

import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/** Tab-like toggle (e.g. Lose / Maintain / Gain, Recommended / Custom / Goals). */
public class SegmentedControl extends RoundedPanel {
    private final List<Segment> segments = new ArrayList<>();
    private final List<IntConsumer> listeners = new ArrayList<>();
    private int selectedIndex;

    public SegmentedControl(String... options) {
        super(new GridLayout(1, options.length, 4, 0), Theme.get().subtle, null, 14);
        setBorder(new EmptyBorder(4, 4, 4, 4));
        for (int i = 0; i < options.length; i++) {
            Segment s = new Segment(options[i], i);
            segments.add(s);
            add(s);
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        setSelectedIndex(index, false);
    }

    private void setSelectedIndex(int index, boolean notify) {
        selectedIndex = index;
        repaint();
        if (notify) {
            for (IntConsumer l : listeners) {
                l.accept(index);
            }
        }
    }

    public void addChangeListener(IntConsumer listener) {
        listeners.add(listener);
    }

    private final class Segment extends JComponent {
        private final String text;
        private final int index;
        private boolean hover;

        Segment(String text, int index) {
            this.text = text;
            this.index = index;
            setFont(Theme.font(Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isEnabled()) {
                        setSelectedIndex(Segment.this.index, true);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            return new Dimension(fm.stringWidth(text) + 24, 36);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Theme t = Theme.get();
            boolean selected = index == selectedIndex;
            Color fg = selected ? Color.WHITE : hover ? t.text : t.muted;
            if (selected) {
                g2.setColor(t.primary);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
            g2.setFont(getFont());
            g2.setColor(fg);
            FontMetrics fm = g2.getFontMetrics();
            String label = text;
            while (fm.stringWidth(label) > getWidth() - 8 && label.length() > 3) {
                label = label.substring(0, label.length() - 2);
            }
            if (!label.equals(text)) {
                label = label + "…";
            }
            int x = (getWidth() - fm.stringWidth(label)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(label, x, y);
            g2.dispose();
        }
    }
}
