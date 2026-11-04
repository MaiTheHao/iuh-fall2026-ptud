package iuh.fit.gui.form;

/**
 * Form cập nhật dịch vụ: hiển thị thêm mã ở dạng chỉ đọc.
 */
public final class DichVuSuaFormPanel extends DichVuFormPanel {

  

  public DichVuSuaFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.dichVu.ma", this.txtMa);
    this.addField("app.dichVu.ten", this.txtTen);
    this.addField("app.dichVu.moTa", this.moTaScroll);
    this.addBlock(this.pnlAnh);
    this.addField("app.dichVu.donGia", this.txtDonGia);
  }
}
