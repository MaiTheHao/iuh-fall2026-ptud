package iuh.fit.gui.form;

import iuh.fit.entity.DoiXe;
import iuh.fit.entity.DongXe;
import iuh.fit.entity.HangXe;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.gui.component.LocSoDocumentFilter;
import iuh.fit.service.DanhMucXeService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import java.util.List;

public class DoiXeFormPanel extends FormFieldPanel {

  

  protected final boolean tao;

  protected final JTextField txtMa = new JTextField(14);
  protected final JComboBox<HangXe> cmbHangXe = new JComboBox<>();
  protected final JComboBox<DongXe> cmbDongXe = new JComboBox<>();
  protected final JTextField txtNam = new JTextField(14);

  public DoiXeFormPanel(boolean tao) {
    this.tao = tao;
    Theme.applyReadOnly(this.txtMa);
    this.txtNam.setHorizontalAlignment(JTextField.RIGHT);
    ((AbstractDocument) this.txtNam.getDocument()).setDocumentFilter(new LocSoDocumentFilter());
    FormValidationHelper.autoClearOnInput(this.txtNam);

    this.dungBoCuc();
  }

  protected void dungBoCuc() {
    if (!this.tao) {
      this.addField("app.danhMucXe.ma", this.txtMa);
    }
    this.addField("app.danhMucXe.hang", this.cmbHangXe);
    this.addField("app.danhMucXe.dong", this.cmbDongXe);
    this.addField("app.danhMucXe.nam", this.txtNam);
  }

  public JComboBox<HangXe> getHangXeCombo() {
    return this.cmbHangXe;
  }

  public JComboBox<DongXe> getDongXeCombo() {
    return this.cmbDongXe;
  }

  public void napDanhSachHang(List<HangXe> danhSach) {
    this.cmbHangXe.removeAllItems();
    for (HangXe hangXe : danhSach) {
      this.cmbHangXe.addItem(hangXe);
    }
  }

  public void napDanhSachDong(List<DongXe> danhSach) {
    this.cmbDongXe.removeAllItems();
    for (DongXe dongXe : danhSach) {
      this.cmbDongXe.addItem(dongXe);
    }
  }

  public void napDuLieu(DoiXe doiXe) {
    this.txtMa.setText(doiXe == null || doiXe.getMaDoiXe() == null ? "" : doiXe.getMaDoiXe());
    this.chonHangXe(doiXe == null ? null : doiXe.getMaHangXe());
    this.chonDongXe(doiXe == null ? null : doiXe.getMaDongXe());
    this.txtNam.setText(doiXe == null || doiXe.getNamSX() <= 0 ? "" : String.valueOf(doiXe.getNamSX()));
  }

  public DoiXe layDuLieu() {
    String ma = this.txtMa.getText().trim();
    Integer namSX = this.layNamSX();
    return new DoiXe(ma.isBlank() ? null : ma,
            this.layMaDongXe(),
            namSX == null ? 0 : namSX,
            this.layMaHangXe(),
            null,
            null);
  }

  public Integer layNamSX() {
    return NumberParseUtil.toIntegerOrNull(this.txtNam.getText());
  }

  public String layMaHangXe() {
    Object sel = this.cmbHangXe.getSelectedItem();
    return sel instanceof HangXe hangXe ? hangXe.getMaHangXe() : null;
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

    if (this.layMaDongXe() == null) {
      FormValidationHelper.markError(this.cmbDongXe,
              I18nReaderUtil.get("app.danhMucXe.chuaChonDong"));
      return this.cmbDongXe;
    }
    FormValidationHelper.clearError(this.cmbDongXe);

    Integer namSX = this.layNamSX();
    if (namSX == null
            || namSX < DanhMucXeService.NAM_SX_NHO_NHAT
            || namSX > DanhMucXeService.namSxLonNhat()) {
      FormValidationHelper.markError(this.txtNam, I18nReaderUtil.get("app.danhMucXe.loiNamSX"));
      return this.txtNam;
    }
    FormValidationHelper.clearError(this.txtNam);
    return null;
  }

  public void hienThiLoiNam(String thongDiep) {
    FormValidationHelper.markError(this.txtNam, thongDiep);
    this.txtNam.requestFocusInWindow();
  }

  private void chonHangXe(String maHangXe) {
    this.cmbHangXe.setSelectedIndex(-1);

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

  private void chonDongXe(String maDongXe) {
    this.cmbDongXe.setSelectedIndex(-1);

    if (maDongXe == null || maDongXe.isBlank()) {
      return;
    }

    for (int i = 0; i < this.cmbDongXe.getItemCount(); i++) {
      DongXe item = this.cmbDongXe.getItemAt(i);
      if (item != null && maDongXe.equals(item.getMaDongXe())) {
        this.cmbDongXe.setSelectedIndex(i);
        return;
      }
    }
  }

  private String layMaDongXe() {
    Object sel = this.cmbDongXe.getSelectedItem();
    return sel instanceof DongXe dongXe ? dongXe.getMaDongXe() : null;
  }
}
