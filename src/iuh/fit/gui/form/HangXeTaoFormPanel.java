package iuh.fit.gui.form;

/** Form thêm hãng xe: chỉ có trường tên, mã do hệ thống sinh. */
public final class HangXeTaoFormPanel extends HangXeFormPanel {

  private static final long serialVersionUID = 1L;

  public HangXeTaoFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.danhMucXe.ten", this.txtTen);
  }
}
