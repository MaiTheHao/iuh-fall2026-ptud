package iuh.fit.gui.form;

public final class PhuTungTaoFormPanel extends PhuTungFormPanel {

  

  public PhuTungTaoFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.phuTung.ten", this.txtTen);
    this.addField("app.phuTung.moTa", this.moTaScroll);
    this.addField("app.phuTung.donGia", this.txtDonGia);
    this.addField("app.phuTung.nhaCungCap", this.thanhNhaCungCap());
    this.addBlock(this.pnlAnh);
  }
}
