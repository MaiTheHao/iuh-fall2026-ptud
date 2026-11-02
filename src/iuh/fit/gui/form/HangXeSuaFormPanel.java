package iuh.fit.gui.form;

/** Form cập nhật hãng xe: hiển thị thêm mã ở dạng chỉ đọc. */
public final class HangXeSuaFormPanel extends HangXeFormPanel {

  private static final long serialVersionUID = 1L;

  public HangXeSuaFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.danhMucXe.ma", this.maField);
    this.addField("app.danhMucXe.ten", this.tenField);
  }
}
