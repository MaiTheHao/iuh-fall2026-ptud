package iuh.fit.gui.form;

import iuh.fit.gui.theme.Theme;

/**
 * Form cập nhật nhân viên: tên đăng nhập là khóa nên chỉ đọc, mật khẩu để trống nếu không đổi.
 */
public final class NhanVienSuaFormPanel extends NhanVienFormPanel {

  private static final long serialVersionUID = 1L;

  public NhanVienSuaFormPanel() {
    Theme.applyReadOnly(this.tenDangNhapField);
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.nhanVien.maNhanVien", this.maField);
    this.addField("app.nhanVien.tenDangNhap", this.tenDangNhapField);
    this.addField("app.nhanVien.matKhau", this.matKhauField);
    this.addBlock(taoGoiY("app.nhanVien.matKhauGoiY"));
    this.addField("app.nhanVien.hoTen", this.hoTenField);
    this.addField("app.nhanVien.soDienThoai", this.soDienThoaiField);
    this.addField("app.nhanVien.vaiTro", this.vaiTroCombo);
    this.addField("app.nhanVien.trangThai", this.trangThaiCombo);
    this.addField("app.nhanVien.ngayTao", this.ngayTaoField);
    this.addBlock(this.anhField);
  }
}
