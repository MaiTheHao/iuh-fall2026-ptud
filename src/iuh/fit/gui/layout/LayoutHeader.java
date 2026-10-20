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
    private final MenuRegistry menuRegistry;

    private final JMenu settingsMenu = new JMenu();
    private final JMenuItem languageItem = new JMenuItem();
    private final JMenuItem accountItem = new JMenuItem();
    private final JMenuItem logoutItem = new JMenuItem();

    public LayoutHeader(PageRegistry registry, Consumer<Class<? extends AbstractPage>> onSelect,
            Runnable onToggleLocale, Runnable onLogout) {
        this.registry = registry;
        this.menuRegistry = new MenuRegistry(registry);
        this.setPreferredSize(new Dimension(0, 48));
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
            JMenuItem item = this.createTopLevelItem(page, onSelect);
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
            menu.add(this.createMenuItem(page, onSelect));
        }
    }

    private void buildSettingsMenu(Consumer<Class<? extends AbstractPage>> onSelect,
            Runnable onToggleLocale, Runnable onLogout) {
        this.menuRegistry.registerMenu(MenuGroup.CAI_DAT, this.settingsMenu);

        this.addLanguageItem(onToggleLocale);
        this.settingsMenu.addSeparator();
        this.addSettingsPageItems(onSelect);
        this.settingsMenu.addSeparator();
        this.addAccountItem();
        this.settingsMenu.addSeparator();
        this.addLogoutItem(onLogout);

        this.add(this.settingsMenu);
    }

    private void addLanguageItem(Runnable onToggleLocale) {
        this.languageItem.addActionListener(e -> onToggleLocale.run());
        this.settingsMenu.add(this.languageItem);
    }

    private void addSettingsPageItems(Consumer<Class<? extends AbstractPage>> onSelect) {
        for (AbstractPage page : this.registry.all()) {
            if (page.getGroup() == MenuGroup.CAI_DAT) {
                this.settingsMenu.add(this.createMenuItem(page, onSelect));
            }
        }
    }

    private void addAccountItem() {
        this.accountItem.setEnabled(false);
        this.settingsMenu.add(this.accountItem);
    }

    private void addLogoutItem(Runnable onLogout) {
        this.logoutItem.setForeground(Theme.DANGER_TEXT);
        this.logoutItem.addActionListener(e -> onLogout.run());
        this.settingsMenu.add(this.logoutItem);
    }

    private JMenuItem createTopLevelItem(AbstractPage page, Consumer<Class<? extends AbstractPage>> onSelect) {
        JMenuItem item = new TopLevelItem(I18nReaderUtil.get(page.getTitleKey()));
        this.registerPageItem(page, item, onSelect);
        return item;
    }

    private JMenuItem createMenuItem(AbstractPage page, Consumer<Class<? extends AbstractPage>> onSelect) {
        JMenuItem item = new JMenuItem(I18nReaderUtil.get(page.getTitleKey()));
        this.registerPageItem(page, item, onSelect);
        return item;
    }

    private void registerPageItem(AbstractPage page, JMenuItem item,
            Consumer<Class<? extends AbstractPage>> onSelect) {
        Class<? extends AbstractPage> type = page.getClass();
        item.addActionListener(e -> onSelect.accept(type));
        this.menuRegistry.registerItem(page, item);
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
        this.languageItem.setText(I18nReaderUtil.get("app.menu.language") + ": " + locale.getI18NBusinessName());

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
