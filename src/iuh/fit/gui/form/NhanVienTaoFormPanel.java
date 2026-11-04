package iuh.fit.gui.form;

import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComponent;

/**
 * Form tạo mới nhân viên: bắt buộc có tên đăng nhập và mật khẩu.
 */
public final class NhanVienTaoFormPanel extends NhanVienFormPanel {

  

  public NhanVienTaoFormPanel() {
    Theme.applyEditable(this.txtTenDangNhap);
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.nhanVien.tenDangNhap", this.txtTenDangNhap);
    this.addField("app.nhanVien.matKhau", this.txtMatKhau);
    this.addField("app.nhanVien.hoTen", this.txtHoTen);
    this.addField("app.nhanVien.soDienThoai", this.txtSoDienThoai);
    this.addField("app.nhanVien.vaiTro", this.cmbVaiTro);
    this.addField("app.nhanVien.trangThai", this.cmbTrangThai);
    this.addBlock(this.pnlAnh);
  }

  @Override
  protected JComponent kiemTraRieng() {
    if (this.txtTenDangNhap.getText().trim().isEmpty()) {
      FormValidationHelper.markError(this.txtTenDangNhap,
              I18nReaderUtil.get("app.nhanVien.loiTenDangNhap"));
      return this.txtTenDangNhap;
    }

    if (new String(this.txtMatKhau.getPassword()).isBlank()) {
      FormValidationHelper.markError(this.txtMatKhau,
              I18nReaderUtil.get("app.nhanVien.loiMatKhau"));
      return this.txtMatKhau;
    }

    return null;
  }
}
