package iuh.fit.gui.form;

public final class KhachHangSuaFormPanel extends KhachHangFormPanel {

  

  public KhachHangSuaFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.khachHang.ma", this.txtMa);
    this.addField("app.khachHang.sdt", this.txtSdt);
    this.addField("app.khachHang.hoTen", this.txtHoTen);
    this.addField("app.khachHang.ngayTao", this.txtNgayTao);
  }
}
