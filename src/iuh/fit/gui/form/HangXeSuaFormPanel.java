package iuh.fit.gui.form;

public final class HangXeSuaFormPanel extends HangXeFormPanel {

  

  public HangXeSuaFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.danhMucXe.ma", this.txtMa);
    this.addField("app.danhMucXe.ten", this.txtTen);
  }
}
