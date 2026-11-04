package iuh.fit.gui.form;

public final class DoiXeSuaFormPanel extends DoiXeFormPanel {

  

  public DoiXeSuaFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.danhMucXe.ma", this.txtMa);
    this.addField("app.danhMucXe.hang", this.cmbHangXe);
    this.addField("app.danhMucXe.dong", this.cmbDongXe);
    this.addField("app.danhMucXe.nam", this.txtNam);
  }
}
