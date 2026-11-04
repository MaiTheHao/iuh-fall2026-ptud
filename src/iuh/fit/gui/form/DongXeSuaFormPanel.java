package iuh.fit.gui.form;

/** Form cập nhật dòng xe: hiển thị thêm mã ở dạng chỉ đọc. */
public final class DongXeSuaFormPanel extends DongXeFormPanel {

  

  public DongXeSuaFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.danhMucXe.ma", this.txtMa);
    this.addField("app.danhMucXe.hang", this.cmbHangXe);
    this.addField("app.danhMucXe.ten", this.txtTen);
  }
}
