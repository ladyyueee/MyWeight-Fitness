package myweigh.ui.components;

import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Grid whose column count follows the available width: one column on a
 * phone, up to {@code maxColumns} on a laptop. Cells in a row share a height.
 */
public class ResponsiveGrid extends JPanel implements SelfMeasuring {
    private final int minCellWidth;
    private final int maxColumns;
    private final int gap;

    public ResponsiveGrid(int minCellWidth, int maxColumns, int gap) {
        this.minCellWidth = minCellWidth;
        this.maxColumns = maxColumns;
        this.gap = gap;
        setOpaque(false);
        setLayout(new GridFlowLayout());
    }

    private List<Component> visibleChildren() {
        List<Component> list = new ArrayList<>();
        for (Component c : getComponents()) {
            if (c.isVisible()) {
                list.add(c);
            }
        }
        return list;
    }

    public int columnsFor(int width, int count) {
        if (width <= 0 || count == 0) {
            return 1;
        }
        int columns = (width + gap) / (minCellWidth + gap);
        return Math.max(1, Math.min(Math.min(maxColumns, count), columns));
    }

    private final class GridFlowLayout implements LayoutManager {
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
            int width = getWidth() > 0 ? getWidth() : minCellWidth + in.left + in.right;
            int inner = width - in.left - in.right;
            List<Component> children = visibleChildren();
            int cols = columnsFor(inner, children.size());
            int cellWidth = (inner - (cols - 1) * gap) / cols;
            int height = in.top + in.bottom;
            for (int i = 0; i < children.size(); i += cols) {
                int rowHeight = 0;
                for (int j = i; j < Math.min(i + cols, children.size()); j++) {
                    rowHeight = Math.max(rowHeight, Stack.measure(children.get(j), cellWidth));
                }
                height += rowHeight + (i > 0 ? gap : 0);
            }
            return new Dimension(minCellWidth + in.left + in.right, height);
        }

        @Override
        public void layoutContainer(Container parent) {
            Insets in = getInsets();
            int inner = getWidth() - in.left - in.right;
            List<Component> children = visibleChildren();
            int cols = columnsFor(inner, children.size());
            int cellWidth = (inner - (cols - 1) * gap) / cols;
            int y = in.top;
            for (int i = 0; i < children.size(); i += cols) {
                int end = Math.min(i + cols, children.size());
                int rowHeight = 0;
                for (int j = i; j < end; j++) {
                    rowHeight = Math.max(rowHeight, Stack.measure(children.get(j), cellWidth));
                }
                int x = in.left;
                for (int j = i; j < end; j++) {
                    children.get(j).setBounds(x, y, cellWidth, rowHeight);
                    x += cellWidth + gap;
                }
                y += rowHeight + gap;
            }
        }
    }
}
