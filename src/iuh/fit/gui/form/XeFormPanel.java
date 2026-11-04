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

  protected final JTextField txtBienSo = new JTextField(14);
  protected final JComboBox<HangXe> cmbHangXe = new JComboBox<>();
  protected final JComboBox<DongXe> cmbDongXe = new JComboBox<>();
  protected final JComboBox<DoiXe> cmbDoiXe = new JComboBox<>();
  protected final JTextField txtOdo = new JTextField(14);

  protected XeFormPanel() {
    this.txtOdo.setHorizontalAlignment(SwingConstants.RIGHT);

    FormValidationHelper.autoClearOnInput(this.txtBienSo);
    FormValidationHelper.autoClearOnInput(this.txtOdo);
  }

  /** Lớp con dựng bố cục trong constructor của chính nó. */
  protected abstract void dungBoCuc();

  public void napDuLieu(XeFormData data) {
    this.txtBienSo.setText(data.bienSo() == null ? "" : data.bienSo());
    this.chonHangXe(data.maHangXe());
    this.chonDongXe(data.maDongXe());
    this.chonDoiXe(data.maDoiXe());
    this.txtOdo.setText(data.odo() == null ? "" : String.valueOf(data.odo()));
  }

  public void chonHangXe(String maHangXe) {
    if (maHangXe == null) {
      return;
    }
    for (int i = 0; i < this.cmbHangXe.getItemCount(); i++) {
      HangXe item = this.cmbHangXe.getItemAt(i);
      if (item != null && maHangXe.equals(item.getMaHangXe())) {
        this.cmbHangXe.setSelectedIndex(i);
        return;
      }
    }
  }

  public void chonDongXe(String maDongXe) {
    if (maDongXe == null) {
      return;
    }
    for (int i = 0; i < this.cmbDongXe.getItemCount(); i++) {
      DongXe item = this.cmbDongXe.getItemAt(i);
      if (item != null && maDongXe.equals(item.getMaDongXe())) {
        this.cmbDongXe.setSelectedIndex(i);
        return;
      }
    }
  }

  private void chonDoiXe(String maDoiXe) {
    if (maDoiXe == null) {
      return;
    }
    for (int i = 0; i < this.cmbDoiXe.getItemCount(); i++) {
      DoiXe item = this.cmbDoiXe.getItemAt(i);
      if (item != null && maDoiXe.equals(item.getMaDoiXe())) {
        this.cmbDoiXe.setSelectedIndex(i);
        return;
      }
    }
  }

  public XeFormData layDuLieu() {
    Object hangSel = this.cmbHangXe.getSelectedItem();
    Object dongSel = this.cmbDongXe.getSelectedItem();
    Object doiSel = this.cmbDoiXe.getSelectedItem();
    String maHangXe = hangSel instanceof HangXe ? ((HangXe) hangSel).getMaHangXe() : null;
    String maDongXe = dongSel instanceof DongXe ? ((DongXe) dongSel).getMaDongXe() : null;
    String maDoiXe = doiSel instanceof DoiXe ? ((DoiXe) doiSel).getMaDoiXe() : null;
    return new XeFormData(
            this.txtBienSo.getText().trim().toUpperCase(),
            maHangXe,
            maDongXe,
            maDoiXe,
            NumberParseUtil.toIntegerOrNull(this.txtOdo.getText()));
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

    String bienSo = this.txtBienSo.getText().trim().toUpperCase();

    if (!FormValidationHelper.kiemTra(this.txtBienSo,
            bienSo.matches(BIEN_SO_REGEX),
            "app.xe.loiBienSo")) {
      loiDauTien = this.txtBienSo;
    }

    Object hangSel = this.cmbHangXe.getSelectedItem();
    if (!(hangSel instanceof HangXe)) {
      FormValidationHelper.markError(this.cmbHangXe,
              I18nReaderUtil.get("app.danhMucXe.chuaChonHang"));
      if (loiDauTien == null) {
        loiDauTien = this.cmbHangXe;
      }
    } else {
      FormValidationHelper.clearError(this.cmbHangXe);
    }

    Object dongSel = this.cmbDongXe.getSelectedItem();
    if (!(dongSel instanceof DongXe)) {
      FormValidationHelper.markError(this.cmbDongXe,
              I18nReaderUtil.get("app.danhMucXe.chuaChonDong"));
      if (loiDauTien == null) {
        loiDauTien = this.cmbDongXe;
      }
    } else {
      FormValidationHelper.clearError(this.cmbDongXe);
    }

    Object doiSel = this.cmbDoiXe.getSelectedItem();
    if (!(doiSel instanceof DoiXe)) {
      FormValidationHelper.markError(this.cmbDoiXe,
              I18nReaderUtil.get("app.danhMucXe.chuaChonDoi"));
      if (loiDauTien == null) {
        loiDauTien = this.cmbDoiXe;
      }
    } else {
      FormValidationHelper.clearError(this.cmbDoiXe);
    }

    Integer odo = NumberParseUtil.toIntegerOrNull(this.txtOdo.getText());

    if (odo != null && odo < 0) {
      FormValidationHelper.markError(this.txtOdo,
              I18nReaderUtil.get("app.xe.loiOdo"));
      if (loiDauTien == null) {
        loiDauTien = this.txtOdo;
      }
    } else {
      FormValidationHelper.clearError(this.txtOdo);
    }

    return loiDauTien;
  }

  public JComboBox<HangXe> getHangXeCombo() {
    return this.cmbHangXe;
  }

  public JComboBox<DongXe> getDongXeCombo() {
    return this.cmbDongXe;
  }

  public JComboBox<DoiXe> getDoiXeCombo() {
    return this.cmbDoiXe;
  }
}
