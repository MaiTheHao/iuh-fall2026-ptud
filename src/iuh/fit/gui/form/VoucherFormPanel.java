package iuh.fit.gui.form;

import com.github.lgooddatepicker.components.DateTimePicker;
import iuh.fit.dto.VoucherFormData;
import iuh.fit.gui.component.BoChonNgayGio;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.time.LocalDateTime;

/**
 * Phần dùng chung của form voucher: trường nhập liệu, đọc/ghi dữ liệu và kiểm tra hợp lệ.
 * Lớp con quyết định bố cục cho dialog tạo mới hoặc cập nhật.
 */
public abstract class VoucherFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  private static final int DO_DAI_TEN_TOI_DA = 100;
  private static final int DO_DAI_MO_TA_TOI_DA = 500;

  protected final JTextField maField = new JTextField(14);
  protected final JTextField tenChuongTrinhField = new JTextField(14);
  protected final JTextArea moTaArea = new JTextArea(4, 20);
  protected final JScrollPane moTaScroll = FormFieldPanel.taoKhungMoTa(this.moTaArea);
  protected final JTextField mucGiamField = new JTextField(14);
  protected final DateTimePicker thoiHanPicker = BoChonNgayGio.taoBoChonNgayGio();
  protected final JCheckBox trangThaiField = new JCheckBox();

  protected VoucherFormPanel() {
    this.mucGiamField.setHorizontalAlignment(SwingConstants.RIGHT);
    Theme.applyReadOnly(this.maField);

    BoChonNgayGio.apDungMau(this.thoiHanPicker);

    this.trangThaiField.setOpaque(false);
    this.trangThaiField.setSelected(true);

    FormValidationHelper.autoClearOnInput(this.tenChuongTrinhField);
    FormValidationHelper.autoClearOnInput(this.mucGiamField);
  }

  /** Lớp con dựng bố cục trong constructor của chính nó. */
  protected abstract void dungBoCuc();

  public void napDuLieu(VoucherFormData data) {
    this.maField.setText(data.maVoucher() == null ? "" : data.maVoucher());
    this.tenChuongTrinhField.setText(data.tenChuongTrinh() == null ? "" : data.tenChuongTrinh());
    this.moTaArea.setText(data.moTa() == null ? "" : data.moTa());
    this.mucGiamField.setText(data.mucGiam() == null ? "" : String.valueOf(data.mucGiam()));

    if (data.thoiHanApDung() == null) {
      this.thoiHanPicker.clear();
    } else {
      this.thoiHanPicker.setDateTimeStrict(data.thoiHanApDung());
    }

    this.trangThaiField.setSelected(Boolean.TRUE.equals(data.trangThaiKichHoat()));
  }

  public VoucherFormData layDuLieu() {
    String ma = this.maField.getText().trim();
    String moTa = this.moTaArea.getText().trim();

    return new VoucherFormData(
            ma.isBlank() ? null : ma,
            this.tenChuongTrinhField.getText().trim(),
            moTa.isEmpty() ? null : moTa,
            NumberParseUtil.toDoubleOrNull(this.mucGiamField.getText()),
            this.thoiHanPicker.getDateTimeStrict(),
            this.trangThaiField.isSelected());
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

    String ten = this.tenChuongTrinhField.getText().trim();

    if (ten.isEmpty()) {
      FormValidationHelper.markError(this.tenChuongTrinhField,
              I18nReaderUtil.get("app.voucher.loiTen"));
      loiDauTien = this.tenChuongTrinhField;
    } else if (ten.length() > DO_DAI_TEN_TOI_DA) {
      FormValidationHelper.markError(this.tenChuongTrinhField,
              I18nReaderUtil.get("app.voucher.tenQuaDai"));
      loiDauTien = this.tenChuongTrinhField;
    } else {
      FormValidationHelper.clearError(this.tenChuongTrinhField);
    }

    if (this.moTaArea.getText().trim().length() > DO_DAI_MO_TA_TOI_DA) {
      FormValidationHelper.markError(this.moTaArea,
              I18nReaderUtil.get("app.voucher.moTaQuaDai"));
      if (loiDauTien == null) {
        loiDauTien = this.moTaArea;
      }
    } else {
      FormValidationHelper.clearError(this.moTaArea);
    }

    Double mucGiam = NumberParseUtil.toDoubleOrNull(this.mucGiamField.getText());

    if (mucGiam == null || !Double.isFinite(mucGiam) || mucGiam < 0 || mucGiam > 1) {
      FormValidationHelper.markError(this.mucGiamField,
              I18nReaderUtil.get("app.voucher.loiMucGiam"));
      if (loiDauTien == null) {
        loiDauTien = this.mucGiamField;
      }
    } else {
      FormValidationHelper.clearError(this.mucGiamField);
    }

    LocalDateTime thoiHan = this.thoiHanPicker.getDateTimeStrict();

    if (thoiHan == null) {
      FormValidationHelper.markError(this.thoiHanPicker,
              I18nReaderUtil.get("app.voucher.loiThoiHan"));
      if (loiDauTien == null) {
        loiDauTien = this.thoiHanPicker;
      }
    } else {
      FormValidationHelper.clearError(this.thoiHanPicker);
    }

    return loiDauTien;
  }
}
