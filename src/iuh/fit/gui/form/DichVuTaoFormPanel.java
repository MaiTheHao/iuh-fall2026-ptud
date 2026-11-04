package iuh.fit.gui.form;

/**
 * Form tạo mới dịch vụ: mã do hệ thống sinh nên không hiển thị.
 */
public final class DichVuTaoFormPanel extends DichVuFormPanel {

  

  public DichVuTaoFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.dichVu.ten", this.txtTen);
    this.addField("app.dichVu.moTa", this.moTaScroll);
    this.addBlock(this.pnlAnh);
    this.addField("app.dichVu.donGia", this.txtDonGia);
  }
}
