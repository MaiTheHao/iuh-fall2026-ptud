package iuh.fit.gui.form;

import iuh.fit.dto.NhaCungCapFormData;

import javax.swing.JTextField;

public final class NhaCungCapSuaFormPanel extends NhaCungCapFormPanel {

  

  private final JTextField txtMa = new JTextField(14);

  public NhaCungCapSuaFormPanel() {
    this.txtMa.setEditable(false);
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.nhaCungCap.ma", this.txtMa);
    this.addField("app.nhaCungCap.ten", this.txtTen);
    this.addField("app.nhaCungCap.soDienThoai", this.txtSdt);
    this.addField("app.nhaCungCap.email", this.txtEmail);
    this.addField("app.nhaCungCap.diaChi", this.txtDiaChi);
    this.addField("app.nhaCungCap.trangThai", this.cmbTrangThai);
    this.addBlock(taoKhungMoTa(this.txaGhiChu));
  }

  @Override
  protected String maHienTai() {
    return this.txtMa.getText().trim();
  }

  @Override
  public void napDuLieu(NhaCungCapFormData duLieu) {
    this.txtMa.setText(duLieu.maNhaCungCap() == null ? "" : duLieu.maNhaCungCap());
    super.napDuLieu(duLieu);
  }
}
