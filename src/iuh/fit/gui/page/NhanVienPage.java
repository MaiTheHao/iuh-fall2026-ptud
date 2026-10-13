package iuh.fit.gui.page;

import iuh.fit.enumeration.MenuGroup;

import javax.swing.KeyStroke;

/**
 * Trang quản lý nhân viên.
 */
public class NhanVienPage extends PlaceholderPage {

    public NhanVienPage() {
        super(MenuGroup.TO_CHUC, "app.menu.employees", KeyStroke.getKeyStroke("F10"), true);
    }
}
