package iuh.fit.gui.page;

import iuh.fit.enumeration.MenuGroup;

import javax.swing.KeyStroke;

/**
 * Trang lập hóa đơn thanh toán.
 */
public class LapHoaDonPage extends PlaceholderPage {

    public LapHoaDonPage() {
        super(MenuGroup.THANH_TOAN, "app.menu.invoices", KeyStroke.getKeyStroke("F8"));
    }
}
