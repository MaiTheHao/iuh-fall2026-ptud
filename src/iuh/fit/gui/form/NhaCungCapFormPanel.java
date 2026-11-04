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

  

  protected final JTextField txtTen = new JTextField(14);
  protected final JTextField txtSdt = new JTextField(14);
  protected final JTextField txtEmail = new JTextField(14);
  protected final JTextField txtDiaChi = new JTextField(14);
  protected final JComboBox<TrangThaiNhaCungCap> cmbTrangThai = new JComboBox<>(TrangThaiNhaCungCap.values());
  protected final JTextArea txaGhiChu = new JTextArea(4, 20);

  protected NhaCungCapFormPanel() {
    this.cmbTrangThai.setRenderer(BoHienThiI18n.tao());
    this.cmbTrangThai.setSelectedItem(TrangThaiNhaCungCap.HOAT_DONG);
  }

  protected abstract void dungBoCuc();

  public void napDuLieu(NhaCungCapFormData duLieu) {
    this.txtTen.setText(duLieu.tenNhaCungCap() == null ? "" : duLieu.tenNhaCungCap());
    this.txtSdt.setText(duLieu.sdt() == null ? "" : duLieu.sdt());
    this.txtEmail.setText(duLieu.email() == null ? "" : duLieu.email());
    this.txtDiaChi.setText(duLieu.diaChi() == null ? "" : duLieu.diaChi());
    this.cmbTrangThai.setSelectedItem(duLieu.trangThai() == null ? TrangThaiNhaCungCap.HOAT_DONG : duLieu.trangThai());
    this.txaGhiChu.setText(duLieu.ghiChu() == null ? "" : duLieu.ghiChu());
  }

  public NhaCungCapFormData layDuLieu() {
    return new NhaCungCapFormData(
            this.maHienTai(),
            this.txtTen.getText().trim(),
            this.txtSdt.getText().trim(),
            this.txtEmail.getText().isBlank() ? null : this.txtEmail.getText().trim(),
            this.txtDiaChi.getText().isBlank() ? null : this.txtDiaChi.getText().trim(),
            (TrangThaiNhaCungCap) this.cmbTrangThai.getSelectedItem(),
            this.txaGhiChu.getText().isBlank() ? null : this.txaGhiChu.getText().trim());
  }

  protected abstract String maHienTai();

  public boolean validateInput() {
    if (this.txtTen.getText().trim().isEmpty()) {
      iuh.fit.gui.component.ThongBao.loi(this, "app.nhaCungCap.loiTen");
      return false;
    }
    if (!this.txtSdt.getText().trim().matches("^0[0-9]{8,10}$")) {
      iuh.fit.gui.component.ThongBao.loi(this, "app.nhaCungCap.loiSoDienThoai");
      return false;
    }
    String email = this.txtEmail.getText().trim();
    if (!email.isEmpty() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
      iuh.fit.gui.component.ThongBao.loi(this, "app.nhaCungCap.loiEmail");
      return false;
    }
    if (email.length() > 100) {
      iuh.fit.gui.component.ThongBao.loi(this, "app.nhaCungCap.loiEmailDai");
      return false;
    }
    if (this.txtDiaChi.getText().trim().length() > 255) {
      iuh.fit.gui.component.ThongBao.loi(this, "app.nhaCungCap.loiDiaChiDai");
      return false;
    }
    if (this.txaGhiChu.getText().trim().length() > 500) {
      iuh.fit.gui.component.ThongBao.loi(this, "app.nhaCungCap.loiGhiChuDai");
      return false;
    }
    return true;
  }
}
