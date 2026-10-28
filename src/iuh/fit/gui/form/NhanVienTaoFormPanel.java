package iuh.fit.gui.form;

import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComponent;

/**
 * Form tạo mới nhân viên: bắt buộc có tên đăng nhập và mật khẩu.
 */
public final class NhanVienTaoFormPanel extends NhanVienFormPanel {

  private static final long serialVersionUID = 1L;

  public NhanVienTaoFormPanel() {
    Theme.applyEditable(this.tenDangNhapField);
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.nhanVien.tenDangNhap", this.tenDangNhapField);
    this.addField("app.nhanVien.matKhau", this.matKhauField);
    this.addField("app.nhanVien.hoTen", this.hoTenField);
    this.addField("app.nhanVien.soDienThoai", this.soDienThoaiField);
    this.addField("app.nhanVien.vaiTro", this.vaiTroCombo);
    this.addField("app.nhanVien.trangThai", this.trangThaiCombo);
    this.addBlock(this.anhField);
  }

  @Override
  protected JComponent kiemTraRieng() {
    if (this.tenDangNhapField.getText().trim().isEmpty()) {
      FormValidationHelper.markError(this.tenDangNhapField,
              I18nReaderUtil.get("app.nhanVien.loiTenDangNhap"));
      return this.tenDangNhapField;
    }

    if (new String(this.matKhauField.getPassword()).isBlank()) {
      FormValidationHelper.markError(this.matKhauField,
              I18nReaderUtil.get("app.nhanVien.loiMatKhau"));
      return this.matKhauField;
    }

    return null;
  }
}
