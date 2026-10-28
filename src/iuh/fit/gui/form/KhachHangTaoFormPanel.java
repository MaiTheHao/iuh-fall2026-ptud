package iuh.fit.gui.form;

/**
 * Form tạo mới khách hàng: mã và ngày tạo do hệ thống sinh nên không hiển thị.
 */
public final class KhachHangTaoFormPanel extends KhachHangFormPanel {

  private static final long serialVersionUID = 1L;

  public KhachHangTaoFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.khachHang.sdt", this.sdtField);
    this.addField("app.khachHang.hoTen", this.hoTenField);
  }
}
