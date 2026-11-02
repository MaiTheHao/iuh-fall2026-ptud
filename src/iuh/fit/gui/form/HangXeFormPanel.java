package iuh.fit.gui.form;

import iuh.fit.entity.HangXe;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * Phần dùng chung của form hãng xe: trường nhập liệu, đọc/ghi dữ liệu và kiểm tra hợp lệ.
 * Lớp con quyết định bố cục cho dialog tạo mới hoặc cập nhật.
 */
public abstract class HangXeFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  private static final int DO_DAI_TEN_TOI_DA = 100;

  protected final JTextField maField = new JTextField(14);
  protected final JTextField tenField = new JTextField(14);

  protected HangXeFormPanel() {
    Theme.applyReadOnly(this.maField);
    FormValidationHelper.autoClearOnInput(this.tenField);
  }

  /** Lớp con dựng bố cục trong constructor của chính nó. */
  protected abstract void dungBoCuc();

  public void napDuLieu(HangXe hangXe) {
    this.maField.setText(hangXe == null || hangXe.getMaHangXe() == null ? "" : hangXe.getMaHangXe());
    this.tenField.setText(hangXe == null || hangXe.getTen() == null ? "" : hangXe.getTen());
  }

  public HangXe layDuLieu() {
    String ma = this.maField.getText().trim();
    return new HangXe(ma.isBlank() ? null : ma, this.layTen());
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
    String ten = this.layTen();

    if (ten.isEmpty()) {
      FormValidationHelper.markError(this.tenField, I18nReaderUtil.get("app.danhMucXe.loiTen"));
      return this.tenField;
    }

    if (ten.length() > DO_DAI_TEN_TOI_DA) {
      FormValidationHelper.markError(this.tenField,
              I18nReaderUtil.get("app.danhMucXe.loiTenQuaDai"));
      return this.tenField;
    }

    FormValidationHelper.clearError(this.tenField);
    return null;
  }

  /** Đánh dấu lỗi nghiệp vụ do service trả về lên đúng ô tên. */
  public void hienThiLoiTen(String thongDiep) {
    FormValidationHelper.markError(this.tenField, thongDiep);
    this.tenField.requestFocusInWindow();
  }

  private String layTen() {
    return this.tenField.getText().trim();
  }
}
