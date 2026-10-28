package iuh.fit.gui.layout;

import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.page.AbstractPage;
import iuh.fit.gui.page.PageRegistry;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sổ đăng ký thanh menu: item, menu nhóm và item cấp cao; đánh dấu active, làm mới nhãn và ẩn/hiện.
 */
public final class MenuRegistry {

    private static final int DO_DAY_CHI_BAO = 2;

    private final PageRegistry registry;
    private final Map<Class<? extends AbstractPage>, JMenuItem> items = new LinkedHashMap<>();
    private final Map<MenuGroup, JMenu> menus = new LinkedHashMap<>();
    private final List<JMenuItem> homeItems = new ArrayList<>();

    public MenuRegistry(PageRegistry registry) {
        this.registry = registry;
    }

    public void registerItem(AbstractPage page, JMenuItem item) {
        this.items.put(page.getClass(), item);
    }

    public void registerHomeItem(JMenuItem item) {
        this.homeItems.add(item);
    }

    public void registerMenu(MenuGroup group, JMenu menu) {
        this.menus.put(group, menu);
    }

    public void setActive(Class<? extends AbstractPage> active) {
        this.items.forEach((type, item) -> danhDau(item, type.equals(active)));

        AbstractPage trang = this.registry.get(active);
        MenuGroup activeGroup = trang == null ? null : trang.getGroup();
        this.menus.forEach((group, menu) -> danhDau(menu, group == activeGroup));
    }

    public void refreshText() {
        this.menus.forEach((group, menu) -> menu.setText(I18nReaderUtil.get(group.getTitleKey())));
        this.items.forEach((type, item) ->
                item.setText(I18nReaderUtil.get(this.registry.get(type).getTitleKey())));
    }

    public void setVisible(boolean visible) {
        this.menus.values().forEach(menu -> setVisibleAndEnabled(menu, visible));
        this.homeItems.forEach(item -> setVisibleAndEnabled(item, visible));
    }

    private static void danhDau(JMenuItem item, boolean active) {
        item.setForeground(active ? Theme.ACCENT : Theme.INK);
        if (item instanceof MenuActive nut) {
            nut.setActive(active);
        }
    }

    private static void setVisibleAndEnabled(JComponent component, boolean visible) {
        component.setVisible(visible);
        component.setEnabled(visible);
    }

    static void veChiBaoDuoi(Graphics g, JComponent component, boolean active) {
        if (!active) {
            return;
        }
        Graphics2D doHoa = (Graphics2D) g.create();
        try {
            doHoa.setColor(Theme.ACCENT);
            doHoa.fillRect(0, component.getHeight() - DO_DAY_CHI_BAO,
                    component.getWidth(), DO_DAY_CHI_BAO);
        } finally {
            doHoa.dispose();
        }
    }
}

/** Item/menu cấp 1 có thể hiển thị chỉ báo mục đang chọn. */
interface MenuActive {

    void setActive(boolean active);
}
