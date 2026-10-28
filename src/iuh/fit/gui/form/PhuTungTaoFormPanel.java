package iuh.fit.gui.form;

/**
 * Form tạo mới phụ tùng: mã do hệ thống sinh nên không hiển thị.
 */
public final class PhuTungTaoFormPanel extends PhuTungFormPanel {

  private static final long serialVersionUID = 1L;

  public PhuTungTaoFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.phuTung.ten", this.tenField);
    this.addField("app.phuTung.moTa", this.moTaScroll);
    this.addBlock(this.anhField);
    this.addField("app.phuTung.donGia", this.donGiaField);
  }
}
