package iuh.fit.gui.layout;

import iuh.fit.core.context.AppContext;
import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.AppLocale;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.LucideIcon;
import iuh.fit.gui.page.AbstractPage;
import iuh.fit.gui.page.PageRegistry;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.function.Consumer;

public class LayoutHeader extends JMenuBar {

    private static final int CHIEU_CAO = 52;
    private static final int CO_ICON = 18;

    private final PageRegistry registry;
    private final MenuRegistry menuRegistry;

    private final JMenu settingsMenu = new JMenu();
    private final JPanel accountBox = new JPanel(new GridLayout(2, 1, 0, 0));
    private final JLabel accountNameLabel = new JLabel();
    private final JLabel accountRoleLabel = new JLabel();
    private final JButton languageButton = new JButton();
    private final JButton logoutButton = new JButton();

    public LayoutHeader(PageRegistry registry, Consumer<Class<? extends AbstractPage>> onSelect,
            Runnable onToggleLocale, Runnable onLogout) {
        this.registry = registry;
        this.menuRegistry = new MenuRegistry(registry);
        this.setPreferredSize(new Dimension(0, CHIEU_CAO));
        this.setBackground(Theme.BG);
        this.setBorder(Theme.vienDuoi());

        this.buildGroupMenus(onSelect);
        this.add(Box.createHorizontalGlue());
        this.buildSettingsMenu(onSelect);
        this.add(Box.createHorizontalStrut(8));
        this.buildAccountBox();
        this.add(this.accountBox);
        this.add(Box.createHorizontalStrut(8));
        this.buildLanguageButton(onToggleLocale);
        this.buildLogoutButton(onLogout);
        this.add(this.languageButton);
        this.add(Box.createHorizontalStrut(4));
        this.add(this.logoutButton);
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
            TopLevelItem item = new TopLevelItem(I18nReaderUtil.get(page.getTitleKey()), bieuTuong(MenuGroup.TRANG_CHU));
            this.menuRegistry.registerHomeItem(item);
            this.registerPageItem(page, item, onSelect);
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
                menu = new GroupMenu(I18nReaderUtil.get(group.getTitleKey()), bieuTuong(group));
                this.menuRegistry.registerMenu(group, menu);
                this.add(menu);
            }
            if (page.hasSeparatorBefore()) {
                menu.addSeparator();
            }
            menu.add(this.createMenuItem(page, onSelect));
        }
    }

    private void buildSettingsMenu(Consumer<Class<? extends AbstractPage>> onSelect) {
        this.settingsMenu.setIcon(new LucideIcon(bieuTuong(MenuGroup.CAI_DAT), CO_ICON, Theme.palette().INK_SECONDARY()));
        this.menuRegistry.registerMenu(MenuGroup.CAI_DAT, this.settingsMenu);
        for (AbstractPage page : this.registry.all()) {
            if (page.getGroup() == MenuGroup.CAI_DAT) {
                this.settingsMenu.add(this.createMenuItem(page, onSelect));
            }
        }
        this.add(this.settingsMenu);
    }

    private void buildAccountBox() {
        this.accountBox.setOpaque(false);
        this.accountNameLabel.setForeground(Theme.INK);
        this.accountNameLabel.setFont(Theme.fieldLabelFont());
        this.accountRoleLabel.setForeground(Theme.palette().INK_MUTED());
        this.accountRoleLabel.setFont(nhoChu());
        this.accountBox.add(this.accountNameLabel);
        this.accountBox.add(this.accountRoleLabel);
    }

    private void buildLanguageButton(Runnable onToggleLocale) {
        this.languageButton.setIcon(new LucideIcon("globe", CO_ICON, Theme.palette().ACCENT()));
        Theme.applyPillButton(this.languageButton, Theme.palette().ACCENT_SOFT(), Theme.palette().ACCENT());
        this.languageButton.setFocusPainted(false);
        this.languageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.languageButton.addActionListener(e -> onToggleLocale.run());
    }

    private void buildLogoutButton(Runnable onLogout) {
        this.logoutButton.setIcon(new LucideIcon("log-out", CO_ICON, Theme.DANGER_TEXT));
        this.logoutButton.setContentAreaFilled(false);
        this.logoutButton.setFocusPainted(false);
        this.logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.logoutButton.addActionListener(e -> onLogout.run());
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
        this.accountBox.setVisible(visible);
        this.languageButton.setVisible(visible);
        this.logoutButton.setVisible(visible);
    }

    public void refreshText() {
        this.menuRegistry.refreshText();

        AppLocale locale = AppContext.getInstance().getLocale();
        this.languageButton.setText(locale == AppLocale.VN ? "VI" : "EN");
        this.languageButton.setToolTipText(I18nReaderUtil.get("app.menu.language") + " (F11)");

        NhanVien nhanVien = AppContext.getInstance().getCurrentUser();
        this.accountNameLabel.setText(nhanVien == null ? "" : this.tenHienThi(nhanVien));
        this.accountRoleLabel.setText(nhanVien == null || nhanVien.getVaiTro() == null
                ? ""
                : nhanVien.getVaiTro().getI18NBusinessName());

        this.logoutButton.setToolTipText(I18nReaderUtil.get("app.menu.logout"));
    }

    private String tenHienThi(NhanVien nhanVien) {
        String hoTen = nhanVien.getHoTen();
        return hoTen == null || hoTen.isBlank() ? nhanVien.getTenDangNhap() : hoTen;
    }

    private static String bieuTuong(MenuGroup group) {
        return switch (group) {
            case TRANG_CHU -> "house";
            case TO_CHUC -> "users";
            case KE_HOACH -> "calendar-days";
            case TIEP_NHAN -> "clipboard-list";
            case NHAP_PHU_TUNG -> "package";
            case THANH_TOAN -> "credit-card";
            case TONG_KET -> "chart-column";
            case CAI_DAT -> "settings";
        };
    }

    private static Font nhoChu() {
        Font coSo = UIManager.getFont("Label.font");
        return coSo.deriveFont(Math.max(coSo.getSize2D() - 2f, 10f));
    }

    private static final class TopLevelItem extends JMenuItem implements MenuActive {

        private static final long serialVersionUID = 1L;

        private final String iconName;
        private boolean active;

        private TopLevelItem(String text, String iconName) {
            super(text);
            this.iconName = iconName;
            this.setIcon(new LucideIcon(this.iconName, CO_ICON, Theme.palette().INK_SECONDARY()));
        }

        @Override
        public void setActive(boolean active) {
            this.active = active;
            this.setIcon(new LucideIcon(this.iconName, CO_ICON, active ? Theme.ACCENT : Theme.palette().INK_SECONDARY()));
            this.repaint();
        }

        @Override
        public Dimension getMaximumSize() {
            return this.getPreferredSize();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            MenuRegistry.veChiBaoDuoi(g, this, this.active);
        }
    }

    private static final class GroupMenu extends JMenu implements MenuActive {

        private static final long serialVersionUID = 1L;

        private final String iconName;
        private boolean active;

        private GroupMenu(String text, String iconName) {
            super(text);
            this.iconName = iconName;
            this.setIcon(new LucideIcon(this.iconName, CO_ICON, Theme.palette().INK_SECONDARY()));
        }

        @Override
        public void setActive(boolean active) {
            this.active = active;
            this.setIcon(new LucideIcon(this.iconName, CO_ICON, active ? Theme.ACCENT : Theme.palette().INK_SECONDARY()));
            this.repaint();
        }

        @Override
        public Dimension getMaximumSize() {
            return this.getPreferredSize();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            MenuRegistry.veChiBaoDuoi(g, this, this.active);
        }
    }
}
