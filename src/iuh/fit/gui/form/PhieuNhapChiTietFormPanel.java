package iuh.fit.gui.form;

import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.theme.Theme;

import javax.swing.JTextField;

/**
 * Form chỉ đọc hiển thị chi tiết phiếu nhập trong dialog.
 */
public final class PhieuNhapChiTietFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  private final JTextField maPhieuField = new JTextField(14);
  private final JTextField moTaField = new JTextField(14);
  private final JTextField thuKhoField = new JTextField(14);
  private final JTextField ngayTaoField = new JTextField(14);
  private final JTextField tongChiPhiField = new JTextField(14);

  public PhieuNhapChiTietFormPanel() {
    for (JTextField field : new JTextField[]{
        this.maPhieuField, this.moTaField, this.thuKhoField,
        this.ngayTaoField, this.tongChiPhiField}) {
      Theme.applyReadOnly(field);
      field.setFocusable(false);
    }

    this.addField("app.phieuNhap.maPhieuNhap", this.maPhieuField);
    this.addField("app.phieuNhap.moTa", this.moTaField);
    this.addField("app.phieuNhap.thuKho", this.thuKhoField);
    this.addField("app.phieuNhap.ngayTao", this.ngayTaoField);
    this.addField("app.phieuNhap.tongChiPhi", this.tongChiPhiField);
  }

  public void napDuLieu(String maPhieu, String moTa, String thuKho, String ngayTao, String tongChiPhi) {
    this.maPhieuField.setText(maPhieu == null ? "" : maPhieu);
    this.moTaField.setText(moTa == null ? "" : moTa);
    this.thuKhoField.setText(thuKho == null ? "" : thuKho);
    this.ngayTaoField.setText(ngayTao == null ? "" : ngayTao);
    this.tongChiPhiField.setText(tongChiPhi == null ? "" : tongChiPhi);
  }
}
