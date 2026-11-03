package iuh.fit.gui.form;

import iuh.fit.dto.XeFormData;
import iuh.fit.entity.DoiXe;
import iuh.fit.entity.DongXe;
import iuh.fit.entity.HangXe;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Phần dùng chung của form xe: trường nhập liệu, đọc/ghi dữ liệu và kiểm tra hợp lệ.
 * Lớp con quyết định bố cục cho dialog tạo mới hoặc cập nhật.
 */
public abstract class XeFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  private static final String BIEN_SO_REGEX = "^[0-9]{2}[A-Z]{1,2}[0-9]{4,6}$";

  protected final JTextField bienSoField = new JTextField(14);
  protected final JComboBox<HangXe> hangXeCombo = new JComboBox<>();
  protected final JComboBox<DongXe> dongXeCombo = new JComboBox<>();
  protected final JComboBox<DoiXe> doiXeCombo = new JComboBox<>();
  protected final JTextField odoField = new JTextField(14);

  protected XeFormPanel() {
    this.odoField.setHorizontalAlignment(SwingConstants.RIGHT);

    FormValidationHelper.autoClearOnInput(this.bienSoField);
    FormValidationHelper.autoClearOnInput(this.odoField);
  }

  /** Lớp con dựng bố cục trong constructor của chính nó. */
  protected abstract void dungBoCuc();

  public void napDuLieu(XeFormData data) {
    this.bienSoField.setText(data.bienSo() == null ? "" : data.bienSo());
    this.chonHangXe(data.maHangXe());
    this.chonDongXe(data.maDongXe());
    this.chonDoiXe(data.maDoiXe());
    this.odoField.setText(data.odo() == null ? "" : String.valueOf(data.odo()));
  }

  public void chonHangXe(String maHangXe) {
    if (maHangXe == null) {
      return;
    }
    for (int i = 0; i < this.hangXeCombo.getItemCount(); i++) {
      HangXe item = this.hangXeCombo.getItemAt(i);
      if (item != null && maHangXe.equals(item.getMaHangXe())) {
        this.hangXeCombo.setSelectedIndex(i);
        return;
      }
    }
  }

  public void chonDongXe(String maDongXe) {
    if (maDongXe == null) {
      return;
    }
    for (int i = 0; i < this.dongXeCombo.getItemCount(); i++) {
      DongXe item = this.dongXeCombo.getItemAt(i);
      if (item != null && maDongXe.equals(item.getMaDongXe())) {
        this.dongXeCombo.setSelectedIndex(i);
        return;
      }
    }
  }

  private void chonDoiXe(String maDoiXe) {
    if (maDoiXe == null) {
      return;
    }
    for (int i = 0; i < this.doiXeCombo.getItemCount(); i++) {
      DoiXe item = this.doiXeCombo.getItemAt(i);
      if (item != null && maDoiXe.equals(item.getMaDoiXe())) {
        this.doiXeCombo.setSelectedIndex(i);
        return;
      }
    }
  }

  public XeFormData layDuLieu() {
    Object hangSel = this.hangXeCombo.getSelectedItem();
    Object dongSel = this.dongXeCombo.getSelectedItem();
    Object doiSel = this.doiXeCombo.getSelectedItem();
    String maHangXe = hangSel instanceof HangXe ? ((HangXe) hangSel).getMaHangXe() : null;
    String maDongXe = dongSel instanceof DongXe ? ((DongXe) dongSel).getMaDongXe() : null;
    String maDoiXe = doiSel instanceof DoiXe ? ((DoiXe) doiSel).getMaDoiXe() : null;
    return new XeFormData(
            this.bienSoField.getText().trim().toUpperCase(),
            maHangXe,
            maDongXe,
            maDoiXe,
            NumberParseUtil.toIntegerOrNull(this.odoField.getText()));
  }

  public final boolean validateInput() {
    JComponent loiChung = this.kiemTraChung();
    JComponent loiRieng = this.kiemTraRieng();
    JComponent loiDauTien = loiChung != null ? loiChung : loiRieng;

    if (loiDauTien != null) {
      loiDauTien.requestFocusInWindow();
      return false;
    }

    return true;
  }

  /** Chỗ mở rộng cho rule chỉ áp dụng ở một mode. */
  protected JComponent kiemTraRieng() {
    return null;
  }

  protected JComponent kiemTraChung() {
    JComponent loiDauTien = null;

    String bienSo = this.bienSoField.getText().trim().toUpperCase();

    if (!FormValidationHelper.kiemTra(this.bienSoField,
            bienSo.matches(BIEN_SO_REGEX),
            "app.xe.loiBienSo")) {
      loiDauTien = this.bienSoField;
    }

    Object hangSel = this.hangXeCombo.getSelectedItem();
    if (!(hangSel instanceof HangXe)) {
      FormValidationHelper.markError(this.hangXeCombo,
              I18nReaderUtil.get("app.danhMucXe.chuaChonHang"));
      if (loiDauTien == null) {
        loiDauTien = this.hangXeCombo;
      }
    } else {
      FormValidationHelper.clearError(this.hangXeCombo);
    }

    Object dongSel = this.dongXeCombo.getSelectedItem();
    if (!(dongSel instanceof DongXe)) {
      FormValidationHelper.markError(this.dongXeCombo,
              I18nReaderUtil.get("app.danhMucXe.chuaChonDong"));
      if (loiDauTien == null) {
        loiDauTien = this.dongXeCombo;
      }
    } else {
      FormValidationHelper.clearError(this.dongXeCombo);
    }

    Object doiSel = this.doiXeCombo.getSelectedItem();
    if (!(doiSel instanceof DoiXe)) {
      FormValidationHelper.markError(this.doiXeCombo,
              I18nReaderUtil.get("app.danhMucXe.chuaChonDoi"));
      if (loiDauTien == null) {
        loiDauTien = this.doiXeCombo;
      }
    } else {
      FormValidationHelper.clearError(this.doiXeCombo);
    }

    Integer odo = NumberParseUtil.toIntegerOrNull(this.odoField.getText());

    if (odo != null && odo < 0) {
      FormValidationHelper.markError(this.odoField,
              I18nReaderUtil.get("app.xe.loiOdo"));
      if (loiDauTien == null) {
        loiDauTien = this.odoField;
      }
    } else {
      FormValidationHelper.clearError(this.odoField);
    }

    return loiDauTien;
  }

  public JComboBox<HangXe> getHangXeCombo() {
    return this.hangXeCombo;
  }

  public JComboBox<DongXe> getDongXeCombo() {
    return this.dongXeCombo;
  }

  public JComboBox<DoiXe> getDoiXeCombo() {
    return this.doiXeCombo;
  }
}
