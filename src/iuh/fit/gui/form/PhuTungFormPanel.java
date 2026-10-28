package iuh.fit.gui.form;

import iuh.fit.dto.PhuTungFormData;
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
 * Phần dùng chung của form phụ tùng: trường nhập liệu, đọc/ghi dữ liệu và kiểm tra hợp lệ.
 * Lớp con quyết định bố cục cho dialog tạo mới hoặc cập nhật.
 */
public abstract class PhuTungFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  protected final JTextField maField = new JTextField(14);
  protected final JTextField tenField = new JTextField(14);
  protected final JTextArea moTaArea = new JTextArea(4, 20);
  protected final JScrollPane moTaScroll = FormFieldPanel.taoKhungMoTa(this.moTaArea);
  protected final JTextField donGiaField = new JTextField(14);
  protected final JTextField soLuongTonField = new JTextField(14);

  protected final AnhField anhField = new AnhField(new AnhField.KhoaAnh(
          "app.phuTung.anhUrl",
          "app.phuTung.chonAnh",
          "app.phuTung.goAnh",
          "app.phuTung.chuaCoAnh",
          "app.phuTung.khongTaiDuocAnh",
          "app.phuTung.loaiTepAnh",
          "app.phuTung.duLieuKhongPhaiAnh"));

  protected PhuTungFormPanel() {
    this.donGiaField.setHorizontalAlignment(SwingConstants.RIGHT);
    this.soLuongTonField.setHorizontalAlignment(SwingConstants.RIGHT);

    Theme.applyReadOnly(this.maField);
    // Tồn kho chỉ thay đổi qua phiếu nhập / nghiệp vụ kho nên luôn chỉ đọc.
    Theme.applyReadOnly(this.soLuongTonField);
    this.soLuongTonField.setText("0");

    FormValidationHelper.autoClearOnInput(this.tenField);
    FormValidationHelper.autoClearOnInput(this.donGiaField);
  }

  /** Lớp con dựng bố cục trong constructor của chính nó. */
  protected abstract void dungBoCuc();

  public void napDuLieu(PhuTungFormData data) {
    this.maField.setText(data.maPhuTung() == null ? "" : data.maPhuTung());
    this.tenField.setText(data.tenPhuTung() == null ? "" : data.tenPhuTung());
    this.moTaArea.setText(data.moTa() == null ? "" : data.moTa());
    this.anhField.setDuongDan(data.anhUrl());
    this.donGiaField.setText(data.donGiaNhap() == null ? "" : String.valueOf(data.donGiaNhap()));
    this.soLuongTonField.setText(data.soLuongTon() == null ? "0" : String.valueOf(data.soLuongTon()));
  }

  public PhuTungFormData layDuLieu() {
    String ma = this.maField.getText().trim();
    String moTa = this.moTaArea.getText().trim();
    String anhUrl = this.anhField.getDuongDan();
    Integer soLuongTon = NumberParseUtil.toIntegerOrNull(this.soLuongTonField.getText());

    return new PhuTungFormData(
            ma.isBlank() ? null : ma,
            this.tenField.getText().trim(),
            moTa.isBlank() ? null : moTa,
            anhUrl.isBlank() ? null : anhUrl,
            NumberParseUtil.toDoubleOrNull(this.donGiaField.getText()),
            soLuongTon == null ? 0 : soLuongTon);
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

    if (this.tenField.getText().trim().isEmpty()) {
      FormValidationHelper.markError(this.tenField,
              I18nReaderUtil.get("app.phuTung.loiTen"));
      loiDauTien = this.tenField;
    } else {
      FormValidationHelper.clearError(this.tenField);
    }

    Double donGia = NumberParseUtil.toDoubleOrNull(this.donGiaField.getText());

    if (donGia == null || !Double.isFinite(donGia) || donGia < 0) {
      FormValidationHelper.markError(this.donGiaField,
              I18nReaderUtil.get("app.phuTung.loiDonGia"));
      if (loiDauTien == null) {
        loiDauTien = this.donGiaField;
      }
    } else {
      FormValidationHelper.clearError(this.donGiaField);
    }

    return loiDauTien;
  }
}
