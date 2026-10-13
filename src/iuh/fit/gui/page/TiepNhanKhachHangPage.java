package iuh.fit.gui.page;

import iuh.fit.enumeration.MenuGroup;

import javax.swing.KeyStroke;

/**
 * Trang tiếp nhận khách hàng.
 */
public class TiepNhanKhachHangPage extends PlaceholderPage {

    public TiepNhanKhachHangPage() {
        super(MenuGroup.TIEP_NHAN, "app.menu.customerIntake", KeyStroke.getKeyStroke("F2"));
    }
}
