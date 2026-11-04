package iuh.fit.gui.form;

import iuh.fit.gui.theme.Theme;

/**
 * Form cập nhật nhân viên: tên đăng nhập là khóa nên chỉ đọc, mật khẩu để trống nếu không đổi.
 */
public final class NhanVienSuaFormPanel extends NhanVienFormPanel {

  private static final long serialVersionUID = 1L;

  public NhanVienSuaFormPanel() {
    Theme.applyReadOnly(this.txtTenDangNhap);
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.nhanVien.maNhanVien", this.txtMa);
    this.addField("app.nhanVien.tenDangNhap", this.txtTenDangNhap);
    this.addField("app.nhanVien.matKhau", this.txtMatKhau);
    this.addBlock(taoGoiY("app.nhanVien.matKhauGoiY"));
    this.addField("app.nhanVien.hoTen", this.txtHoTen);
    this.addField("app.nhanVien.soDienThoai", this.txtSoDienThoai);
    this.addField("app.nhanVien.vaiTro", this.cmbVaiTro);
    this.addField("app.nhanVien.trangThai", this.cmbTrangThai);
    this.addField("app.nhanVien.ngayTao", this.txtNgayTao);
    this.addBlock(this.pnlAnh);
  }
}
