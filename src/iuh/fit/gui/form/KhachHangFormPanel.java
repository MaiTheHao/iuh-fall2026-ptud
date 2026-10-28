package iuh.fit.gui.form;

import com.formdev.flatlaf.FlatClientProperties;
import iuh.fit.dto.KhachHangFormData;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * Phần dùng chung của form khách hàng: trường nhập liệu, đọc/ghi dữ liệu và kiểm tra hợp lệ.
 * Lớp con quyết định bố cục cho dialog tạo mới hoặc cập nhật.
 */
public abstract class KhachHangFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  private static final String SDT_REGEX = "\\d{10}";
  private static final int DO_DAI_HO_TEN_TOI_DA = 50;

  protected final JTextField maField = new JTextField(14);
  protected final JTextField sdtField = new JTextField(14);
  protected final JTextField hoTenField = new JTextField(14);
  protected final JTextField ngayTaoField = new JTextField(14);

  protected KhachHangFormPanel() {
    Theme.applyReadOnly(this.maField);
    Theme.applyReadOnly(this.ngayTaoField);
    this.sdtField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
            I18nReaderUtil.get("app.khachHang.sdtGoiY"));

    FormValidationHelper.autoClearOnInput(this.sdtField);
    FormValidationHelper.autoClearOnInput(this.hoTenField);
  }

  /** Lớp con dựng bố cục trong constructor của chính nó. */
  protected abstract void dungBoCuc();

  public void napDuLieu(KhachHangFormData data) {
    this.maField.setText(data.maKhachHang() == null ? "" : data.maKhachHang());
    this.sdtField.setText(data.sdt() == null ? "" : data.sdt());
    this.hoTenField.setText(data.hoTen() == null ? "" : data.hoTen());
    this.ngayTaoField.setText(data.ngayTao() == null ? "" : data.ngayTao());
  }

  public KhachHangFormData layDuLieu() {
    String ma = this.maField.getText().trim();
    return new KhachHangFormData(
            ma.isBlank() ? null : ma,
            this.sdtField.getText().trim(),
            this.hoTenField.getText().trim(),
            this.ngayTaoField.getText().trim());
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

    String hoTen = this.hoTenField.getText().trim();

    if (hoTen.isEmpty()) {
      FormValidationHelper.markError(this.hoTenField,
              I18nReaderUtil.get("app.khachHang.loiHoTen"));
      loiDauTien = this.hoTenField;
    } else if (hoTen.length() > DO_DAI_HO_TEN_TOI_DA) {
      FormValidationHelper.markError(this.hoTenField,
              I18nReaderUtil.get("app.khachHang.loiHoTenQuaDai"));
      loiDauTien = this.hoTenField;
    } else {
      FormValidationHelper.clearError(this.hoTenField);
    }

    if (!FormValidationHelper.kiemTra(this.sdtField,
            this.sdtField.getText().trim().matches(SDT_REGEX),
            "app.khachHang.loiSdt")
            && loiDauTien == null) {
      loiDauTien = this.sdtField;
    }

    return loiDauTien;
  }
}
