package myweigh.ui.components;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;

/**
 * Vertical stack: every child gets the full width (up to a maximum, centered)
 * and the height it needs at that width. This is what lets text wrap and
 * cards reflow when the window changes between phone and laptop sizes.
 */
public class Stack extends JPanel implements Scrollable, SelfMeasuring {
    private final int gap;
    private int maxContentWidth = Integer.MAX_VALUE;

    public Stack(int gap) {
        this(gap, new Insets(0, 0, 0, 0));
    }

    public Stack(int gap, Insets padding) {
        this.gap = gap;
        setOpaque(false);
        setPadding(padding);
        setLayout(new StackLayout());
    }

    public void setPadding(Insets p) {
        setBorder(BorderFactory.createEmptyBorder(p.top, p.left, p.bottom, p.right));
        revalidate();
    }

    public void setMaxContentWidth(int width) {
        this.maxContentWidth = width;
        revalidate();
    }

    /** Height a component needs when given this width. */
    public static int measure(Component c, int width) {
        if (width <= 0) {
            return c.getPreferredSize().height;
        }
        c.setSize(width, Math.max(1, c.getHeight()));
        if (!(c instanceof SelfMeasuring) && c instanceof Container) {
            layoutTree((Container) c);
        }
        return c.getPreferredSize().height;
    }

    private static void layoutTree(Container c) {
        c.doLayout();
        for (Component child : c.getComponents()) {
            if (child instanceof Container && !(child instanceof SelfMeasuring)) {
                layoutTree((Container) child);
            }
        }
    }

    private int availableWidth() {
        Container parent = getParent();
        if (parent instanceof JViewport && parent.getWidth() > 0) {
            return parent.getWidth();
        }
        return getWidth();
    }

    private final class StackLayout implements LayoutManager {
        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(0, 0);
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            Insets in = getInsets();
            int prefWidth = 0;
            for (Component c : getComponents()) {
                if (c.isVisible() && !(c instanceof SelfMeasuring)) {
                    prefWidth = Math.max(prefWidth, Math.min(c.getPreferredSize().width, 600));
                }
            }
            prefWidth = Math.min(prefWidth, maxContentWidth);
            int width = availableWidth();
            if (width <= 0) {
                width = prefWidth + in.left + in.right;
            }
            int inner = Math.max(0, Math.min(width - in.left - in.right, maxContentWidth));
            int height = in.top + in.bottom;
            boolean first = true;
            for (Component c : getComponents()) {
                if (!c.isVisible()) {
                    continue;
                }
                if (!first) {
                    height += gap;
                }
                first = false;
                height += measure(c, inner);
            }
            return new Dimension(prefWidth + in.left + in.right, height);
        }

        @Override
        public void layoutContainer(Container parent) {
            Insets in = getInsets();
            int available = Math.max(0, getWidth() - in.left - in.right);
            int inner = Math.min(available, maxContentWidth);
            int x = in.left + (available - inner) / 2;
            int y = in.top;
            for (Component c : getComponents()) {
                if (!c.isVisible()) {
                    continue;
                }
                int h = measure(c, inner);
                c.setBounds(x, y, inner, h);
                y += h + gap;
            }
        }
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 18;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return Math.max(60, visibleRect.height - 40);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        Container parent = getParent();
        return parent instanceof JViewport && parent.getHeight() > getPreferredSize().height;
    }
}
