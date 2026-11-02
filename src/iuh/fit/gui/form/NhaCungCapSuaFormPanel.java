package iuh.fit.gui.form;

import iuh.fit.dto.NhaCungCapFormData;

import javax.swing.JTextField;

/**
 * Form cập nhật nhà cung cấp: thêm mã read-only.
 */
public final class NhaCungCapSuaFormPanel extends NhaCungCapFormPanel {

  private static final long serialVersionUID = 1L;

  private final JTextField maField = new JTextField(14);

  public NhaCungCapSuaFormPanel() {
    this.maField.setEditable(false);
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.nhaCungCap.ma", this.maField);
    this.addField("app.nhaCungCap.ten", this.tenField);
    this.addField("app.nhaCungCap.soDienThoai", this.sdtField);
    this.addField("app.nhaCungCap.email", this.emailField);
    this.addField("app.nhaCungCap.diaChi", this.diaChiField);
    this.addField("app.nhaCungCap.trangThai", this.trangThaiCombo);
    this.addBlock(taoKhungMoTa(this.ghiChuArea));
  }

  @Override
  protected String maHienTai() {
    return this.maField.getText().trim();
  }

  @Override
  public void napDuLieu(NhaCungCapFormData duLieu) {
    this.maField.setText(duLieu.maNhaCungCap() == null ? "" : duLieu.maNhaCungCap());
    super.napDuLieu(duLieu);
  }
}
