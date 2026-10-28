package iuh.fit.gui.component;

import iuh.fit.util.I18nReaderUtil;

import javax.swing.JOptionPane;

import java.awt.Component;
import java.text.MessageFormat;

public final class ThongBao {

  private static final String KEY_THONG_BAO = "app.common.notification";
  private static final String KEY_LOI = "app.common.error";
  private static final String KEY_LOI_KHONG_XAC_DINH = "app.common.loiKhongXacDinh";

  private ThongBao() {
    throw new AssertionError("Utility class");
  }

  public static void thongTin(Component parent, String key) {
    hienThi(parent, dich(key), KEY_THONG_BAO, JOptionPane.INFORMATION_MESSAGE);
  }

  public static void thongTinDinhDang(Component parent, String key, Object... args) {
    hienThi(parent, dinhDang(dich(key), args), KEY_THONG_BAO, JOptionPane.INFORMATION_MESSAGE);
  }

  public static void canhBao(Component parent, String key) {
    hienThi(parent, dich(key), KEY_THONG_BAO, JOptionPane.WARNING_MESSAGE);
  }

  public static void canhBaoDinhDang(Component parent, String key, Object... args) {
    hienThi(parent, dinhDang(dich(key), args), KEY_THONG_BAO, JOptionPane.WARNING_MESSAGE);
  }

  public static boolean xacNhanDinhDang(Component parent,
          String keyNoiDung,
          String keyTieuDe,
          Object... args) {
    return JOptionPane.showConfirmDialog(
            parent,
            dinhDang(dich(keyNoiDung), args),
            dich(keyTieuDe),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
  }

  public static void loi(Component parent, String key) {
    hienThi(parent, dich(key), KEY_LOI, JOptionPane.ERROR_MESSAGE);
  }

  public static void loi(Component parent, Exception ex) {
    String key = ex == null ? null : ex.getMessage();
    loi(parent, key == null || key.isBlank() ? KEY_LOI_KHONG_XAC_DINH : key);
  }

  public static boolean xacNhan(Component parent, String keyNoiDung, String keyTieuDe) {
    return JOptionPane.showConfirmDialog(
            parent,
            dich(keyNoiDung),
            dich(keyTieuDe),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
  }

  private static void hienThi(Component parent, String noiDung, String keyTieuDe, int messageType) {
    JOptionPane.showMessageDialog(parent, noiDung, dich(keyTieuDe), messageType);
  }

  private static String dinhDang(String pattern, Object... args) {
    if (args == null || args.length == 0) {
      return pattern;
    }
    return MessageFormat.format(pattern, args);
  }

  private static String dich(String text) {
    if (text == null || text.isBlank()) {
      return "";
    }

    String value = I18nReaderUtil.get(text);

    return value != null ? value : text;
  }
}
