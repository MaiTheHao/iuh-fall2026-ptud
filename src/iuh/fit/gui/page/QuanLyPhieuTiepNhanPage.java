package iuh.fit.gui.page;

import iuh.fit.enumeration.MenuGroup;

import javax.swing.KeyStroke;

/**
 * Trang quản lý phiếu tiếp nhận.
 */
public class QuanLyPhieuTiepNhanPage extends PlaceholderPage {

    public QuanLyPhieuTiepNhanPage() {
        super(MenuGroup.TIEP_NHAN, "app.menu.intakeSlips", KeyStroke.getKeyStroke("F3"));
    }
}
