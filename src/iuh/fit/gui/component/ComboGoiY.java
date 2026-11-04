package iuh.fit.gui.component;

import java.util.List;

import javax.swing.JComboBox;

public final class ComboGoiY {

    private ComboGoiY() {
        throw new AssertionError("Utility class");
    }

    public static String giaTri(JComboBox<String> combo) {
        Object value = combo.getEditor().getItem();
        return value == null ? "" : value.toString().trim();
    }

    public static void nap(JComboBox<String> combo, List<String> danhSach, String giaTriGiuLai) {
        String giuLai = giaTriGiuLai == null ? giaTri(combo) : giaTriGiuLai;

        combo.removeAllItems();
        if (danhSach != null) {
            for (String item : danhSach) {
                combo.addItem(item);
            }
        }
        combo.setSelectedItem(giuLai);
    }

    public static void xoa(JComboBox<String> combo) {
        combo.setSelectedItem("");
    }
}
