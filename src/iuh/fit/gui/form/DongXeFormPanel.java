package iuh.fit.gui.form;

import iuh.fit.entity.DongXe;
import iuh.fit.entity.HangXe;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import java.util.List;

/**
 * Phần dùng chung của form dòng xe: chọn hãng, nhập tên và kiểm tra hợp lệ.
 * Lớp con quyết định bố cục cho dialog tạo mới hoặc cập nhật.
 */
public abstract class DongXeFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  private static final int DO_DAI_TEN_TOI_DA = 100;

  protected final JTextField maField = new JTextField(14);
  protected final JTextField tenField = new JTextField(14);
  protected final JComboBox<HangXe> hangXeCombo = new JComboBox<>();

  protected DongXeFormPanel() {
    Theme.applyReadOnly(this.maField);
    FormValidationHelper.autoClearOnInput(this.tenField);
  }

  /** Lớp con dựng bố cục trong constructor của chính nó. */
  protected abstract void dungBoCuc();

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
    this.maField.setText(dongXe == null || dongXe.getMaDongXe() == null ? "" : dongXe.getMaDongXe());
    this.tenField.setText(dongXe == null || dongXe.getTen() == null ? "" : dongXe.getTen());
    this.chonHangXe(dongXe == null ? null : dongXe.getMaHangXe());
  }

  public DongXe layDuLieu() {
    String ma = this.maField.getText().trim();
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

  /** Chỗ mở rộng cho rule chỉ áp dụng ở một mode. */
  protected JComponent kiemTraRieng() {
    return null;
  }

  protected JComponent kiemTraChung() {
    if (this.layMaHangXe() == null) {
      FormValidationHelper.markError(this.hangXeCombo,
              I18nReaderUtil.get("app.danhMucXe.chuaChonHang"));
      return this.hangXeCombo;
    }
    FormValidationHelper.clearError(this.hangXeCombo);

    String ten = this.layTen();
    if (ten.isEmpty()) {
      FormValidationHelper.markError(this.tenField, I18nReaderUtil.get("app.danhMucXe.loiTen"));
      return this.tenField;
    }

    if (ten.length() > DO_DAI_TEN_TOI_DA) {
      FormValidationHelper.markError(this.tenField,
              I18nReaderUtil.get("app.danhMucXe.loiTenQuaDai"));
      return this.tenField;
    }

    FormValidationHelper.clearError(this.tenField);
    return null;
  }

  /** Đánh dấu lỗi nghiệp vụ do service trả về lên đúng ô tên. */
  public void hienThiLoiTen(String thongDiep) {
    FormValidationHelper.markError(this.tenField, thongDiep);
    this.tenField.requestFocusInWindow();
  }

  private void chonHangXe(String maHangXe) {
    this.hangXeCombo.setSelectedIndex(-1);

    if (maHangXe == null || maHangXe.isBlank()) {
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

  private String layMaHangXe() {
    Object sel = this.hangXeCombo.getSelectedItem();
    HangXe item = sel instanceof HangXe ? (HangXe) sel : null;
    return item == null ? null : item.getMaHangXe();
  }

  private String layTen() {
    return this.tenField.getText().trim();
  }
}
