package iuh.fit.gui.form;

/** Form cập nhật dòng xe: hiển thị thêm mã ở dạng chỉ đọc. */
public final class DongXeSuaFormPanel extends DongXeFormPanel {

  private static final long serialVersionUID = 1L;

  public DongXeSuaFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.danhMucXe.ma", this.maField);
    this.addField("app.danhMucXe.hang", this.hangXeCombo);
    this.addField("app.danhMucXe.ten", this.tenField);
  }
}
