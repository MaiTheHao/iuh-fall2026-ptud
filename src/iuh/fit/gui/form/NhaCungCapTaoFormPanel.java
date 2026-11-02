package iuh.fit.gui.form;

/**
 * Form thêm nhà cung cấp.
 */
public final class NhaCungCapTaoFormPanel extends NhaCungCapFormPanel {

  private static final long serialVersionUID = 1L;

  public NhaCungCapTaoFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.nhaCungCap.ten", this.tenField);
    this.addField("app.nhaCungCap.soDienThoai", this.sdtField);
    this.addField("app.nhaCungCap.email", this.emailField);
    this.addField("app.nhaCungCap.diaChi", this.diaChiField);
    this.addField("app.nhaCungCap.trangThai", this.trangThaiCombo);
    this.addBlock(taoKhungMoTa(this.ghiChuArea));
  }

  @Override
  protected String maHienTai() {
    return null;
  }
}
