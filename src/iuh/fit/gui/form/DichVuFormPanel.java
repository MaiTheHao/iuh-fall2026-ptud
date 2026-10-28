package iuh.fit.gui.form;

import iuh.fit.dto.DichVuFormData;
import iuh.fit.gui.component.AnhField;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Phần dùng chung của form dịch vụ: trường nhập liệu, đọc/ghi dữ liệu và kiểm tra hợp lệ.
 * Lớp con quyết định bố cục cho dialog tạo mới hoặc cập nhật.
 */
public abstract class DichVuFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  private static final int DO_DAI_TEN_TOI_DA = 100;
  private static final int DO_DAI_MO_TA_TOI_DA = 500;

  protected final JTextField maField = new JTextField(14);
  protected final JTextField tenField = new JTextField(14);
  protected final JTextArea moTaArea = new JTextArea(4, 20);
  protected final JScrollPane moTaScroll = FormFieldPanel.taoKhungMoTa(this.moTaArea);
  protected final JTextField donGiaField = new JTextField(14);

  protected final AnhField anhField = new AnhField(new AnhField.KhoaAnh(
          "app.dichVu.anhUrl",
          "app.dichVu.chonAnh",
          "app.dichVu.goAnh",
          "app.dichVu.chuaCoAnh",
          "app.dichVu.khongTaiDuocAnh",
          "app.dichVu.loaiTepAnh",
          "app.dichVu.duLieuKhongPhaiAnh"));

  protected DichVuFormPanel() {
    this.donGiaField.setHorizontalAlignment(SwingConstants.RIGHT);
    Theme.applyReadOnly(this.maField);

    FormValidationHelper.autoClearOnInput(this.tenField);
    FormValidationHelper.autoClearOnInput(this.donGiaField);
  }

  /** Lớp con dựng bố cục trong constructor của chính nó. */
  protected abstract void dungBoCuc();

  public void napDuLieu(DichVuFormData data) {
    this.maField.setText(data.maDichVu() == null ? "" : data.maDichVu());
    this.tenField.setText(data.tenDichVu() == null ? "" : data.tenDichVu());
    this.moTaArea.setText(data.moTa() == null ? "" : data.moTa());
    this.anhField.setDuongDan(data.anhUrl());
    this.donGiaField.setText(data.donGia() == null ? "" : String.valueOf(data.donGia()));
  }

  public DichVuFormData layDuLieu() {
    String ma = this.maField.getText().trim();
    String moTa = this.moTaArea.getText().trim();
    String anhUrl = this.anhField.getDuongDan();

    return new DichVuFormData(
            ma.isBlank() ? null : ma,
            this.tenField.getText().trim(),
            moTa.isBlank() ? null : moTa,
            anhUrl.isBlank() ? null : anhUrl,
            NumberParseUtil.toDoubleOrNull(this.donGiaField.getText()));
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
    JComponent loiDauTien = null;

    String ten = this.tenField.getText().trim();

    if (ten.isEmpty()) {
      FormValidationHelper.markError(this.tenField,
              I18nReaderUtil.get("app.dichVu.loiTen"));
      loiDauTien = this.tenField;
    } else if (ten.length() > DO_DAI_TEN_TOI_DA) {
      FormValidationHelper.markError(this.tenField,
              I18nReaderUtil.get("app.dichVu.loiTenQuaDai"));
      loiDauTien = this.tenField;
    } else {
      FormValidationHelper.clearError(this.tenField);
    }

    if (this.moTaArea.getText().trim().length() > DO_DAI_MO_TA_TOI_DA) {
      FormValidationHelper.markError(this.moTaArea,
              I18nReaderUtil.get("app.dichVu.loiMoTa"));
      if (loiDauTien == null) {
        loiDauTien = this.moTaArea;
      }
    } else {
      FormValidationHelper.clearError(this.moTaArea);
    }

    Double donGia = NumberParseUtil.toDoubleOrNull(this.donGiaField.getText());

    if (donGia == null || !Double.isFinite(donGia) || donGia < 0) {
      FormValidationHelper.markError(this.donGiaField,
              I18nReaderUtil.get("app.dichVu.loiDonGia"));
      if (loiDauTien == null) {
        loiDauTien = this.donGiaField;
      }
    } else {
      FormValidationHelper.clearError(this.donGiaField);
    }

    return loiDauTien;
  }
}
