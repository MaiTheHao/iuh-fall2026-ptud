package iuh.fit.gui.form;

public final class KhachHangTaoFormPanel extends KhachHangFormPanel {

  

  public KhachHangTaoFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.khachHang.sdt", this.txtSdt);
    this.addField("app.khachHang.hoTen", this.txtHoTen);
  }
}
