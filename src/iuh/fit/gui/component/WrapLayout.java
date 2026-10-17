package iuh.fit.gui.component;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

public class WrapLayout extends FlowLayout {

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();

            int maxWidth = target.getWidth()
                    - insets.left
                    - insets.right
                    - getHgap() * 2;

            if (maxWidth <= 0) {
                return;
            }

            List<Component> components = new ArrayList<>();
            List<Dimension> sizes = new ArrayList<>();
            List<Integer> rows = new ArrayList<>();
            List<Integer> rowHeights = new ArrayList<>();

            int x = 0;
            int row = 0;

            rowHeights.add(0);

            for (Component component : target.getComponents()) {
                if (!component.isVisible()) {
                    continue;
                }

                Dimension size = component.getPreferredSize();

                if (x > 0 && x + size.width > maxWidth) {
                    row++;
                    x = 0;
                    rowHeights.add(0);
                }

                components.add(component);
                sizes.add(size);
                rows.add(row);
                rowHeights.set(row, Math.max(rowHeights.get(row), size.height));

                x += size.width + getHgap();
            }

            int left = insets.left + getHgap();
            int top = insets.top + getVgap();
            int currentRow = 0;

            x = left;

            for (int i = 0; i < components.size(); i++) {
                if (rows.get(i) != currentRow) {
                    top += rowHeights.get(currentRow) + getVgap();
                    currentRow = rows.get(i);
                    x = left;
                }

                Dimension size = sizes.get(i);

                components.get(i).setBounds(x, top + (rowHeights.get(currentRow) - size.height) / 2,
                        size.width, size.height);

                x += size.width + getHgap();
            }
        }
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            int width = target.getWidth();

            if (width <= 0) {
                width = 800;
            }

            Insets insets = target.getInsets();
            int maxWidth = width - insets.left - insets.right
                    - getHgap() * 2;

            int rowWidth = 0;
            int rowHeight = 0;
            int totalHeight = getVgap() * 2
                    + insets.top + insets.bottom;
            int totalWidth = 0;

            for (Component component : target.getComponents()) {
                if (!component.isVisible()) {
                    continue;
                }

                Dimension size = component.getPreferredSize();

                if (rowWidth > 0
                        && rowWidth + getHgap() + size.width > maxWidth) {
                    totalWidth = Math.max(totalWidth, rowWidth);
                    totalHeight += rowHeight + getVgap();
                    rowWidth = 0;
                    rowHeight = 0;
                }

                if (rowWidth > 0) {
                    rowWidth += getHgap();
                }

                rowWidth += size.width;
                rowHeight = Math.max(rowHeight, size.height);
            }

            totalWidth = Math.max(totalWidth, rowWidth);
            totalHeight += rowHeight;

            return new Dimension(
                    totalWidth + insets.left + insets.right
                            + getHgap() * 2,
                    totalHeight
            );
        }
    }
}
