package iuh.fit.gui.form;

import com.github.lgooddatepicker.components.DateTimePicker;
import iuh.fit.dto.VoucherFormData;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.BoChonNgayGio;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
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
 * Form nhập voucher giảm giá, dùng chung cho tạo mới và sửa.
 */
public class VoucherFormPanel extends FormFieldPanel {

  protected final boolean tao;


  private static final int DO_DAI_TEN_TOI_DA = 100;
  private static final int DO_DAI_MO_TA_TOI_DA = 500;

  protected final JTextField txtMa = new JTextField(14);
  protected final JTextField txtTenChuongTrinh = new JTextField(14);
  protected final JTextArea txaMoTa = new JTextArea(4, 20);
  protected final JScrollPane moTaScroll = FormFieldPanel.taoKhungMoTa(this.txaMoTa);
  protected final JTextField txtMucGiam = new JTextField(14);
  protected final DateTimePicker thoiHanPicker = BoChonNgayGio.taoBoChonNgayGio();
  protected final JCheckBox chkTrangThai = new JCheckBox();

  public VoucherFormPanel(boolean tao) {
    this.tao = tao;
    this.txtMucGiam.setHorizontalAlignment(SwingConstants.RIGHT);
    Theme.applyReadOnly(this.txtMa);

    // DateTimePicker của LGoodDatePicker không tự theo theme nên phải áp màu thủ công.
    BoChonNgayGio.apDungMau(this.thoiHanPicker);

    this.chkTrangThai.setOpaque(false);
    this.chkTrangThai.setSelected(true);

    FormValidationHelper.autoClearOnInput(this.txtTenChuongTrinh);
    FormValidationHelper.autoClearOnInput(this.txtMucGiam);

    this.dungBoCuc();
  }

  protected void dungBoCuc() {
    if (!this.tao) {
      this.addField("app.voucher.ma", this.txtMa);
    }
    this.addField("app.voucher.tenChuongTrinh", this.txtTenChuongTrinh);
    this.addField("app.voucher.moTa", this.moTaScroll);
    this.addField("app.voucher.mucGiam", this.txtMucGiam);
    this.addBlock(taoGoiY("app.voucher.huongDanMucGiam"));
    this.addField("app.voucher.thoiHanApDung", this.thoiHanPicker);
    this.addField("app.voucher.trangThaiKichHoat", this.chkTrangThai);
  }

  public void napDuLieu(VoucherFormData data) {
    this.txtMa.setText(data.maVoucher() == null ? "" : data.maVoucher());
    this.txtTenChuongTrinh.setText(data.tenChuongTrinh() == null ? "" : data.tenChuongTrinh());
    this.txaMoTa.setText(data.moTa() == null ? "" : data.moTa());
    this.txtMucGiam.setText(data.mucGiam() == null ? "" : String.valueOf(data.mucGiam()));

    if (data.thoiHanApDung() == null) {
      this.thoiHanPicker.clear();
    } else {
      this.thoiHanPicker.setDateTimeStrict(data.thoiHanApDung());
    }

    this.chkTrangThai.setSelected(Boolean.TRUE.equals(data.trangThaiKichHoat()));
  }

  public VoucherFormData layDuLieu() {
    String ma = this.txtMa.getText().trim();
    String moTa = this.txaMoTa.getText().trim();

    return new VoucherFormData(
            ma.isBlank() ? null : ma,
            this.txtTenChuongTrinh.getText().trim(),
            moTa.isEmpty() ? null : moTa,
            NumberParseUtil.toDoubleOrNull(this.txtMucGiam.getText()),
            this.thoiHanPicker.getDateTimeStrict(),
            this.chkTrangThai.isSelected());
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
    // Chỉ kiểm tra hạn dùng khi tạo mới để không chặn việc sửa voucher đã tạo.
    if (!this.tao) {
      return null;
    }

    LocalDateTime thoiHan = this.thoiHanPicker.getDateTimeStrict();

    if (thoiHan == null) {
      return null;
    }

    if (!thoiHan.isAfter(LocalDateTime.now())) {
      FormValidationHelper.markError(this.thoiHanPicker,
              I18nReaderUtil.get("app.voucher.loiThoiHan"));
      return this.thoiHanPicker;
    }

    return null;
  }

  protected JComponent kiemTraChung() {
    JComponent loiDauTien = null;

    String ten = this.txtTenChuongTrinh.getText().trim();

    if (ten.isEmpty()) {
      FormValidationHelper.markError(this.txtTenChuongTrinh,
              I18nReaderUtil.get("app.voucher.loiTen"));
      loiDauTien = this.txtTenChuongTrinh;
    } else if (ten.length() > DO_DAI_TEN_TOI_DA) {
      FormValidationHelper.markError(this.txtTenChuongTrinh,
              I18nReaderUtil.get("app.voucher.tenQuaDai"));
      loiDauTien = this.txtTenChuongTrinh;
    } else {
      FormValidationHelper.clearError(this.txtTenChuongTrinh);
    }

    if (this.txaMoTa.getText().trim().length() > DO_DAI_MO_TA_TOI_DA) {
      FormValidationHelper.markError(this.txaMoTa,
              I18nReaderUtil.get("app.voucher.moTaQuaDai"));
      if (loiDauTien == null) {
        loiDauTien = this.txaMoTa;
      }
    } else {
      FormValidationHelper.clearError(this.txaMoTa);
    }

    Double mucGiam = NumberParseUtil.toDoubleOrNull(this.txtMucGiam.getText());

    if (mucGiam == null || !Double.isFinite(mucGiam) || mucGiam < 0 || mucGiam > 1) {
      FormValidationHelper.markError(this.txtMucGiam,
              I18nReaderUtil.get("app.voucher.loiMucGiam"));
      if (loiDauTien == null) {
        loiDauTien = this.txtMucGiam;
      }
    } else {
      FormValidationHelper.clearError(this.txtMucGiam);
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
