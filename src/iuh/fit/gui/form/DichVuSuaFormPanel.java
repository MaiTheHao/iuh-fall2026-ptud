package iuh.fit.gui.form;

/**
 * Form cập nhật dịch vụ: hiển thị thêm mã ở dạng chỉ đọc.
 */
public final class DichVuSuaFormPanel extends DichVuFormPanel {

  private static final long serialVersionUID = 1L;

  public DichVuSuaFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.dichVu.ma", this.maField);
    this.addField("app.dichVu.ten", this.tenField);
    this.addField("app.dichVu.moTa", this.moTaScroll);
    this.addBlock(this.anhField);
    this.addField("app.dichVu.donGia", this.donGiaField);
  }
}
