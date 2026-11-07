package iuh.fit.gui.layout;

import iuh.fit.core.context.AppContext;
import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.AppLocale;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.VaiTro;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.LucideIcon;
import iuh.fit.gui.page.AbstractPage;
import iuh.fit.gui.page.PageRegistry;
import iuh.fit.gui.page.permission.VaiTroQuyen;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
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
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class LayoutHeader extends JMenuBar {

	private static final int CHIEU_CAO = 52;
	private static final int CO_ICON = 18;
	private static final int DO_DAY_CHI_BAO = 2;

	private final PageRegistry registry;
	private final Map<Class<? extends AbstractPage>, JMenuItem> itemTheoTrang = new LinkedHashMap<>();
	private final Map<MenuGroup, JMenu> menuTheoNhom = new EnumMap<>(MenuGroup.class);
	private final List<JMenuItem> mucTrangChu = new ArrayList<>();

	private final Consumer<Class<? extends AbstractPage>> onSelect;

	private VaiTro vaiTroLanDungMenuGanNhat;

	private final JMenu mnuSettings = new JMenu();
	private final JPanel pnlAccount = new AccountBox();
	private final JLabel lblAccountName = new JLabel();
	private final JLabel lblAccountRole = new JLabel();
	private final JButton btnLanguage = new JButton();
	private final JButton btnLogout = new JButton();

	public LayoutHeader(PageRegistry registry, Consumer<Class<? extends AbstractPage>> onSelect,
			Runnable onToggleLocale, Runnable onLogout) {
		this.registry = registry;
		this.onSelect = onSelect;

		this.setPreferredSize(new Dimension(0, CHIEU_CAO));
		this.setBackground(Theme.palette().BG());
		this.setBorder(Theme.vienDuoi());

		this.buildAccountBox();
		this.buildLanguageButton(onToggleLocale);
		this.buildLogoutButton(onLogout);

		this.dungLaiMenu(null);
		this.refreshText();
	}

	private void dungLaiMenu(VaiTro vaiTro) {
		this.removeAll();

		this.itemTheoTrang.clear();
		this.menuTheoNhom.clear();
		this.mucTrangChu.clear();

		Map<MenuGroup, List<AbstractPage>> trangTheoNhom = new EnumMap<>(MenuGroup.class);

		for (AbstractPage page : this.registry.all()) {
			if (!VaiTroQuyen.duocHien(vaiTro, page.getClass())) {
				continue;
			}

			MenuGroup group = page.getGroup();

			if (group != null) {
				trangTheoNhom.computeIfAbsent(group, g -> new ArrayList<>()).add(page);
			}
		}

		this.buildGroupMenus(trangTheoNhom, this.onSelect);

		this.add(Box.createHorizontalGlue());
		this.add(this.pnlAccount);

		List<AbstractPage> trangCaiDat = trangTheoNhom.getOrDefault(MenuGroup.CAI_DAT, List.of());

		if (!trangCaiDat.isEmpty()) {
			this.add(Box.createHorizontalStrut(8));
			this.buildSettingsMenu(trangCaiDat, this.onSelect);
		}

		this.add(Box.createHorizontalStrut(8));
		this.add(this.btnLanguage);
		this.add(Box.createHorizontalStrut(4));
		this.add(this.btnLogout);
		this.add(Box.createHorizontalStrut(4));

		this.vaiTroLanDungMenuGanNhat = vaiTro;

		this.revalidate();
		this.repaint();
	}

	private void buildGroupMenus(Map<MenuGroup, List<AbstractPage>> trangTheoNhom,
			Consumer<Class<? extends AbstractPage>> onSelect) {
		this.buildHomeItems(trangTheoNhom.getOrDefault(MenuGroup.TRANG_CHU, List.of()), onSelect);
		for (MenuGroup group : MenuGroup.values()) {
			if (group == MenuGroup.TRANG_CHU || group == MenuGroup.CAI_DAT) {
				continue;
			}
			this.buildGroupMenu(group, trangTheoNhom.getOrDefault(group, List.of()), onSelect);
		}
	}

	private void buildHomeItems(List<AbstractPage> pages, Consumer<Class<? extends AbstractPage>> onSelect) {
		for (AbstractPage page : pages) {
			TopLevelItem item = new TopLevelItem(I18nReaderUtil.get(page.getTitleKey()),
					bieuTuong(MenuGroup.TRANG_CHU));
			this.registerHomeItem(item);
			this.registerPageItem(page, item, onSelect);
			this.add(item);
		}
	}

	private void buildGroupMenu(MenuGroup group, List<AbstractPage> pages,
			Consumer<Class<? extends AbstractPage>> onSelect) {
		JMenu menu = null;
		for (AbstractPage page : pages) {
			if (menu == null) {
				menu = new GroupMenu(I18nReaderUtil.get(group.getTitleKey()), bieuTuong(group));
				this.registerMenu(group, menu);
				this.add(menu);
			}
			if (page.hasSeparatorBefore()) {
				menu.addSeparator();
			}
			menu.add(this.createMenuItem(page, onSelect));
		}
	}

	private void buildSettingsMenu(List<AbstractPage> pages, Consumer<Class<? extends AbstractPage>> onSelect) {

		if (pages.isEmpty()) {
			return;
		}

		this.mnuSettings.removeAll();
		this.mnuSettings.setIcon(taoBieuTuong(bieuTuong(MenuGroup.CAI_DAT), false));

		this.registerMenu(MenuGroup.CAI_DAT, this.mnuSettings);

		for (AbstractPage page : pages) {
			this.mnuSettings.add(this.createMenuItem(page, onSelect));
		}

		this.add(this.mnuSettings);
	}

	private void buildAccountBox() {
		this.pnlAccount.setOpaque(false);
		this.lblAccountName.setForeground(Theme.palette().INK());
		this.lblAccountName.setFont(Theme.fieldLabelFont());
		this.lblAccountRole.setForeground(Theme.palette().INK_MUTED());
		this.lblAccountRole.setFont(nhoChu());
		this.pnlAccount.add(this.lblAccountName);
		this.pnlAccount.add(this.lblAccountRole);
	}

	private void buildLanguageButton(Runnable onToggleLocale) {
		this.btnLanguage.setIcon(new LucideIcon("globe", CO_ICON, Theme.palette().ACCENT()));
		Theme.applyPillButton(this.btnLanguage, Theme.palette().ACCENT_SOFT(), Theme.palette().ACCENT());
		this.btnLanguage.setFocusPainted(false);
		this.btnLanguage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.btnLanguage.addActionListener(e -> onToggleLocale.run());
	}

	private void buildLogoutButton(Runnable onLogout) {
		this.btnLogout.setIcon(new LucideIcon("log-out", CO_ICON, Theme.palette().DANGER_TEXT()));
		this.btnLogout.setContentAreaFilled(false);
		this.btnLogout.setFocusPainted(false);
		this.btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.btnLogout.addActionListener(e -> onLogout.run());
	}

	private JMenuItem createMenuItem(AbstractPage page, Consumer<Class<? extends AbstractPage>> onSelect) {
		JMenuItem item = new JMenuItem(I18nReaderUtil.get(page.getTitleKey()));
		this.registerPageItem(page, item, onSelect);
		return item;
	}

	private void registerPageItem(AbstractPage page, JMenuItem item, Consumer<Class<? extends AbstractPage>> onSelect) {
		Class<? extends AbstractPage> type = page.getClass();
		item.addActionListener(e -> onSelect.accept(type));
		this.itemTheoTrang.put(type, item);
	}

	private void registerHomeItem(JMenuItem item) {
		this.mucTrangChu.add(item);
	}

	private void registerMenu(MenuGroup group, JMenu menu) {
		this.menuTheoNhom.put(group, menu);
	}

	public void setActive(Class<? extends AbstractPage> active) {
		this.itemTheoTrang.forEach((type, item) -> danhDau(item, type.equals(active)));

		AbstractPage trang = this.registry.get(active);
		MenuGroup activeGroup = trang == null ? null : trang.getGroup();
		this.menuTheoNhom.forEach((group, menu) -> danhDau(menu, group == activeGroup));
	}

	public void setMenuVisible(boolean visible) {
		this.menuTheoNhom.values().forEach(menu -> setVisibleAndEnabled(menu, visible));
		this.mucTrangChu.forEach(item -> setVisibleAndEnabled(item, visible));
		this.pnlAccount.setVisible(visible);
		this.btnLanguage.setVisible(visible);
		this.btnLogout.setVisible(visible);
	}

	public void refreshText() {
	    NhanVien nhanVien = AppContext.getInstance().getCurrentUser();

	    VaiTro vaiTro = nhanVien == null ? null : nhanVien.getVaiTro();

	    if (!Objects.equals(this.vaiTroLanDungMenuGanNhat, vaiTro)) {
	        this.dungLaiMenu(vaiTro);
	    }

	    this.menuTheoNhom.forEach((group, menu) ->
	            menu.setText(I18nReaderUtil.get(group.getTitleKey())));

	    this.itemTheoTrang.forEach((type, item) ->
	            item.setText(I18nReaderUtil.get(this.registry.get(type).getTitleKey())));

	    AppLocale locale = AppContext.getInstance().getLocale();

	    this.btnLanguage.setText(locale == AppLocale.VN ? "VI" : "EN");
	    this.btnLanguage.setToolTipText(
	            I18nReaderUtil.get("app.menu.language") + " (F11)");

	    this.lblAccountName.setText(
	            nhanVien == null ? "" : nhanVien.getTenHienThi());

	    this.lblAccountRole.setText(
	            nhanVien == null || nhanVien.getVaiTro() == null
	                    ? ""
	                    : nhanVien.getVaiTro().getI18NBusinessName());

	    this.btnLogout.setToolTipText(
	            I18nReaderUtil.get("app.menu.logout"));
	}

	private static void danhDau(JMenuItem item, boolean active) {
		item.setForeground(active ? Theme.palette().ACCENT() : Theme.palette().INK());
		if (item instanceof MenuActive nut) {
			nut.setActive(active);
		}
	}

	private static void setVisibleAndEnabled(JComponent component, boolean visible) {
		component.setVisible(visible);
		component.setEnabled(visible);
	}

	private static LucideIcon taoBieuTuong(String ten, boolean active) {
		return new LucideIcon(ten, CO_ICON, active ? Theme.palette().ACCENT() : Theme.palette().INK_SECONDARY());
	}

	private static void ganBieuTuong(AbstractButton component, String ten) {
		component.putClientProperty("bieuTuong", ten);
		component.setIcon(taoBieuTuong(ten, false));
	}

	private static void lamMoiBieuTuong(AbstractButton component, boolean active) {
		component.setIcon(taoBieuTuong((String) component.getClientProperty("bieuTuong"), active));
	}

	private static Dimension kichThuocToiDa(JComponent component) {
		return component.getPreferredSize();
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

	private static void veChiBaoDuoi(Graphics g, JComponent component, boolean active) {
		if (!active) {
			return;
		}
		Graphics2D doHoa = (Graphics2D) g.create();
		try {
			doHoa.setColor(Theme.palette().ACCENT());
			doHoa.fillRect(0, component.getHeight() - DO_DAY_CHI_BAO, component.getWidth(), DO_DAY_CHI_BAO);
		} finally {
			doHoa.dispose();
		}
	}

	private static final class AccountBox extends JPanel {

		private AccountBox() {
			super(new GridLayout(2, 1, 0, 0));
		}

		@Override
		public Dimension getMaximumSize() {
			return kichThuocToiDa(this);
		}
	}

	private static final class TopLevelItem extends JMenuItem implements MenuActive {

		private boolean active;

		private TopLevelItem(String text, String iconName) {
			super(text);
			ganBieuTuong(this, iconName);
		}

		@Override
		public void setActive(boolean active) {
			this.active = active;
			lamMoiBieuTuong(this, active);
			this.repaint();
		}

		@Override
		public Dimension getMaximumSize() {
			return kichThuocToiDa(this);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			veChiBaoDuoi(g, this, this.active);
		}
	}

	private static final class GroupMenu extends JMenu implements MenuActive {

		private boolean active;

		private GroupMenu(String text, String iconName) {
			super(text);
			ganBieuTuong(this, iconName);
		}

		@Override
		public void setActive(boolean active) {
			this.active = active;
			lamMoiBieuTuong(this, active);
			this.repaint();
		}

		@Override
		public Dimension getMaximumSize() {
			return kichThuocToiDa(this);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			veChiBaoDuoi(g, this, this.active);
		}
	}

	private interface MenuActive {

		void setActive(boolean active);
	}
}
