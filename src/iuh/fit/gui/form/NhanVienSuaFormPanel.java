package iuh.fit.gui.form;

import iuh.fit.gui.Theme;

public final class NhanVienSuaFormPanel extends NhanVienFormPanel {

  

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
