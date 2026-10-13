package iuh.fit.gui.layout;

import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.page.AbstractPage;
import iuh.fit.gui.page.PageRegistry;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Sổ đăng ký thanh menu: item, menu nhóm và item cấp cao; đánh dấu active, làm mới nhãn và ẩn/hiện. */
public final class MenuRegistry {

    private final Map<Class<? extends AbstractPage>, JMenuItem> items = new LinkedHashMap<>();
    private final Map<MenuGroup, JMenu> menus = new LinkedHashMap<>();
    private final List<JMenuItem> homeItems = new ArrayList<>();

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
        this.items.forEach((type, item) ->
                item.setFont(item.getFont().deriveFont(type.equals(active) ? Font.BOLD : Font.PLAIN)));
    }

    public void refreshText() {
        this.menus.forEach((group, menu) -> menu.setText(I18nReaderUtil.get(group.getTitleKey())));
        this.items.forEach((type, item) ->
                item.setText(I18nReaderUtil.get(PageRegistry.getInstance().get(type).getTitleKey())));
    }

    public void setVisible(boolean visible) {
        this.menus.values().forEach(menu -> setVisibleAndEnabled(menu, visible));
        this.homeItems.forEach(item -> setVisibleAndEnabled(item, visible));
    }

    private static void setVisibleAndEnabled(JComponent component, boolean visible) {
        component.setVisible(visible);
        component.setEnabled(visible);
    }
}
