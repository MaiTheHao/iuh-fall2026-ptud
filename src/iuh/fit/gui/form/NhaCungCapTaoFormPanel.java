package iuh.fit.gui.form;

/**
 * Form thêm nhà cung cấp.
 */
public final class NhaCungCapTaoFormPanel extends NhaCungCapFormPanel {

  

  public NhaCungCapTaoFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.nhaCungCap.ten", this.txtTen);
    this.addField("app.nhaCungCap.soDienThoai", this.txtSdt);
    this.addField("app.nhaCungCap.email", this.txtEmail);
    this.addField("app.nhaCungCap.diaChi", this.txtDiaChi);
    this.addField("app.nhaCungCap.trangThai", this.cmbTrangThai);
    this.addBlock(taoKhungMoTa(this.txaGhiChu));
  }

  @Override
  protected String maHienTai() {
    return null;
  }
}
