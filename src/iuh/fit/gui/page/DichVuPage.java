package iuh.fit.gui.page;

import iuh.fit.enumeration.MenuGroup;

import javax.swing.KeyStroke;

/**
 * Trang quản lý dịch vụ.
 */
public class DichVuPage extends PlaceholderPage {

    public DichVuPage() {
        super(MenuGroup.TO_CHUC, "app.menu.servicePriceList", KeyStroke.getKeyStroke("F6"));
    }
}
