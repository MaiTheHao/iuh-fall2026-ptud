package iuh.fit.gui.form;

/**
 * Form cập nhật khách hàng: hiển thị thêm mã và ngày tạo ở dạng chỉ đọc.
 */
public final class KhachHangSuaFormPanel extends KhachHangFormPanel {

  private static final long serialVersionUID = 1L;

  public KhachHangSuaFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.khachHang.ma", this.txtMa);
    this.addField("app.khachHang.sdt", this.txtSdt);
    this.addField("app.khachHang.hoTen", this.txtHoTen);
    this.addField("app.khachHang.ngayTao", this.txtNgayTao);
  }
}
