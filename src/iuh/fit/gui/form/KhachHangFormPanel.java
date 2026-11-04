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

  protected final JTextField txtMa = new JTextField(14);
  protected final JTextField txtSdt = new JTextField(14);
  protected final JTextField txtHoTen = new JTextField(14);
  protected final JTextField txtNgayTao = new JTextField(14);

  protected KhachHangFormPanel() {
    Theme.applyReadOnly(this.txtMa);
    Theme.applyReadOnly(this.txtNgayTao);
    this.txtSdt.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
            I18nReaderUtil.get("app.khachHang.sdtGoiY"));

    FormValidationHelper.autoClearOnInput(this.txtSdt);
    FormValidationHelper.autoClearOnInput(this.txtHoTen);
  }

  /** Lớp con dựng bố cục trong constructor của chính nó. */
  protected abstract void dungBoCuc();

  public void napDuLieu(KhachHangFormData data) {
    this.txtMa.setText(data.maKhachHang() == null ? "" : data.maKhachHang());
    this.txtSdt.setText(data.sdt() == null ? "" : data.sdt());
    this.txtHoTen.setText(data.hoTen() == null ? "" : data.hoTen());
    this.txtNgayTao.setText(data.ngayTao() == null ? "" : data.ngayTao());
  }

  public KhachHangFormData layDuLieu() {
    String ma = this.txtMa.getText().trim();
    return new KhachHangFormData(
            ma.isBlank() ? null : ma,
            this.txtSdt.getText().trim(),
            this.txtHoTen.getText().trim(),
            this.txtNgayTao.getText().trim());
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

    String hoTen = this.txtHoTen.getText().trim();

    if (hoTen.isEmpty()) {
      FormValidationHelper.markError(this.txtHoTen,
              I18nReaderUtil.get("app.khachHang.loiHoTen"));
      loiDauTien = this.txtHoTen;
    } else if (hoTen.length() > DO_DAI_HO_TEN_TOI_DA) {
      FormValidationHelper.markError(this.txtHoTen,
              I18nReaderUtil.get("app.khachHang.loiHoTenQuaDai"));
      loiDauTien = this.txtHoTen;
    } else {
      FormValidationHelper.clearError(this.txtHoTen);
    }

    if (!FormValidationHelper.kiemTra(this.txtSdt,
            this.txtSdt.getText().trim().matches(SDT_REGEX),
            "app.khachHang.loiSdt")
            && loiDauTien == null) {
      loiDauTien = this.txtSdt;
    }

    return loiDauTien;
  }
}
