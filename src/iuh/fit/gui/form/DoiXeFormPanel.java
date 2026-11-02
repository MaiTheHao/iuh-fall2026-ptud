package iuh.fit.gui.form;

import iuh.fit.entity.DoiXe;
import iuh.fit.entity.DongXe;
import iuh.fit.entity.HangXe;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import java.util.List;

public final class DoiXeFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  private final JComboBox<HangXe> hangXeCombo = new JComboBox<>();
  private final JComboBox<DongXe> dongXeCombo = new JComboBox<>();
  private final JTextField namField = new JTextField(10);

  public DoiXeFormPanel() {
    this.addField("app.danhMucXe.hang", this.hangXeCombo);
    this.addField("app.danhMucXe.dong", this.dongXeCombo);
    this.addField("app.danhMucXe.nam", this.namField);
  }

  public JComboBox<HangXe> getHangXeCombo() {
    return this.hangXeCombo;
  }

  public JComboBox<DongXe> getDongXeCombo() {
    return this.dongXeCombo;
  }

  public void napDanhSachHang(List<HangXe> danhSach) {
    this.hangXeCombo.removeAllItems();
    for (HangXe hangXe : danhSach) {
      this.hangXeCombo.addItem(hangXe);
    }
  }

  public void napDanhSachDong(List<DongXe> danhSach) {
    this.dongXeCombo.removeAllItems();
    for (DongXe dongXe : danhSach) {
      this.dongXeCombo.addItem(dongXe);
    }
  }

  public void napDuLieu(DoiXe doiXe) {
    this.chonHangXe(doiXe == null ? null : doiXe.getMaHangXe());
    this.chonDongXe(doiXe == null ? null : doiXe.getMaDongXe());
    this.namField.setText(doiXe == null ? "" : String.valueOf(doiXe.getNamSX()));
  }

  private void chonHangXe(String maHangXe) {
    if (maHangXe == null) {
      return;
    }
    for (int i = 0; i < this.hangXeCombo.getItemCount(); i++) {
      HangXe item = this.hangXeCombo.getItemAt(i);
      if (item != null && maHangXe.equals(item.getMaHangXe())) {
        this.hangXeCombo.setSelectedIndex(i);
        return;
      }
    }
  }

  private void chonDongXe(String maDongXe) {
    if (maDongXe == null) {
      return;
    }
    for (int i = 0; i < this.dongXeCombo.getItemCount(); i++) {
      DongXe item = this.dongXeCombo.getItemAt(i);
      if (item != null && maDongXe.equals(item.getMaDongXe())) {
        this.dongXeCombo.setSelectedIndex(i);
        return;
      }
    }
  }

  public String layMaHangXe() {
    Object sel = this.hangXeCombo.getSelectedItem();
    HangXe item = sel instanceof HangXe ? (HangXe) sel : null;
    return item == null ? null : item.getMaHangXe();
  }

  public String layMaDongXe() {
    Object sel = this.dongXeCombo.getSelectedItem();
    DongXe item = sel instanceof DongXe ? (DongXe) sel : null;
    return item == null ? null : item.getMaDongXe();
  }

  public Integer layNamSX() {
    return NumberParseUtil.toIntegerOrNull(this.namField.getText());
  }

  public boolean validateInput() {
    if (this.layMaHangXe() == null) {
      FormValidationHelper.markError(this.hangXeCombo, I18nReaderUtil.get("app.danhMucXe.chuaChonHang"));
      this.hangXeCombo.requestFocusInWindow();
      return false;
    }
    if (this.layMaDongXe() == null) {
      FormValidationHelper.markError(this.dongXeCombo, I18nReaderUtil.get("app.danhMucXe.chuaChonDong"));
      this.dongXeCombo.requestFocusInWindow();
      return false;
    }
    if (this.layNamSX() == null) {
      FormValidationHelper.markError(this.namField, I18nReaderUtil.get("app.danhMucXe.loiNamSX"));
      this.namField.requestFocusInWindow();
      return false;
    }
    FormValidationHelper.clearError(this.namField);
    return true;
  }
}
