package iuh.fit.gui.form;

/**
 * Form cập nhật phụ tùng: hiển thị thêm mã ở dạng chỉ đọc.
 */
public final class PhuTungSuaFormPanel extends PhuTungFormPanel {

  private static final long serialVersionUID = 1L;

  public PhuTungSuaFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.phuTung.ma", this.maField);
    this.addField("app.phuTung.ten", this.tenField);
    this.addField("app.phuTung.moTa", this.moTaScroll);
    this.addBlock(this.anhField);
    this.addField("app.phuTung.donGia", this.donGiaField);
  }
}
