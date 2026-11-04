package iuh.fit.gui.form;

/** Form cập nhật đời xe: hiển thị thêm mã ở dạng chỉ đọc. */
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
