package iuh.fit.gui.page;

import iuh.fit.enumeration.MenuGroup;

import javax.swing.KeyStroke;

/**
 * Trang quản lý voucher.
 */
public class VoucherPage extends PlaceholderPage {

    public VoucherPage() {
        super(MenuGroup.TO_CHUC, "app.menu.vouchers", KeyStroke.getKeyStroke("F7"));
    }
}
