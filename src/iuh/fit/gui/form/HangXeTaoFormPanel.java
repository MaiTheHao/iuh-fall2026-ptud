package iuh.fit.gui.form;

public final class HangXeTaoFormPanel extends HangXeFormPanel {

  

  public HangXeTaoFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.danhMucXe.ten", this.txtTen);
  }
}
