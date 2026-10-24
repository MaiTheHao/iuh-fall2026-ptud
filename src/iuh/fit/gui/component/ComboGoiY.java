package iuh.fit.gui.component;

import java.util.List;

import javax.swing.JComboBox;

/**
 * Thao tác dùng chung cho combo nhập tay: đọc giá trị đang gõ và nạp danh sách gợi ý.
 */
public final class ComboGoiY {

    private ComboGoiY() {
        throw new AssertionError("Utility class");
    }

    public static String giaTri(JComboBox<String> combo) {
        Object value = combo.getEditor().getItem();
        return value == null ? "" : value.toString().trim();
    }

    public static void nap(JComboBox<String> combo, List<String> danhSach, String giaTriGiuLai) {
        // Phải đọc giá trị hiện tại trước removeAllItems vì thao tác đó xoá trắng editor.
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
