package iuh.fit.gui.form;

public final class DongXeTaoFormPanel extends DongXeFormPanel {

  

  public DongXeTaoFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.danhMucXe.hang", this.cmbHangXe);
    this.addField("app.danhMucXe.ten", this.txtTen);
  }
}
