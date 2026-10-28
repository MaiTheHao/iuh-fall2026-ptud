package iuh.fit.gui.form;

/**
 * Form cập nhật voucher: hiển thị thêm mã ở dạng chỉ đọc.
 */
public final class VoucherSuaFormPanel extends VoucherFormPanel {

  private static final long serialVersionUID = 1L;

  public VoucherSuaFormPanel() {
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.voucher.ma", this.maField);
    this.addField("app.voucher.tenChuongTrinh", this.tenChuongTrinhField);
    this.addField("app.voucher.moTa", this.moTaScroll);
    this.addField("app.voucher.mucGiam", this.mucGiamField);
    this.addBlock(taoGoiY("app.voucher.huongDanMucGiam"));
    this.addField("app.voucher.thoiHanApDung", this.thoiHanPicker);
    this.addField("app.voucher.trangThaiKichHoat", this.trangThaiField);
  }
}
