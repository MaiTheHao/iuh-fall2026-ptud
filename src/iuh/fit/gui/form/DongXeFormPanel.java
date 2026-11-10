package iuh.fit.gui.form;

import iuh.fit.entity.DongXe;
import iuh.fit.entity.HangXe;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import java.util.List;

/**
 * Form nhập dòng xe và chọn hãng xe tương ứng.
 */
public class DongXeFormPanel extends FormFieldPanel {

  

  private static final int DO_DAI_TEN_TOI_DA = 100;

  protected final boolean tao;

  protected final JTextField txtMa = new JTextField(14);
  protected final JTextField txtTen = new JTextField(14);
  protected final JComboBox<HangXe> cmbHangXe = new JComboBox<>();

  public DongXeFormPanel(boolean tao) {
    this.tao = tao;
    Theme.applyReadOnly(this.txtMa);
    FormValidationHelper.autoClearOnInput(this.txtTen);

    this.dungBoCuc();
  }

  protected void dungBoCuc() {
    // Mã dòng xe do hệ thống sinh nên chỉ hiển thị khi sửa.
    if (!this.tao) {
      this.addField("app.danhMucXe.ma", this.txtMa);
    }
    this.addField("app.danhMucXe.hang", this.cmbHangXe);
    this.addField("app.danhMucXe.ten", this.txtTen);
  }

  public JComboBox<HangXe> getHangXeCombo() {
    return this.cmbHangXe;
  }

  public void napDanhSachHang(List<HangXe> danhSach) {
    this.cmbHangXe.removeAllItems();
    for (HangXe hangXe : danhSach) {
      this.cmbHangXe.addItem(hangXe);
    }
  }

  public void napDuLieu(DongXe dongXe) {
    this.txtMa.setText(dongXe == null || dongXe.getMaDongXe() == null ? "" : dongXe.getMaDongXe());
    this.txtTen.setText(dongXe == null || dongXe.getTen() == null ? "" : dongXe.getTen());
    this.chonHangXe(dongXe == null ? null : dongXe.getMaHangXe());
  }

  public DongXe layDuLieu() {
    String ma = this.txtMa.getText().trim();
    return new DongXe(ma.isBlank() ? null : ma, this.layMaHangXe(), this.layTen(), null);
  }

  public final boolean validateInput() {
    JComponent loiChung = this.kiemTraChung();
    JComponent loiRieng = this.kiemTraRieng();
    JComponent loiDauTien = loiChung != null ? loiChung : loiRieng;

    if (loiDauTien != null) {
      loiDauTien.requestFocusInWindow();
      return false;
    }

    return true;
  }

  protected JComponent kiemTraRieng() {
    return null;
  }

  protected JComponent kiemTraChung() {
    if (this.layMaHangXe() == null) {
      FormValidationHelper.markError(this.cmbHangXe,
              I18nReaderUtil.get("app.danhMucXe.chuaChonHang"));
      return this.cmbHangXe;
    }
    FormValidationHelper.clearError(this.cmbHangXe);

    String ten = this.layTen();
    if (ten.isEmpty()) {
      FormValidationHelper.markError(this.txtTen, I18nReaderUtil.get("app.danhMucXe.loiTen"));
      return this.txtTen;
    }

    if (ten.length() > DO_DAI_TEN_TOI_DA) {
      FormValidationHelper.markError(this.txtTen,
              I18nReaderUtil.get("app.danhMucXe.loiTenQuaDai"));
      return this.txtTen;
    }

    FormValidationHelper.clearError(this.txtTen);
    return null;
  }

  public void hienThiLoiTen(String thongDiep) {
    FormValidationHelper.markError(this.txtTen, thongDiep);
    this.txtTen.requestFocusInWindow();
  }

  private void chonHangXe(String maHangXe) {
    this.cmbHangXe.setSelectedIndex(-1);

    // Tìm theo mã vì danh sách có thể được nạp lại với thứ tự khác.
    if (maHangXe == null || maHangXe.isBlank()) {
      return;
    }

    for (int i = 0; i < this.cmbHangXe.getItemCount(); i++) {
      HangXe item = this.cmbHangXe.getItemAt(i);
      if (item != null && maHangXe.equals(item.getMaHangXe())) {
        this.cmbHangXe.setSelectedIndex(i);
        return;
      }
    }
  }

  private String layMaHangXe() {
    Object sel = this.cmbHangXe.getSelectedItem();
    return sel instanceof HangXe hangXe ? hangXe.getMaHangXe() : null;
  }

  private String layTen() {
    return this.txtTen.getText().trim();
  }
}
