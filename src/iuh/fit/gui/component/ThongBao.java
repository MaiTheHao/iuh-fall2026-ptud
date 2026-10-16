package iuh.fit.gui.component;

import iuh.fit.util.I18nReaderUtil;

import javax.swing.JOptionPane;

import java.awt.Component;

public final class ThongBao {

    private ThongBao() {
        throw new AssertionError("Utility class");
    }

    public static void thongTin(Component parent, String key) {
        JOptionPane.showMessageDialog(
                parent,
                dich(key),
                dich("app.common.notification"),
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void canhBao(Component parent, String key) {
        JOptionPane.showMessageDialog(
                parent,
                dich(key),
                dich("app.common.notification"),
                JOptionPane.WARNING_MESSAGE
        );
    }

    public static void loi(Component parent, String key) {
        JOptionPane.showMessageDialog(
                parent,
                dich(key),
                dich("app.common.error"),
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void loi(Component parent, Exception ex) {
        loi(parent, ex == null ? null : ex.getMessage());
    }

    public static boolean xacNhan(Component parent, String keyNoiDung, String keyTieuDe) {
        return JOptionPane.showConfirmDialog(
                parent,
                dich(keyNoiDung),
                dich(keyTieuDe),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }

    private static String dich(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String value = I18nReaderUtil.get(text);

        return value != null ? value : text;
    }
}
