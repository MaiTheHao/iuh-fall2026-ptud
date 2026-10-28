package iuh.fit.gui.form;

import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComponent;

import java.time.LocalDateTime;

/**
 * Form tạo mới voucher: hạn áp dụng phải ở tương lai.
 */
public final class VoucherTaoFormPanel extends VoucherFormPanel {

  private static final long serialVersionUID = 1L;

  public VoucherTaoFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.voucher.tenChuongTrinh", this.tenChuongTrinhField);
    this.addField("app.voucher.moTa", this.moTaScroll);
    this.addField("app.voucher.mucGiam", this.mucGiamField);
    this.addBlock(taoGoiY("app.voucher.huongDanMucGiam"));
    this.addField("app.voucher.thoiHanApDung", this.thoiHanPicker);
    this.addField("app.voucher.trangThaiKichHoat", this.trangThaiField);
  }

  @Override
  protected JComponent kiemTraRieng() {
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
}
