
package iuh.fit.gui.component;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Component;

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

            int x = insets.left + getHgap();
            int y = insets.top + getVgap();
            int rowHeight = 0;

            for (Component component : target.getComponents()) {
                if (!component.isVisible()) {
                    continue;
                }

                Dimension size = component.getPreferredSize();

                if (x > insets.left + getHgap()
                        && x + size.width > insets.left + getHgap() + maxWidth) {
                    x = insets.left + getHgap();
                    y += rowHeight + getVgap();
                    rowHeight = 0;
                }

                component.setBounds(x, y, size.width, size.height);

                x += size.width + getHgap();
                rowHeight = Math.max(rowHeight, size.height);
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
