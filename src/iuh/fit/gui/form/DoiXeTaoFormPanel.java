package iuh.fit.gui.form;

public final class DoiXeTaoFormPanel extends DoiXeFormPanel {

  

  public DoiXeTaoFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.danhMucXe.hang", this.cmbHangXe);
    this.addField("app.danhMucXe.dong", this.cmbDongXe);
    this.addField("app.danhMucXe.nam", this.txtNam);
  }
}
