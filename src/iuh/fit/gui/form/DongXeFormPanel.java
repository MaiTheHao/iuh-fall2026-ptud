package iuh.fit.gui.form;

import iuh.fit.entity.DongXe;
import iuh.fit.entity.HangXe;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import java.util.List;

public final class DongXeFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;
  private static final int DO_DAI_TEN_TOI_DA = 100;

  private final JComboBox<HangXe> hangXeCombo = new JComboBox<>();
  private final JTextField tenField = new JTextField(20);

  public DongXeFormPanel() {
    this.addField("app.danhMucXe.hang", this.hangXeCombo);
    this.addField("app.danhMucXe.ten", this.tenField);
  }

  public JComboBox<HangXe> getHangXeCombo() {
    return this.hangXeCombo;
  }

  public void napDanhSachHang(List<HangXe> danhSach) {
    this.hangXeCombo.removeAllItems();
    for (HangXe hangXe : danhSach) {
      this.hangXeCombo.addItem(hangXe);
    }
  }

  public void napDuLieu(DongXe dongXe) {
    this.tenField.setText(dongXe == null || dongXe.getTen() == null ? "" : dongXe.getTen());
    this.chonHangXe(dongXe == null ? null : dongXe.getMaHangXe());
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

  public String layMaHangXe() {
    Object sel = this.hangXeCombo.getSelectedItem();
    HangXe item = sel instanceof HangXe ? (HangXe) sel : null;
    return item == null ? null : item.getMaHangXe();
  }

  public String layTen() {
    return this.tenField.getText().trim();
  }

  public boolean validateInput() {
    if (this.layMaHangXe() == null) {
      FormValidationHelper.markError(this.hangXeCombo, I18nReaderUtil.get("app.danhMucXe.chuaChonHang"));
      this.hangXeCombo.requestFocusInWindow();
      return false;
    }
    FormValidationHelper.clearError(this.hangXeCombo);
    String ten = this.layTen();
    if (ten.isEmpty()) {
      FormValidationHelper.markError(this.tenField, I18nReaderUtil.get("app.danhMucXe.loiTen"));
      this.tenField.requestFocusInWindow();
      return false;
    }
    if (ten.length() > DO_DAI_TEN_TOI_DA) {
      FormValidationHelper.markError(this.tenField, I18nReaderUtil.get("app.danhMucXe.loiTenQuaDai"));
      this.tenField.requestFocusInWindow();
      return false;
    }
    FormValidationHelper.clearError(this.tenField);
    return true;
  }
}
