package iuh.fit.gui.component;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

public class WrapLayout extends FlowLayout {

    private record Dong(List<Component> thanhPhan, int rong, int cao) {
    }

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

            int left = insets.left + getHgap();
            int top = insets.top + getVgap();

            for (Dong dong : chiaDong(target, maxWidth)) {
                int x = left;

                for (Component component : dong.thanhPhan()) {
                    Dimension size = component.getPreferredSize();

                    component.setBounds(x, top + (dong.cao() - size.height) / 2,
                            size.width, size.height);

                    x += size.width + getHgap();
                }

                top += dong.cao() + getVgap();
            }
        }
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return this.preferredLayoutSize(target, -1);
    }

    /**
     * Đo chiều cao theo đúng bề rộng sẽ dùng khi vẽ, để số hàng không phụ thuộc vào
     * thời điểm container còn width 0. width <= 0 nghĩa là tự dò bề rộng.
     */
    public Dimension preferredLayoutSize(Container target, int width) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int maxWidth = this.doRongDo(target, width) - insets.left - insets.right
                    - getHgap() * 2;

            List<Dong> dsDong = chiaDong(target, maxWidth);
            int totalWidth = 0;
            int totalHeight = getVgap() * 2
                    + insets.top + insets.bottom;

            for (int i = 0; i < dsDong.size(); i++) {
                Dong dong = dsDong.get(i);

                totalWidth = Math.max(totalWidth, dong.rong());
                totalHeight += dong.cao();

                if (i < dsDong.size() - 1) {
                    totalHeight += getVgap();
                }
            }

            return new Dimension(
                    totalWidth + insets.left + insets.right
                            + getHgap() * 2,
                    totalHeight
            );
        }
    }

    private int doRongDo(Container target, int width) {
        if (width > 0) {
            return width;
        }

        int rong = target.getWidth();

        if (rong > 0) {
            return rong;
        }

        Container cha = target.getParent();

        if (cha != null && cha.getWidth() > 0) {
            return cha.getWidth();
        }

        return 800;
    }

    private List<Dong> chiaDong(Container target, int maxWidth) {
        List<Dong> dsDong = new ArrayList<>();
        List<Component> thanhPhan = new ArrayList<>();
        int rong = 0;
        int cao = 0;

        for (Component component : target.getComponents()) {
            if (!component.isVisible()) {
                continue;
            }

            Dimension size = component.getPreferredSize();

            if (!thanhPhan.isEmpty()
                    && rong + getHgap() + size.width > maxWidth) {
                dsDong.add(new Dong(thanhPhan, rong, cao));
                thanhPhan = new ArrayList<>();
                rong = 0;
                cao = 0;
            }

            if (!thanhPhan.isEmpty()) {
                rong += getHgap();
            }

            thanhPhan.add(component);
            rong += size.width;
            cao = Math.max(cao, size.height);
        }

        if (!thanhPhan.isEmpty()) {
            dsDong.add(new Dong(thanhPhan, rong, cao));
        }

        return dsDong;
    }
}
