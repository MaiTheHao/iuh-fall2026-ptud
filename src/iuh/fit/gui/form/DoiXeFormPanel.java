package iuh.fit.gui.form;

import iuh.fit.entity.DoiXe;
import iuh.fit.entity.DongXe;
import iuh.fit.entity.HangXe;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.gui.component.LocSoDocumentFilter;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.DanhMucXeService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import java.util.List;

/**
 * Phần dùng chung của form đời xe: chọn hãng, chọn dòng, nhập năm sản xuất và kiểm tra hợp lệ.
 * Lớp con quyết định bố cục cho dialog tạo mới hoặc cập nhật.
 */
public abstract class DoiXeFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  protected final JTextField maField = new JTextField(14);
  protected final JComboBox<HangXe> hangXeCombo = new JComboBox<>();
  protected final JComboBox<DongXe> dongXeCombo = new JComboBox<>();
  protected final JTextField namField = new JTextField(14);

  protected DoiXeFormPanel() {
    Theme.applyReadOnly(this.maField);
    this.namField.setHorizontalAlignment(JTextField.RIGHT);
    ((AbstractDocument) this.namField.getDocument()).setDocumentFilter(new LocSoDocumentFilter());
    FormValidationHelper.autoClearOnInput(this.namField);
  }

  /** Lớp con dựng bố cục trong constructor của chính nó. */
  protected abstract void dungBoCuc();

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
    this.maField.setText(doiXe == null || doiXe.getMaDoiXe() == null ? "" : doiXe.getMaDoiXe());
    this.chonHangXe(doiXe == null ? null : doiXe.getMaHangXe());
    this.chonDongXe(doiXe == null ? null : doiXe.getMaDongXe());
    this.namField.setText(doiXe == null || doiXe.getNamSX() <= 0 ? "" : String.valueOf(doiXe.getNamSX()));
  }

  public DoiXe layDuLieu() {
    String ma = this.maField.getText().trim();
    Integer namSX = this.layNamSX();
    return new DoiXe(ma.isBlank() ? null : ma,
            this.layMaDongXe(),
            namSX == null ? 0 : namSX,
            this.layMaHangXe(),
            null,
            null);
  }

  public Integer layNamSX() {
    return NumberParseUtil.toIntegerOrNull(this.namField.getText());
  }

  public String layMaHangXe() {
    Object sel = this.hangXeCombo.getSelectedItem();
    HangXe item = sel instanceof HangXe ? (HangXe) sel : null;
    return item == null ? null : item.getMaHangXe();
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

    if (this.layMaDongXe() == null) {
      FormValidationHelper.markError(this.dongXeCombo,
              I18nReaderUtil.get("app.danhMucXe.chuaChonDong"));
      return this.dongXeCombo;
    }
    FormValidationHelper.clearError(this.dongXeCombo);

    Integer namSX = this.layNamSX();
    if (namSX == null
            || namSX < DanhMucXeService.NAM_SX_NHO_NHAT
            || namSX > DanhMucXeService.namSxLonNhat()) {
      FormValidationHelper.markError(this.namField, I18nReaderUtil.get("app.danhMucXe.loiNamSX"));
      return this.namField;
    }
    FormValidationHelper.clearError(this.namField);
    return null;
  }

  /** Đánh dấu lỗi nghiệp vụ do service trả về lên đúng ô năm sản xuất. */
  public void hienThiLoiNam(String thongDiep) {
    FormValidationHelper.markError(this.namField, thongDiep);
    this.namField.requestFocusInWindow();
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

  private void chonDongXe(String maDongXe) {
    this.dongXeCombo.setSelectedIndex(-1);

    if (maDongXe == null || maDongXe.isBlank()) {
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

  private String layMaDongXe() {
    Object sel = this.dongXeCombo.getSelectedItem();
    DongXe item = sel instanceof DongXe ? (DongXe) sel : null;
    return item == null ? null : item.getMaDongXe();
  }
}
