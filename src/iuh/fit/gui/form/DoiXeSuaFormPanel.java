package iuh.fit.gui.form;

/** Form cập nhật đời xe: hiển thị thêm mã ở dạng chỉ đọc. */
public final class DoiXeSuaFormPanel extends DoiXeFormPanel {

  private static final long serialVersionUID = 1L;

  public DoiXeSuaFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.danhMucXe.ma", this.maField);
    this.addField("app.danhMucXe.hang", this.hangXeCombo);
    this.addField("app.danhMucXe.dong", this.dongXeCombo);
    this.addField("app.danhMucXe.nam", this.namField);
  }
}
