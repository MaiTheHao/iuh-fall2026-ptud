package iuh.fit.gui.layout;

import iuh.fit.core.context.AppContext;
import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.AppLocale;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.page.AbstractPage;
import iuh.fit.gui.page.PageRegistry;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.Dimension;
import java.util.function.Consumer;

public class LayoutHeader extends JMenuBar {

    private final PageRegistry registry;
    private final MenuRegistry menuRegistry = new MenuRegistry();

    private final JMenu settingsMenu = new JMenu();
    private final JMenuItem languageItem = new JMenuItem();
    private final JMenuItem accountItem = new JMenuItem();
    private final JMenuItem logoutItem = new JMenuItem();

    public LayoutHeader(PageRegistry registry, Consumer<Class<? extends AbstractPage>> onSelect,
            Runnable onToggleLocale, Runnable onLogout) {
        this.registry = registry;
        this.setBackground(Theme.BG);
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        this.buildGroupMenus(onSelect);
        this.add(Box.createHorizontalGlue());
        this.buildSettingsMenu(onSelect, onToggleLocale, onLogout);
        this.add(Box.createHorizontalStrut(4));
        this.refreshText();
    }

    private void buildGroupMenus(Consumer<Class<? extends AbstractPage>> onSelect) {
        this.buildHomeItems(onSelect);
        for (MenuGroup group : MenuGroup.values()) {
            if (group == MenuGroup.TRANG_CHU || group == MenuGroup.CAI_DAT) {
                continue;
            }
            this.buildGroupMenu(group, onSelect);
        }
    }

    private void buildHomeItems(Consumer<Class<? extends AbstractPage>> onSelect) {
        for (AbstractPage page : this.registry.all()) {
            if (page.getGroup() != MenuGroup.TRANG_CHU) {
                continue;
            }
            JMenuItem item = this.createAndRegisterItem(page, onSelect, true);
            this.menuRegistry.registerHomeItem(item);
            this.add(item);
        }
    }

    private void buildGroupMenu(MenuGroup group, Consumer<Class<? extends AbstractPage>> onSelect) {
        JMenu menu = null;
        for (AbstractPage page : this.registry.all()) {
            if (page.getGroup() != group) {
                continue;
            }
            if (menu == null) {
                menu = new JMenu(I18nReaderUtil.get(group.getTitleKey()));
                this.menuRegistry.registerMenu(group, menu);
                this.add(menu);
            }
            if (page.hasSeparatorBefore()) {
                menu.addSeparator();
            }
            menu.add(this.createAndRegisterItem(page, onSelect, false));
        }
    }

    private void buildSettingsMenu(Consumer<Class<? extends AbstractPage>> onSelect,
            Runnable onToggleLocale, Runnable onLogout) {
        this.menuRegistry.registerMenu(MenuGroup.CAI_DAT, this.settingsMenu);

        this.languageItem.addActionListener(e -> onToggleLocale.run());
        this.settingsMenu.add(this.languageItem);
        this.settingsMenu.addSeparator();

        for (AbstractPage page : this.registry.all()) {
            if (page.getGroup() == MenuGroup.CAI_DAT) {
                this.settingsMenu.add(this.createAndRegisterItem(page, onSelect, false));
            }
        }
        this.settingsMenu.addSeparator();

        this.accountItem.setEnabled(false);
        this.settingsMenu.add(this.accountItem);
        this.settingsMenu.addSeparator();

        this.logoutItem.setForeground(Theme.DANGER_TEXT);
        this.logoutItem.addActionListener(e -> onLogout.run());
        this.settingsMenu.add(this.logoutItem);

        this.add(this.settingsMenu);
    }

    private JMenuItem createAndRegisterItem(AbstractPage page, Consumer<Class<? extends AbstractPage>> onSelect,
            boolean topLevel) {
        String text = I18nReaderUtil.get(page.getTitleKey());
        JMenuItem item = topLevel ? new TopLevelItem(text) : new JMenuItem(text);
        Class<? extends AbstractPage> type = page.getClass();
        item.addActionListener(e -> onSelect.accept(type));
        this.menuRegistry.registerItem(page, item);
        return item;
    }

    public void setActive(Class<? extends AbstractPage> active) {
        this.menuRegistry.setActive(active);
    }

    public void setMenuVisible(boolean visible) {
        this.menuRegistry.setVisible(visible);
    }

    public void refreshText() {
        this.menuRegistry.refreshText();

        AppLocale locale = AppContext.getInstance().getLocale();
        String localeKey = locale == AppLocale.EN ? "app.localeName.en" : "app.localeName.vn";
        this.languageItem.setText(I18nReaderUtil.get("app.menu.language") + ": " + I18nReaderUtil.get(localeKey));

        NhanVien nhanVien = AppContext.getInstance().getCurrentUser();
        String tenDangNhap = nhanVien != null ? nhanVien.getTenDangNhap() : "";
        this.accountItem.setText(I18nReaderUtil.get("app.menu.account") + ": " + tenDangNhap);

        this.logoutItem.setText(I18nReaderUtil.get("app.menu.logout"));
    }

    private static final class TopLevelItem extends JMenuItem {

        private TopLevelItem(String text) {
            super(text);
        }

        @Override
        public Dimension getMaximumSize() {
            return this.getPreferredSize();
        }
    }
}
