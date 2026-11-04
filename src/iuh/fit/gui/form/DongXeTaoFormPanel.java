package iuh.fit.gui.form;

/** Form thêm dòng xe: chọn hãng và nhập tên, mã do hệ thống sinh. */
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
