package iuh.fit.gui.form;

import iuh.fit.dto.NhaCungCapFormData;
import iuh.fit.enumeration.TrangThaiNhaCungCap;
import iuh.fit.gui.component.BoHienThiI18n;
import iuh.fit.gui.component.FormFieldPanel;

import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Phần dùng chung của form nhà cung cấp.
 */
public abstract class NhaCungCapFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  protected final JTextField tenField = new JTextField(14);
  protected final JTextField sdtField = new JTextField(14);
  protected final JTextField emailField = new JTextField(14);
  protected final JTextField diaChiField = new JTextField(14);
  protected final JComboBox<TrangThaiNhaCungCap> trangThaiCombo = new JComboBox<>(TrangThaiNhaCungCap.values());
  protected final JTextArea ghiChuArea = new JTextArea(4, 20);

  protected NhaCungCapFormPanel() {
    this.trangThaiCombo.setRenderer(BoHienThiI18n.tao());
    this.trangThaiCombo.setSelectedItem(TrangThaiNhaCungCap.HOAT_DONG);
  }

  protected abstract void dungBoCuc();

  public void napDuLieu(NhaCungCapFormData duLieu) {
    this.tenField.setText(duLieu.tenNhaCungCap() == null ? "" : duLieu.tenNhaCungCap());
    this.sdtField.setText(duLieu.sdt() == null ? "" : duLieu.sdt());
    this.emailField.setText(duLieu.email() == null ? "" : duLieu.email());
    this.diaChiField.setText(duLieu.diaChi() == null ? "" : duLieu.diaChi());
    this.trangThaiCombo.setSelectedItem(duLieu.trangThai() == null ? TrangThaiNhaCungCap.HOAT_DONG : duLieu.trangThai());
    this.ghiChuArea.setText(duLieu.ghiChu() == null ? "" : duLieu.ghiChu());
  }

  public NhaCungCapFormData layDuLieu() {
    return new NhaCungCapFormData(
            this.maHienTai(),
            this.tenField.getText().trim(),
            this.sdtField.getText().trim(),
            this.emailField.getText().isBlank() ? null : this.emailField.getText().trim(),
            this.diaChiField.getText().isBlank() ? null : this.diaChiField.getText().trim(),
            (TrangThaiNhaCungCap) this.trangThaiCombo.getSelectedItem(),
            this.ghiChuArea.getText().isBlank() ? null : this.ghiChuArea.getText().trim());
  }

  protected abstract String maHienTai();

  public boolean validateInput() {
    if (this.tenField.getText().trim().isEmpty()) {
      iuh.fit.gui.component.ThongBao.loi(this, "app.nhaCungCap.loiTen");
      return false;
    }
    if (!this.sdtField.getText().trim().matches("^0[0-9]{8,10}$")) {
      iuh.fit.gui.component.ThongBao.loi(this, "app.nhaCungCap.loiSoDienThoai");
      return false;
    }
    String email = this.emailField.getText().trim();
    if (!email.isEmpty() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
      iuh.fit.gui.component.ThongBao.loi(this, "app.nhaCungCap.loiEmail");
      return false;
    }
    if (email.length() > 100) {
      iuh.fit.gui.component.ThongBao.loi(this, "app.nhaCungCap.loiEmailDai");
      return false;
    }
    if (this.diaChiField.getText().trim().length() > 255) {
      iuh.fit.gui.component.ThongBao.loi(this, "app.nhaCungCap.loiDiaChiDai");
      return false;
    }
    if (this.ghiChuArea.getText().trim().length() > 500) {
      iuh.fit.gui.component.ThongBao.loi(this, "app.nhaCungCap.loiGhiChuDai");
      return false;
    }
    return true;
  }
}
