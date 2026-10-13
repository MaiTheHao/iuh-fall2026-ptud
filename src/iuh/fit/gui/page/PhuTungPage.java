package iuh.fit.gui.page;

import iuh.fit.enumeration.MenuGroup;

import javax.swing.KeyStroke;

/**
 * Trang quản lý phụ tùng.
 */
public class PhuTungPage extends PlaceholderPage {

    public PhuTungPage() {
        super(MenuGroup.TO_CHUC, "app.menu.inventory", KeyStroke.getKeyStroke("F4"));
    }
}
