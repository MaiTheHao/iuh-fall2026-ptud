package iuh.fit.gui.page;

import iuh.fit.enumeration.MenuGroup;

import javax.swing.KeyStroke;

/**
 * Trang lập phiếu nhập phụ tùng.
 */
public class LapPhieuNhapPage extends PlaceholderPage {

    public LapPhieuNhapPage() {
        super(MenuGroup.NHAP_PHU_TUNG, "app.menu.partsReceipts", KeyStroke.getKeyStroke("F5"));
    }
}
