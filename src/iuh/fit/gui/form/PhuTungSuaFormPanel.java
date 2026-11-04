package iuh.fit.gui.form;

/**
 * Form cập nhật phụ tùng: hiển thị thêm mã ở dạng chỉ đọc.
 */
public final class PhuTungSuaFormPanel extends PhuTungFormPanel {

  

  public PhuTungSuaFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.phuTung.ma", this.txtMa);
    this.addField("app.phuTung.ten", this.txtTen);
    this.addField("app.phuTung.moTa", this.moTaScroll);
    this.addField("app.phuTung.donGia", this.txtDonGia);
    this.addField("app.phuTung.nhaCungCap", this.thanhNhaCungCap());
    this.addBlock(this.pnlAnh);
  }
}
