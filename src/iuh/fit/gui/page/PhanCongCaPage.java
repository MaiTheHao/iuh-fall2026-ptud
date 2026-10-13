package iuh.fit.gui.page;

import iuh.fit.enumeration.MenuGroup;

import javax.swing.KeyStroke;

/**
 * Trang phân công ca làm việc.
 */
public class PhanCongCaPage extends PlaceholderPage {

    public PhanCongCaPage() {
        super(MenuGroup.KE_HOACH, "app.menu.shiftAssignment", KeyStroke.getKeyStroke("F9"));
    }
}
